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

    BatteryLevelReceiver batteryLevelReceiver;

    Toolbar Toolbar1;
    EditText OriginAddressText; //שדה להזנת כתובת המוצא
    EditText DestinationAddressText; //שדה להזנת כתובת היעד

    Button FindButton; //כפתור חיפוש
    Button ClearButton; //כפתור ניקוי
    Button BuyButton; //כפתור קניה

    ImageButton SearchButton1; //כפתור בית 1
    ImageButton OrderStatusButton1; //כפתור תפריט 1

    TextView KilometerText1; //קילומטר (ק"מ) 1
    TextView TimeText1; //זמן נסיעה 1

    double distance = 0;
    int duration = 0;

    ListView stationsView;
    ArrayList<String> stationsList = new ArrayList<>();
    ArrayList<Station> stationsValues = new ArrayList<>();
    ArrayAdapter<String> stationsAdapter;
    int stationIndex = -1;

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

        stationsView = findViewById(R.id.stationsView);
        stationsAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, stationsList);
        stationsView.setAdapter(stationsAdapter);
        stationsView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_LOW);
        registerReceiver(batteryLevelReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
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

    public void onFindButtonClick(View view) {
        fetchDistance();
        fetchPrices();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void fetchDistance() {
        String origin = String.valueOf(OriginAddressText.getText());
        String destination = String.valueOf(DestinationAddressText.getText());
        MapsRequest mapsRequest = new MapsRequest(new Address(origin), new Address(destination));

        try {
            MapsResponse mapsResponse = invokeMapsRequest(mapsRequest);

            distance = (double) mapsResponse.getTotalMeters() / 1000; // to Kilometers
            duration = mapsResponse.getTotalSeconds() / 60; // to Minutes

            KilometerText1.setText(String.format("%.1f", distance) + " km");
            TimeText1.setText(duration + " min");
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("DefaultLocale")
    public void fetchPrices() {
        refStations.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stationsList.clear();
                stationsValues.clear();
                for (DataSnapshot data: snapshot.getChildren()) {
                    Station valuesItem = data.getValue(Station.class);
                    stationsValues.add(valuesItem);

                    assert valuesItem != null;
                    double totalPrice = valuesItem.getOrderPrice() + valuesItem.getKmPrice() * distance;
                    String listItem =
                            "Name: " + valuesItem.getName() + "\n" +
                            "Price: " + String.format("%.2f", totalPrice);
                    stationsList.add(listItem);
                }
                stationsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO: Implement.
            }
        });
    }

    /**
     * Invoke request to Google Maps - Routes API (v2).
     * @param mapsRequest MapsRequest - MapsRequest object.
     * @return MapsResponse - MapsResponse object.
     * @throws IOException - Thrown by ObjectMapper, OkHttpClient.
     */
    public MapsResponse invokeMapsRequest(MapsRequest mapsRequest) throws IOException {
        String mask = "routes.duration,routes.distanceMeters";
        String type = "application/json";

        ObjectMapper objectMapper = new ObjectMapper();
        String payload = objectMapper.writeValueAsString(mapsRequest);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(payload, MediaType.parse(type));
        Request request = new Request.Builder()
                .url(Secrets.mapsApiUrl)
                .post(body)
                .addHeader("X-Goog-Api-Key", Secrets.mapsApiKey)
                .addHeader("X-Goog-FieldMask", mask)
                .addHeader("Accept", type)
                //.addHeader("Content-type", type) // Set in RequestBody.create()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                String json = response.body().string();
                return objectMapper.readValue(json, MapsResponse.class);
            } else {
                return new MapsResponse();
            }
        }
    }

    public void onClearButtonClick(View view) {
        OriginAddressText.setText(""); //אפס שדה להזנת מספר טלפון
        DestinationAddressText.setText(""); //אפס שדה להזנת שם פרטי
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        stationIndex = i;
    }

    public void onBuyButtonClick(View view) {
        String userId = sharedPreferences.getString("yourPhoneNumber", "");
        if (userId.length() > 0 && stationIndex >= 0 && distance > 0) {
            Order order = new Order(userId, stationsValues.get(stationIndex), distance);
            refOrders.child(order.getOid()).setValue(order);
            gotoOrderStatusActivity();
        } else {
            Toast.makeText(this, "Invalid parameters.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSearchButton1Click(View view) {
        Toast.makeText(this, "You are now in Search screen", Toast.LENGTH_SHORT).show();
    }

    public void onOrderStatusButton1Click(View view) {
        gotoOrderStatusActivity();
    }

    public void gotoOpeningActivity() {
        Intent intent = new Intent(this, OpeningActivity.class);
        startActivity(intent);
    }

    public void gotoOrderStatusActivity() {
        Intent intent = new Intent(this, OrderStatusActivity.class);
        startActivity(intent);
    }
}
