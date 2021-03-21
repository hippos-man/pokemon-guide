package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Pokemon;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CacheService {

    public final ObjectMapper objectMapper = new ObjectMapper();

    public List<Pokemon> retrieveCache(File textFile) throws RuntimeException, IOException {
        if(textFile == null) {
            throw new RuntimeException();
        }
        List<Pokemon> cachedData = Arrays.asList(objectMapper.readValue(new FileInputStream(textFile), Pokemon[].class));
        return new ArrayList<>(cachedData);
    }

    public void saveCache(File textFile, List<Pokemon> data) throws IOException {
        objectMapper.writeValue(textFile, data);
    }

}
