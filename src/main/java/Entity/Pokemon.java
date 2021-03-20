package Entity;

import java.util.List;

public class Pokemon {

    private String id;
    private String name;
    private List<String> types; // e.g. ["ground", "rock"]
    private List<EncounterCondition> encounterConditions; // e.g. [{name: "xxxxx", method: "walk"},]
    private List<Stat> stats; // e.g. [{name: "attack", baseStat: 55}]
    private String cachedDate;

    public Pokemon() {
    }

    public Pokemon(String id, String name, List<String> types, List<EncounterCondition> encounterConditions,
                   List<Stat> stats, String cachedDate) {
        this.id = id;
        this.name = name;
        this.types = types;
        this.encounterConditions = encounterConditions;
        this.stats = stats;
        this.cachedDate = cachedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<EncounterCondition> getEncounterConditions() {
        return encounterConditions;
    }

    public void setEncounterConditions(List<EncounterCondition> encounterConditions) {
        this.encounterConditions = encounterConditions;
    }

    public List<Stat> getStats() {
        return stats;
    }

    public void setStats(List<Stat> stats) {
        this.stats = stats;
    }

    public String getCachedDate() {
        return cachedDate;
    }

    public void setCachedDate(String cachedDate) {
        this.cachedDate = cachedDate;
    }
}
