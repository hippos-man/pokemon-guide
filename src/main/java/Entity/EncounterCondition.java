package Entity;

import java.util.List;

public class EncounterCondition {

    private String locationName;
    private List<String> methodNames;

    public EncounterCondition() {
    }

    public EncounterCondition(String locationName, List<String> methodNames) {
        this.locationName = locationName;
        this.methodNames = methodNames;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public List<String> getMethodNames() {
        return methodNames;
    }

    public void setMethodNames(List<String> methodNames) {
        this.methodNames = methodNames;
    }
}
