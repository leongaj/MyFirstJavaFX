package com.example.adapt;

public class Tasking {

    private String CapabilityType;
    private String Date_short;
    private String Time;
    private String Location;
    private int Priority;
    private int Revisit_Hours;

    public Tasking(String Location, int Priority, int Revisit_Hours, String CapabilityType, String Date_short, String Time) {
        this.Location = Location;
        this.CapabilityType = CapabilityType;
        this.Date_short = Date_short;
        this.Time = Time;
        this.Priority = Priority;
        this.Revisit_Hours = Revisit_Hours;
    }

    public String getLocation() {
        return this.Location;
    }

    public String getCapabilityType() {
        return this.CapabilityType;
    }

    public String getDate_short() {
        return this.Date_short;
    }

    public String getTime() {
        return this.Time;
    }

    public int getPriority() {
        return this.Priority;
    }

    public int getRevisit_Hours() {
        return this.Revisit_Hours;
    }
}
