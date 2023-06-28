/**
 * אקטיביטי סטטוס הזמנות
 */

package com.example.mytaxiproject;

import static com.example.mytaxiproject.firebase.FBRef.refOrders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
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
    BatteryLevelReceiver batteryLevelReceiver; // מאזין מצב סוללה

    Toolbar Toolbar3; // תפריט ראשי

    Button PayButton1; // כפתור תשלום

    ImageButton SearchButton2; // כפתור חיפוש 2
    ImageButton OrderStatusButton2; // כפתור תפריט 2

    ListView ordersView; // רשימת הזמנות
    ArrayList<String> ordersList = new ArrayList<>(); // ערכים של רשימת הזמנות
    ArrayList<Order> ordersValues = new ArrayList<>(); // עצמים של רשימת הזמנות
    OrdersArrayAdapter ordersAdapter; // מקשר בין רשמיה לערכים
    ValueEventListener ordersListener; // מאזין לשינויים בבסיס נתונים
    int orderIndex = -1; // הזמנה שנבחרה מהרשימה, אם לא נבחר הערך יהיה -1

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        batteryLevelReceiver = new BatteryLevelReceiver();

        // תפריט ראשי
        Toolbar3 = findViewById(R.id.Toolbar3);
        setSupportActionBar(Toolbar3);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        PayButton1 = findViewById(R.id.PayButton1);
        SearchButton2 = findViewById(R.id.SearchButton2);
        OrderStatusButton2 = findViewById(R.id.OrderStatusButton2);

        // חיבור בפועל של רשימה לתצוגה
        ordersView = findViewById(R.id.ordersView);
        ordersAdapter = new OrdersArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, ordersList, ordersValues);
        ordersView.setAdapter(ordersAdapter);
        ordersView.setOnItemClickListener(this);

        // יצירת מאזין רציף לבסיס נתונים
        ordersListener = new ValueEventListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ordersList.clear(); // נקה רשימת ערכים
                ordersValues.clear(); // נקה רשימת עצמים

                for (DataSnapshot data: snapshot.getChildren()) {
                    Order valuesItem = data.getValue(Order.class); // הכנס הזמנה לעצם
                    ordersValues.add(valuesItem); // הוסף עצם של הזמנה לרשימת העצמים

                    // בניית שורה שתוצג ברשימה
                    assert valuesItem != null;
                    String listItem = "Station: " + valuesItem.getStationName() + "\n";
                    listItem += "Price: " + String.format("%.2f", valuesItem.getTotalPrice()) + " ILS\n";
                    listItem += "Status: " + (valuesItem.isOrderPaid() ? "PAID" : "NOT PAID") + "\n";

                    // Timestamp > Date and Time
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    Instant instant = Instant.ofEpochSecond(valuesItem.getOrderTimestamp());
                    ZonedDateTime datetime = ZonedDateTime.ofInstant(instant, ZoneOffset.systemDefault());
                    listItem += "Order date: " + datetime.format(formatter) + "\n";

                    ordersList.add(listItem);
                }

                orderIndex = -1; // אפס את האינדקס
                ordersAdapter.notifyDataSetChanged(); // עדכן את התצוגה שהנתונים השתנו
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // שגיאה בקריאת נתונים
            }
        };

        String userId = sharedPreferences.getString("yourPhoneNumber", ""); // שלוף מזהה משתמש - מספר טלפון

        // חבר מאזין רציף להזמנות וסנן לפי מזהה משתמש
        refOrders.orderByChild("userId").equalTo(userId).addValueEventListener(ordersListener);
    }

    // אירוע תחילת העבודה
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_LOW);
        registerReceiver(batteryLevelReceiver, intentFilter);
    }

    // אירוע הפסקת העבודה
    @Override
    protected void onPause() {
        if (ordersListener != null) {
            refOrders.removeEventListener(ordersListener); // ביטול מאזין של בסיס נתונים
        }
        unregisterReceiver(batteryLevelReceiver);
        super.onPause();
    }

    // יצירת תפריט
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    // פעולות של התפריט
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        // יצירת תיבת דיאלוג
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("האם אתה בטוח?");
        builder.setNegativeButton("לא", (dialog, which) -> {dialog.cancel();});

        if (itemId == R.id.menuItem11) {
            // מעבר לאקטיביטי פרופיל משתמש
            gotoOpeningActivity();
        } else if (itemId == R.id.menuItem12) {
            // מחיקת פרטי כרטיס אשראי
            builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    myEdit.remove("yourCreditCard").apply();
                    myEdit.remove("yourCardValidity").apply();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else if (itemId == R.id.menuItem13) {
            // מחיקת כל הפרטים
            builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
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

    // מאזין בחירת פריט ברשימה
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        orderIndex = i;
    }

    // מאזין ללחיצת כפתור תשלום
    public void onPayButton1Click(View view) {
        if (orderIndex >= 0) { // בדיקה שנבחר פריט ברשימה
            if (!ordersValues.get(orderIndex).isOrderPaid()) { // בדיקה אם הזמנה לא שולמה
                Intent intent = new Intent(this, PaymentActivity.class);
                intent.putExtra("orderId", ordersValues.get(orderIndex).getOid());
                startActivity(intent);
            } else {
                Toast.makeText(this, "ההזמנה כבר שולמה :)", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "יש לבחור הזמנה", Toast.LENGTH_SHORT).show();
        }
    }

    // מאזין ללחיצת כפתור חיפוש
    public void onSearchButton2Click(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    // מאזין ללחיצת כפתור סטטוס הזמנות
    public void onOrderStatusButton2Click(View view) {
        Toast.makeText(this, "אתה נמצא במסך סטטוס הזמנות", Toast.LENGTH_SHORT).show();
    }

    // מעבר לאקטיביטי פרופיל משתמש
    public void gotoOpeningActivity() {
        Intent intent = new Intent(this, OpeningActivity.class);
        startActivity(intent);
    }
}
