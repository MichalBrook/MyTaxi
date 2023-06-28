/**
 * Custom Orders ArrayAdapter
 * צובע את ההזמנה בהתאם לסטטוס: משולמת או לא משולמת
 */

package com.example.mytaxiproject.firebase;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class OrdersArrayAdapter extends ArrayAdapter<String> {
    private final ArrayList<Order> objects;

    private String alpha = "33"; // 20% transparency
    private String red = "FF0000"; // Red color
    private String green = "00FF00"; // Green color

    public OrdersArrayAdapter(Context context, int resource, ArrayList<String> items, ArrayList<Order> objects) {
        super(context, resource, items);
        this.objects = objects; // שמור רשימת העצמים
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View updView = super.getView(position, convertView, parent); // קבל את השורה לתצוגה מסופר
        String color = "#" + alpha + (objects.get(position).isOrderPaid() ? green : red); // בניית צבע בהתאם לסטטוס הזמנה
        updView.setBackgroundColor(Color.parseColor(color)); // עדכון צבע של השורה
        return updView; // החזר שורה
    }
}
