/**
 * מייצג תשובה של שירות המפות
 */

package com.example.mytaxiproject.maps;

public class MapsResponse {
    private Route[] routes;

    public MapsResponse() {}

    public MapsResponse(Route[] routes) {
        this.routes = routes;
    }

    public void setRoutes(Route[] routes) {
        this.routes = routes;
    }

    public Route[] getRoutes() {
        return this.routes;
    }

    // סכום מרחק של כל המקטעים
    public int getTotalMeters() {
        int result = 0;
        for (Route route : this.routes) {
            result += route.getDistanceMeters();
        }
        return result;
    }

    // סכום זמן נסיעה של כל המקטעים
    public int getTotalSeconds() {
        int result = 0;
        for (Route route : this.routes) {
            result += route.getDurationSeconds();
        }
        return result;
    }
}
