import entity.EncounterCondition;
import entity.Pokemon;
import entity.Stat;
import dto.*;
import service.CacheService;
import service.PokemonService;

import java.io.*;
import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        init();
        Boolean isEnabled = true;
        LocalDate now = LocalDate.now();
        String formattedDate = now.toString();
        String input = "";
        CacheService cacheService = new CacheService();
        PokemonService pokemonService = new PokemonService();

        while (isEnabled) {

            System.out.println("Please type Pokemon Name or ID to search!");
            System.out.print(">>> ");

            try {
                input = readInput();
            } catch (IOException ex) {
                System.out.println("Invalid input. Try again!");
                System.out.println("========================================================================================");
                System.out.println("");
                continue;
            }

            System.out.println("You typed \"" + input + "\"");

            if(input.equals("exit") || input.equals("quit")){
                break;
            }

            if (input.equals("")) {
                printError("Type something!");
                continue;
            }

            System.out.println("");
            System.out.println("Searching...");
            System.out.println("");

            Pokemon cachedPokemon = null;

            // Read Cache from external text file
            String cacheFileLocation = getFilePath("prod");
            File textFile = new File(cacheFileLocation);
            List<Pokemon> copiedCachedData = cacheService.retrieveCache(textFile);


            // Find Pokemon from cache.
            Pokemon target = cacheService.getCachedPokemon(input, copiedCachedData);

            // Check if it's available (not older than a week)
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

            // Call API if not found from cache.
            if (cachedPokemon == null) {

                try {

                    PokemonResponse pokemonResponse = pokemonService.fetchPokemonInfo(input);
                    retrievedPokemon = pokemonService.retrievePokemon(pokemonResponse, formattedDate);

                } catch (IOException ex) {
                    printError("Not found! Try again!");
                    continue;
                }

                copiedCachedData.add(retrievedPokemon);
                // Update Cache in the text file.
                cacheService.saveCache(textFile, copiedCachedData);
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

    public static String getFilePath(String env) {
        String cachePath = "";

        if(env.equals("local")) {
            cachePath = Main.class.getProtectionDomain().getCodeSource().getLocation().toString()
                    .split("/build")[0].split("file:")[1] + "/cache/cache.txt";
        } else if(env.equals("prod")){
            cachePath = Main.class.getProtectionDomain().getCodeSource().getLocation().toString()
                    .split("/pokemon-finder-1.0-SNAPSHOT.jar")[0].split("file:")[1] + "/cache/cache.txt";
        }
        return cachePath;
    }

    public static Boolean isOlderThanAWeek(Pokemon cache, LocalDate now) {
        LocalDate cachedDate = LocalDate.parse(cache.getCachedDate());
        LocalDate oneWeekAgo = now.minusDays(7);
        return cachedDate.isBefore(oneWeekAgo);
    }

    public static void printError(String message) {
        System.out.println(message);
        System.out.println("========================================================================================");
        System.out.println("");
    }

    public static void printResult(Pokemon result) {
        System.out.println("========================================================================================");
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
