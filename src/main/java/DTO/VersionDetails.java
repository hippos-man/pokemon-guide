package DTO;

import java.util.List;

public class VersionDetails {

    List<EncounterDetails> encounter_details;

    public List<EncounterDetails> getEncounter_details() {
        return encounter_details;
    }

    public void setEncounter_details(List<EncounterDetails> encounter_details) {
        this.encounter_details = encounter_details;
    }
}
