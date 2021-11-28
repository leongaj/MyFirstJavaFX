package com.example.adapt;

public class Location {

    private String Location_Name;
    private int Location_Revisit_Rate;
    private Boolean CapabilityA;
    private Boolean CapabilityB;
    private Boolean CapabilityC;
    private Boolean CapabilityD;

    public Location(String location_name, String loc_revisit,
                    String loc_cap_A, String loc_cap_B, String loc_cap_C, String loc_cap_D) {
        this.Location_Name = location_name;
        this.Location_Revisit_Rate = Integer.parseInt(loc_revisit);
        this.CapabilityA = check_boolean_input(loc_cap_A);
        this.CapabilityB = check_boolean_input(loc_cap_B);
        this.CapabilityC = check_boolean_input(loc_cap_C);
        this.CapabilityD = check_boolean_input(loc_cap_D);
    }

    public boolean check_boolean_input(String input) {
        if (input == null) {
            return false;
        }

        if(input.strip().equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    public String getLocation() {
        return this.Location_Name;
    }

    public int getRevisit() {return this.Location_Revisit_Rate; }

    public Boolean getCapabilityA() {
        return this.CapabilityA;
    }

    public Boolean getCapabilityB() {
        return this.CapabilityB;
    }

    public Boolean getCapabilityC() {
        return this.CapabilityC;
    }

    public Boolean getCapabilityD() {
        return this.CapabilityD;
    }

}
