/**
 * הפניות לבסיס הנתונים
 */

package com.example.mytaxiproject.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FBRef {
    public static String rootStations = "Stations";
    public static String rootOrders = "Orders";

    public static FirebaseDatabase db = FirebaseDatabase.getInstance();

    public static DatabaseReference refStations = db.getReference(rootStations);
    public static DatabaseReference refOrders = db.getReference(rootOrders);
}
