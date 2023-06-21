package com.example.mytaxiproject.maps;

public class Route {
    private int distanceMeters;
    private String duration;

    public void setDistanceMeters(int distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public int getDistanceMeters() {
        return this.distanceMeters;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDuration() {
        return this.duration;
    }

    public int getDurationSeconds() {
        if (
                this.duration != null &&
                this.duration.length() > 0 &&
                this.duration.charAt(this.duration.length() - 1) == 's'
        ) {
            return Integer.parseInt(this.duration.substring(0, this.duration.length() - 1));
        }
        return 0;
    }
}
