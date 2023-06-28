/**
 * אקטיביטי חיפוש
 */

package com.example.mytaxiproject;

import static com.example.mytaxiproject.firebase.FBRef.refOrders;
import static com.example.mytaxiproject.firebase.FBRef.refStations;

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
import android.os.StrictMode;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytaxiproject.firebase.Order;
import com.example.mytaxiproject.firebase.Station;
import com.example.mytaxiproject.maps.Address;
import com.example.mytaxiproject.maps.MapsRequest;
import com.example.mytaxiproject.maps.MapsResponse;
import com.example.mytaxiproject.receivers.BatteryLevelReceiver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    // Allow HTTP request from main thread
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();

    BatteryLevelReceiver batteryLevelReceiver; // מאזין מצב סוללה

    Toolbar Toolbar1; // תפריט ראשי
    EditText OriginAddressText; // שדה להזנת כתובת המוצא
    EditText DestinationAddressText; // שדה להזנת כתובת היעד

    Button FindButton; // כפתור חיפוש
    Button ClearButton; // כפתור ניקוי
    Button BuyButton; // כפתור קניה

    ImageButton SearchButton1; // כפתור חיפוש 1
    ImageButton OrderStatusButton1; // כפתור תפריט 1

    TextView KilometerText1; // קילומטר (ק"מ) 1
    TextView TimeText1; // זמן נסיעה 1

    double distance = 0; // מרחק נסיעה
    int duration = 0; // זמן נסיעה

    ListView stationsView; // רשימת תחנות מוניות
    ArrayList<String> stationsList = new ArrayList<>(); // ערכים של רשימת תחנות מוניות
    ArrayList<Station> stationsValues = new ArrayList<>(); // עצמים של רשימת תחנות מוניות
    ArrayAdapter<String> stationsAdapter; // מקשר בין רשימה לערכים
    int stationIndex = -1; // תחנה שנבחרה מהרשימה, אם לא נבחר הערך יהיה -1

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Allow HTTP request from main thread
        StrictMode.setThreadPolicy(policy);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        batteryLevelReceiver = new BatteryLevelReceiver();

        // תפריט ראשי
        Toolbar1 = findViewById(R.id.Toolbar1);
        setSupportActionBar(Toolbar1);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        OriginAddressText = findViewById(R.id.OriginAddressText);
        DestinationAddressText = findViewById(R.id.DestinationAddressText);

        FindButton = findViewById(R.id.FindButton);
        ClearButton = findViewById(R.id.ClearButton);
        BuyButton = findViewById(R.id.BuyButton);

        SearchButton1 = findViewById(R.id.SearchButton1);
        OrderStatusButton1 = findViewById(R.id.OrderStatusButton1);

        KilometerText1 = findViewById(R.id.KilometerText1);
        TimeText1 = findViewById(R.id.TimeText1);

        // חיבור בפועל של רשימה לתצוגה
        stationsView = findViewById(R.id.stationsView);
        stationsAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, stationsList);
        stationsView.setAdapter(stationsAdapter);
        stationsView.setOnItemClickListener(this);
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

    // כפתור מצא
    public void onFindButtonClick(View view) {
        fetchDistance();
        fetchPrices();
    }

    // פונקציה שמכינה נתונים לפניה ומעבדת נתונים של התשובה של שירות המפות
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void fetchDistance() {
        String origin = String.valueOf(OriginAddressText.getText()); // שליפה של כתובת מוצא
        String destination = String.valueOf(DestinationAddressText.getText()); // שליפה של כתובת יעד
        MapsRequest mapsRequest = new MapsRequest(new Address(origin), new Address(destination)); // בניית אובייקט פניה לשירות

        try {
            MapsResponse mapsResponse = invokeMapsRequest(mapsRequest); // פניה לשירות וקבלת תשובה

            // המרת ערכים של התשובה
            distance = (double) mapsResponse.getTotalMeters() / 1000; // המרה לקילומטר
            duration = mapsResponse.getTotalSeconds() / 60; // המרה לדקות

            KilometerText1.setText(String.format("%.1f", distance) + " km"); // הצבה בשדות במסך
            TimeText1.setText(duration + " min"); // הצבה בשדות במסך
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show(); // מציג את ההודעת שגיאה
        }
    }

    // פונקציה ששולפת מפיירבייס את הרשומות של תחנות מוניות, מבצעת חישוב של מחיר ובונה רשימה לתצוגה
    @SuppressLint("DefaultLocale")
    public void fetchPrices() {
        // הכנת מאזין לקריאה חד פעמית מפיירבייס
        refStations.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stationsList.clear(); // נקה רשימת ערכים
                stationsValues.clear(); // נקה רשימת עצמים

                for (DataSnapshot data: snapshot.getChildren()) {
                    Station valuesItem = data.getValue(Station.class); // הכנס תחנה לעצם
                    stationsValues.add(valuesItem); // הוסף עצם של תחנה לרשימת העצמים

                    assert valuesItem != null; // לוודא
                    double totalPrice = valuesItem.getOrderPrice() + valuesItem.getKmPrice() * distance; // חישוב מחיר הנסיעה
                    // בניית שורה שתוצג ברשימה
                    String listItem =
                            "Name: " + valuesItem.getName() + "\n" +
                            "Price: " + String.format("%.2f", totalPrice);
                    stationsList.add(listItem);
                }

                stationsAdapter.notifyDataSetChanged(); // עדכן את התצוגה שהנתונים השתנו
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // שגיאה בקריאת נתונים
            }
        });
    }

    /**
     * פונקציה שמבצעת פניה לשירותי מפות ומחזירה את התשובה שהתקבלה
     * @param mapsRequest MapsRequest - MapsRequest object.
     * @return MapsResponse - MapsResponse object.
     * @throws IOException - Thrown by ObjectMapper, OkHttpClient.
     */
    public MapsResponse invokeMapsRequest(MapsRequest mapsRequest) throws IOException {
        String mask = "routes.duration,routes.distanceMeters"; // איזה נתונים להחזיר
        String type = "application/json"; // סוג התוכן

        ObjectMapper objectMapper = new ObjectMapper(); // JSON Parser
        String payload = objectMapper.writeValueAsString(mapsRequest);  // Object > JSON

        OkHttpClient client = new OkHttpClient(); // HTTP client - מי שפונה ומקבל תשובה
        RequestBody body = RequestBody.create(payload, MediaType.parse(type)); // בניית גוף הפניה
        Request request = new Request.Builder()
                .url(Secrets.mapsApiUrl) // כתובת פניה
                .post(body) // סוג פניה
                .addHeader("X-Goog-Api-Key", Secrets.mapsApiKey) // API key
                .addHeader("X-Goog-FieldMask", mask) // איזה נתונים להחזיר
                .addHeader("Accept", type) // איזה סוג תוכן להחזיר
                //.addHeader("Content-type", type) // Set in RequestBody.create()
                .build();

        try (Response response = client.newCall(request).execute()) { // בצע פניה וקבל תשובה
            if (response.body() != null) {
                String json = response.body().string(); // Get JSON
                return objectMapper.readValue(json, MapsResponse.class); // JSON > Object
            } else {
                return new MapsResponse(); // החזר עצם ריק
            }
        }
    }

    // כפתור ניקוי
    public void onClearButtonClick(View view) {
        OriginAddressText.setText(""); // אפס שדה להזנת כתובת מוצא
        DestinationAddressText.setText(""); // אפס שדה להזנת כתובת יעד
    }

    // מאזין בחירת פריט ברשימה
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        stationIndex = i;
    }

    // מאזין ללחיצת כפתור קניה
    public void onBuyButtonClick(View view) {
        String userId = sharedPreferences.getString("yourPhoneNumber", "");
        if (userId.length() > 0 && stationIndex >= 0 && distance > 0) { // אם שמור מספר טלפון
            Order order = new Order(userId, stationsValues.get(stationIndex), distance); // יצירת עצם הזמנה
            refOrders.child(order.getOid()).setValue(order); // שמירת הזמנה בבסיס נתונים
            gotoOrderStatusActivity(); // מעבר לרשימת הזמנות
        } else {
            Toast.makeText(this, "פרמטרים לא תקינים", Toast.LENGTH_SHORT).show();
        }
    }

    // מאזין ללחיצת כפתור חיפוש
    public void onSearchButton1Click(View view) {
        Toast.makeText(this, "אתה נמצא במסך חיפוש", Toast.LENGTH_SHORT).show();
    }

    // מאזין ללחיצת כפתור סטטוס הזמנות
    public void onOrderStatusButton1Click(View view) {
        gotoOrderStatusActivity();
    }

    // מעבר לאקטיביטי פרופיל משתמש
    public void gotoOpeningActivity() {
        Intent intent = new Intent(this, OpeningActivity.class);
        startActivity(intent);
    }

    // מעבר לאקטיביטי סטטוס הזמנות
    public void gotoOrderStatusActivity() {
        Intent intent = new Intent(this, OrderStatusActivity.class);
        startActivity(intent);
    }
}
