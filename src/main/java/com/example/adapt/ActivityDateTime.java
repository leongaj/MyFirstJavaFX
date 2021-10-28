package com.example.adapt;

public class ActivityDateTime {

    private String Date;
    private String CapabilityA;
    private String CapabilityB;
    private String CapabilityC;
    private String CapabilityD;

    public ActivityDateTime (String date,
                             String activity_time_A, String activity_time_B,
                             String activity_time_C, String activity_time_D) {

        this.Date = date;
        if (date.split("-")[0].length() == 1) {
            this.Date = "0"+date;
        }
        this.CapabilityA = activity_time_A;
        this.CapabilityB = activity_time_B;
        this.CapabilityC = activity_time_C;
        this.CapabilityD = activity_time_D;
    }

    public String getDate() {
        return this.Date;
    }

    public String getCapabilityA() {
        return this.CapabilityA;
    }

    public String getCapabilityB() {
        return this.CapabilityB;
    }

    public String getCapabilityC() {
        return this.CapabilityC;
    }

    public String getCapabilityD() {
        return this.CapabilityD;
    }

}
