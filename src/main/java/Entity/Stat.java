package Entity;

public class Stat {

    private String name;
    private Integer baseStat;

    public Stat() {
    }

    public Stat(String name, Integer baseStat) {
        this.name = name;
        this.baseStat = baseStat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBaseStat() {
        return baseStat;
    }

    public void setBaseStat(Integer baseStat) {
        this.baseStat = baseStat;
    }
}
