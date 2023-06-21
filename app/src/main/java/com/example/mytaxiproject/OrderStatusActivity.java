package com.example.mytaxiproject;

import static com.example.mytaxiproject.firebase.FBRef.refOrders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mytaxiproject.firebase.Order;
import com.example.mytaxiproject.firebase.OrdersArrayAdapter;
import com.example.mytaxiproject.receivers.BatteryLevelReceiver;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class OrderStatusActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    BatteryLevelReceiver batteryLevelReceiver;

    Toolbar Toolbar3;

    Button PayButton1; //כפתור תשלום

    ImageButton SearchButton2; //כפתור בית 2
    ImageButton OrderStatusButton2; //כפתור תפריט 2

    ListView ordersView;
    ArrayList<String> ordersList = new ArrayList<>();
    ArrayList<Order> ordersValues = new ArrayList<>();
    OrdersArrayAdapter ordersAdapter;
    ValueEventListener ordersListener; // מאזין לשינויים בבסיס נתונים
    int orderIndex = -1;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        batteryLevelReceiver = new BatteryLevelReceiver();

        Toolbar3 = findViewById(R.id.Toolbar3);
        setSupportActionBar(Toolbar3);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        PayButton1 = findViewById(R.id.PayButton1);
        SearchButton2 = findViewById(R.id.SearchButton2);
        OrderStatusButton2 = findViewById(R.id.OrderStatusButton2);

        ordersView = findViewById(R.id.ordersView);
        ordersAdapter = new OrdersArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, ordersList, ordersValues);
        ordersView.setAdapter(ordersAdapter);
        ordersView.setOnItemClickListener(this);

        ordersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ordersList.clear();
                ordersValues.clear();

                for (DataSnapshot data: snapshot.getChildren()) {
                    Order valuesItem = data.getValue(Order.class);
                    ordersValues.add(valuesItem);

                    assert valuesItem != null;
                    String listItem = data.getKey() + "\n";
                    listItem += "Status: " + (valuesItem.isOrderPaid() ? "PAID" : "NOT PAID") + "\n";

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    Instant instant = Instant.ofEpochSecond(valuesItem.getOrderTimestamp());
                    ZonedDateTime datetime = ZonedDateTime.ofInstant(instant, ZoneOffset.systemDefault());
                    listItem += datetime.format(formatter) + "\n";

                    ordersList.add(listItem);
                }

                orderIndex = -1;
                ordersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO: Handle.
            }
        };

        String userId = sharedPreferences.getString("yourPhoneNumber", "");
        refOrders.orderByChild("userId").equalTo(userId).addValueEventListener(ordersListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_LOW);
        registerReceiver(batteryLevelReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        if (ordersListener != null) {
            refOrders.removeEventListener(ordersListener);
        }
        unregisterReceiver(batteryLevelReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        // Alert dialog
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setNegativeButton("No", (dialog, which) -> {dialog.cancel();});

        if (itemId == R.id.menuItem11) {
            // Go to user profile activity
            gotoOpeningActivity();
        } else if (itemId == R.id.menuItem12) {
            // Delete credit card info
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    myEdit.remove("yourCreditCard").apply();
                    myEdit.remove("yourCardValidity").apply();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else if (itemId == R.id.menuItem13) {
            // Delete all
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    myEdit.clear().apply();
                    gotoOpeningActivity();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        orderIndex = i;
    }

    public void onPayButton1Click(View view) {
        if (orderIndex >= 0) {
            if (!ordersValues.get(orderIndex).isOrderPaid()) {
                Intent intent = new Intent(this, PaymentActivity.class);
                intent.putExtra("orderId", ordersValues.get(orderIndex).getOid());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Order already paid :)", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No order selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSearchButton2Click(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void onOrderStatusButton2Click(View view) {
        Toast.makeText(this, "You are now in Order Status screen", Toast.LENGTH_SHORT).show();
    }

    public void gotoOpeningActivity() {
        Intent intent = new Intent(this, OpeningActivity.class);
        startActivity(intent);
    }
}
