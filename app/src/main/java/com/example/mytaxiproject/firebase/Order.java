package com.example.mytaxiproject.firebase;

import java.time.Instant;

public class Order {
    private String oid;             // Order ID - May be userId + orderDateTime
    private String cid;             // Confirmation ID - Payment confirmation id ("0" if not paid)
    private String userId;          // User ID - Customer phone number
    private String stationId;       // Station ID
    private String stationName;     // Station Name
    private double orderPrice;      // Order Price - One-time fee
    private double kmPrice;         // Kilometer Price
    private double rideDistance;    // Ride Distance - In km
    private double totalPrice;      // Total Price = orderPrice + kmPrice * rideDistance
    private boolean orderPaid;      // Order Payment Status - FALSE (default) if not paid, TRUE if paid
    private long orderTimestamp;    // Order DateTime - Order creation timestamp
    private long paymentTimestamp;  // Payment DateTime - Payment timestamp (0 if not paid)

    public Order() {
        // Default constructor required for calls to DataSnapshot.getValue(Order.class)
    }

    public Order(String userId, Station station, double rideDistance) {
        Instant instant = Instant.now();
        long timestamp = instant.getEpochSecond();

        this.oid = "OID" + userId + timestamp;
        this.cid = "0";
        this.userId = userId;
        this.stationId = station.getId();
        this.stationName = station.getName();
        this.orderPrice = station.getOrderPrice();
        this.kmPrice = station.getKmPrice();
        this.rideDistance = rideDistance;
        this.totalPrice = this.orderPrice + this.kmPrice * this.rideDistance;
        this.orderPaid = false;
        this.orderTimestamp = timestamp;
        this.paymentTimestamp = 0;
    }

    public void updatePaid(String cid, long paymentTimestamp) {
        this.cid = cid;
        this.orderPaid = true;
        this.paymentTimestamp = paymentTimestamp;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
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

    public double getRideDistance() {
        return rideDistance;
    }

    public void setRideDistance(double rideDistance) {
        this.rideDistance = rideDistance;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isOrderPaid() {
        return orderPaid;
    }

    public void setOrderPaid(boolean orderPaid) {
        this.orderPaid = orderPaid;
    }

    public long getOrderTimestamp() {
        return orderTimestamp;
    }

    public void setOrderTimestamp(long orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }

    public long getPaymentTimestamp() {
        return paymentTimestamp;
    }

    public void setPaymentTimestamp(long paymentTimestamp) {
        this.paymentTimestamp = paymentTimestamp;
    }
}
