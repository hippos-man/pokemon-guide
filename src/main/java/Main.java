import Entity.EncounterCondition;
import Entity.Pokemon;
import Entity.Stat;
import DTO.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException, ParseException {

        System.out.println("--------------------------------------------");
        System.out.println("Welcome to Pokemon Finder!!");
        System.out.println("");

        Boolean isEnabled = true;
        LocalDate now = LocalDate.now();
        String formattedDate = now.toString();

        while (isEnabled) {

            System.out.println("Please type Pokemon Name or ID to search!");
            System.out.print(">>> ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine().toLowerCase();
            System.out.println("You typed: " + input);
            System.out.println("");

            if(input.equals("exit") || input.equals("quit")){
                break;
            }
            Pokemon cachedPokemon = null;

            // Retrieve Cache data
            List<Pokemon> cachedData = Arrays.asList(objectMapper.readValue(Paths.get("cache.txt").toFile(), Pokemon[].class));
            List<Pokemon> copiedCachedData = new ArrayList<>(cachedData);

            // If find the Pokemon & not older than a week old, use cache.
            for (Pokemon pokemon : copiedCachedData) {
                // find the Pokemon by name & id
                if(pokemon.getName().equals(input) || pokemon.getId().equals(input)) {
                    // check if the cached date is older than a week old.
                    LocalDate cachedDate = LocalDate.parse(pokemon.getCachedDate());
                    LocalDate oneWeekAgo = now.minusDays(7);
                    if (cachedDate.isBefore(oneWeekAgo)) {
                        // Delete old cache
                        copiedCachedData.remove(pokemon);
                        break;
                    } else {
                        cachedPokemon = pokemon;
                        break;
                    }
                }
            }


            Pokemon retrievedPokemon = null;
            // If not found in cache
            if (cachedPokemon == null) {

                // Fetch the Pokemon data from API
                URL pokemonUrl = new URL("https://pokeapi.co/api/v2/pokemon/" + input + "/");
                System.out.println("Requested URL: " + pokemonUrl);
                HttpURLConnection basicConnection = (HttpURLConnection) pokemonUrl.openConnection();
                basicConnection.setRequestProperty("Accept", "application/json");
                basicConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
                InputStream pokemonMainStream = basicConnection.getInputStream();

                // Convert Pokemon Response Json to Java object
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                PokemonResponse pokemonResponse = objectMapper.readValue(pokemonMainStream, PokemonResponse.class);

                // Convert Pokemon Json to Pokemon Object (name, id, types, stats)
                String id = pokemonResponse.getId();
                String name = pokemonResponse.getName();
                List<String> types = new ArrayList<>();
                for (Types type : pokemonResponse.getTypes()) {
                    types.add(type.getType().getName());
                }

                List<Stat> stats = new ArrayList<>();
                for (Stats statsResponse : pokemonResponse.getStats()) {
                    stats.add(new Stat(statsResponse.getStat().getName(), Integer.parseInt(statsResponse.getBase_stat())));
                }

                // Fetch the Location and Methods data from API
                URL locationUrl = new URL("https://pokeapi.co/api/v2/pokemon/" + id + "/encounters");
                System.out.println("Requested URL: " + locationUrl);
                HttpURLConnection locationConnection = (HttpURLConnection) locationUrl.openConnection();
                locationConnection.setRequestProperty("Accept", "application/json");
                locationConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
                InputStream locationStream = locationConnection.getInputStream();

                // Convert Location response Json to Java object (location, method)
                Encounter[] encounters = objectMapper.readValue(locationStream, Encounter[].class);
                // Encounter condition
                List<EncounterCondition> encounterConditionList = new ArrayList<>();
                for (Encounter encounter: encounters) {
                    String locationName = encounter.getLocation_area().getName();
                    // Add only location in Kanto.
                    if(!locationName.startsWith("kanto-")) {
                        continue;
                    }
                    List<String> methods = new ArrayList<>();
                    for (VersionDetails versionDetails : encounter.getVersion_details()) {
                        for (EncounterDetails encounterDetails : versionDetails.getEncounter_details()) {
                            // if already the list has same method, don't add it.
                            String method = encounterDetails.getMethod().getName();
                            if(!methods.contains(method)) {
                                methods.add(encounterDetails.getMethod().getName());
                            }
                        }
                    }
                    encounterConditionList.add(new EncounterCondition(locationName, methods));
                }
                // TODO Save the entity into text file
                // Create Instance of the Pokemon
                retrievedPokemon = new Pokemon(id, name, types, encounterConditionList, stats, formattedDate);
                copiedCachedData.add(retrievedPokemon);
                objectMapper.writeValue(Paths.get("cache.txt").toFile(), copiedCachedData);
            }

            Pokemon result = cachedPokemon != null ? cachedPokemon : retrievedPokemon;

            // Print out the result
            System.out.println("Here's the result!!");
            System.out.println("========================================================================================");
            System.out.println("Result:");
            System.out.println("    <Pokemon ID>");
            System.out.println("    -> " + result.getId());
            System.out.println("");
            System.out.println("    <Pokemon Name>");
            System.out.println("    -> " + result.getName());
            System.out.println("");
            System.out.println("    <Pokemon Types>");
            System.out.println("    -> " + result.getTypes());
            System.out.println("");
            System.out.println("    <Pokemon Encounter Locations and methods in Kanto> ");
            if(result.getEncounterConditions().size() == 0) {
                System.out.println("    -> " + "-");
            } else {
                for (EncounterCondition encounterCondition : result.getEncounterConditions()) {
                    System.out.println("    -> " + encounterCondition.getLocationName() + "(method: " + encounterCondition.getMethodNames() + ")");
                }
            }
            System.out.println("");
            System.out.println("    <Pokemon stats>");
            for (Stat stat: result.getStats()) {
                System.out.println("    -> " + stat.getName() + ": " + stat.getBaseStat());
            }
            System.out.println("========================================================================================");
            System.out.println("");

        } // End while



        System.out.println("Bye!");



    }

    public static void printResult(Pokemon pokemon) {
        // TODO
    }
}
