package dto;

import java.util.List;

public class Encounter {

    private LocationArea location_area;
    private List<VersionDetails> version_details;

    public LocationArea getLocation_area() {
        return location_area;
    }

    public void setLocation_area(LocationArea location_area) {
        this.location_area = location_area;
    }

    public List<VersionDetails> getVersion_details() {
        return version_details;
    }

    public void setVersion_details(List<VersionDetails> version_details) {
        this.version_details = version_details;
    }
}
