/**
 * מייצג פניה לשירות המפות
 */

package com.example.mytaxiproject.maps;

public class MapsRequest {
    private Address origin;
    private Address destination;

    public MapsRequest() {}

    public MapsRequest(Address origin, Address destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public void setOrigin(Address origin) {
        this.origin = origin;
    }

    public Address getOrigin() {
        return this.origin;
    }

    public void setDestination(Address destination) {
        this.destination = destination;
    }

    public Address getDestination() {
        return this.destination;
    }
}
