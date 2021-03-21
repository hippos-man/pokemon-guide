package service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.*;
import entity.EncounterCondition;
import entity.Pokemon;
import entity.Stat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PokemonService {

    public final ObjectMapper objectMapper = new ObjectMapper();

    public Pokemon retrievePokemon (PokemonResponse pokemonResponse, String formattedDate) throws IOException{

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

        Encounter[] encounters = fetchEncounters(id);

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

        return new Pokemon(id, name, types, encounterConditionList, stats, formattedDate);
    }

    public PokemonResponse fetchPokemonInfo(String input) throws IOException {

        // Fetch the Pokemon data from API
        URL pokemonUrl = new URL("https://pokeapi.co/api/v2/pokemon/" + input + "/");
        HttpURLConnection basicConnection = (HttpURLConnection) pokemonUrl.openConnection();
        basicConnection.setRequestProperty("Accept", "application/json");
        basicConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        InputStream pokemonMainStream = basicConnection.getInputStream();

        // Convert Pokemon Response Json to Java object
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(pokemonMainStream, PokemonResponse.class);
    }

    public Encounter[] fetchEncounters(String id) throws IOException{
        URL locationUrl = new URL("https://pokeapi.co/api/v2/pokemon/" + id + "/encounters");
        HttpURLConnection locationConnection = (HttpURLConnection) locationUrl.openConnection();
        locationConnection.setRequestProperty("Accept", "application/json");
        locationConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        InputStream locationStream = locationConnection.getInputStream();

        // Convert Location response Json to Java object (location, method)
        return objectMapper.readValue(locationStream, Encounter[].class);
    }
}
