import entity.EncounterCondition;
import entity.Pokemon;
import entity.Stat;
import dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import service.PokemonService;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        init();
        Boolean isEnabled = true;
        LocalDate now = LocalDate.now();
        String formattedDate = now.toString();
        String input = "";

        while (isEnabled) {

            System.out.println("Please type Pokemon Name or ID to search!");
            System.out.print(">>> ");

            try {
                input = readInput();
            } catch (IOException ex) {
                System.out.println("Invalid input. Try again!");
                continue;
            }

            System.out.println("You typed \"" + input + "\"");
            System.out.println("");
            System.out.println("Searching...");
            System.out.println("");

            if(input.equals("exit") || input.equals("quit")){
                break;
            }
            Pokemon cachedPokemon = null;

            // Read Cache from external text file
            // For executable jar
//            String jarLocation = Main.class.getProtectionDomain().getCodeSource().getLocation().toString().split("/pokemon-finder-1.0-SNAPSHOT.jar")[0].split("file:")[1] + "/cache/cache.txt";
            // For local dev
            String cacheFileLocation = Main.class.getProtectionDomain().getCodeSource().getLocation().toString().split("/build")[0].split("file:")[1] + "/cache/cache.txt";

            File textFile = new File(cacheFileLocation);
            List<Pokemon> copiedCachedData = retrieveCache(textFile);


            // Find Pokemon from cache.
            // If find the Pokemon & not older than a week old, use cache.
            Pokemon target = getCachedPokemon(input, copiedCachedData, now);

            // Check if it's available
            if (target != null) {
                Boolean isOldCache = isOlderThanAWeek(target, now);
                if (isOldCache) {
                    // Delete exiting cache if it's old.
                    copiedCachedData.remove(target);
                } else {
                    cachedPokemon = target;
                }
            }

            Pokemon retrievedPokemon = null;

            // If not found in cache.
            if (cachedPokemon == null) {
                PokemonService pokemonService = new PokemonService();
                PokemonResponse pokemonResponse = pokemonService.fetchPokemonInfo(input);
                retrievedPokemon = pokemonService.retrievePokemon(pokemonResponse, formattedDate);
                copiedCachedData.add(retrievedPokemon);

                // TODO export method
                // Update Cache in the text file.
                objectMapper.writeValue(textFile, copiedCachedData);
            }

            Pokemon result = cachedPokemon != null ? cachedPokemon : retrievedPokemon;

            // Print out the result
            printResult(result);

        } // End while

        System.out.println("Bye!");

    }


    public static void init() {
        System.out.println("--------------------------------------------");
        System.out.println("Welcome to Pokemon App!!");
        System.out.println("");
    }

    public static String readInput() throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine().toLowerCase();
    }

    public static List<Pokemon> retrieveCache(File textFile) throws RuntimeException, IOException {
        if(textFile == null) {
            throw new RuntimeException();
        }
        List<Pokemon> cachedData = Arrays.asList(objectMapper.readValue(new FileInputStream(textFile), Pokemon[].class));
        return new ArrayList<>(cachedData);
    }

    public static Pokemon getCachedPokemon(String input, List<Pokemon> copiedCacheList, LocalDate now) {
        Pokemon target = null;
        for (Pokemon pokemon : copiedCacheList) {
            // find the Pokemon by name & id
            if(pokemon.getName().equals(input) || pokemon.getId().equals(input)) {
                target = pokemon;
            }
        }
        return target;
    }

    public static Boolean isOlderThanAWeek(Pokemon cache, LocalDate now) {
        LocalDate cachedDate = LocalDate.parse(cache.getCachedDate());
        LocalDate oneWeekAgo = now.minusDays(7);
        return cachedDate.isBefore(oneWeekAgo);
    }

    public static void printResult(Pokemon result) {
        System.out.println("We found it!!");
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
    }
}
