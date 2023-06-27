/**
 * מייצג תחנת מוניות
 */

package com.example.mytaxiproject.firebase;

public class Station {
    private String id;          // Station ID
    private String name;        // Station Name
    private double orderPrice;  // Order Price - One-time fee
    private double kmPrice;     // Kilometer Price

    public Station() {
        // Default constructor required for calls to DataSnapshot.getValue(Station.class)
    }

    public Station(
            String id,
            String name,
            double orderPrice,
            double kmPrice
    ) {
        this.id = id;
        this.name = name;
        this.orderPrice = orderPrice;
        this.kmPrice = kmPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public double getKmPrice() {
        return kmPrice;
    }

    public void setKmPrice(double kmPrice) {
        this.kmPrice = kmPrice;
    }
}
