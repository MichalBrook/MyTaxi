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
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View updView = super.getView(position, convertView, parent);
        String color = "#" + alpha + (objects.get(position).isOrderPaid() ? green : red);
        updView.setBackgroundColor(Color.parseColor(color));
        return updView;
    }
}
