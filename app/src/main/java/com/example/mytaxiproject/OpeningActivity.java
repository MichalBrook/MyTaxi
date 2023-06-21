package com.example.mytaxiproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class OpeningActivity extends AppCompatActivity {
    Toolbar Toolbar2;
    EditText PhoneText; //שדה להזנת מספר הטלפון
    EditText FirstNameText; //שדה להזנת השם הפרטי
    EditText LastNameText; //שדה להזנת שם המשפחה
    EditText MailText; //שדה להזנת המייל
    Button CancelButton1; //כפתור ביטול
    Button SaveButton; //כפתור שמירה

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        Toolbar2 = findViewById(R.id.Toolbar2);
        setSupportActionBar(Toolbar2);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        PhoneText = findViewById(R.id.PhoneText);
        FirstNameText = findViewById(R.id.FirstNameText);
        LastNameText = findViewById(R.id.LastNameText);
        MailText = findViewById(R.id.MailText);
        CancelButton1 = findViewById(R.id.CancelButton1);
        SaveButton = findViewById(R.id.SaveButton);

        updateFields();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_menu2, menu);
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

        if (itemId == R.id.menuItem22) {
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
        } else if (itemId == R.id.menuItem23) {
            // Delete all
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    myEdit.clear().apply();
                    updateFields();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    /**
     * כפתור שמירה והעברת נתונים לאקטיביטי2
     */
    public void onSaveButtonClick(View view) {
        myEdit.putString("yourPhoneNumber", String.valueOf(PhoneText.getText())).apply();
        myEdit.putString("yourFirstName", String.valueOf(FirstNameText.getText())).apply();
        myEdit.putString("yourLastName", String.valueOf(LastNameText.getText())).apply();
        myEdit.putString("yourMail", String.valueOf(MailText.getText())).apply();

        // Go to next screen:
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void updateFields() {
        PhoneText.setText(sharedPreferences.getString("yourPhoneNumber", ""));
        FirstNameText.setText(sharedPreferences.getString("yourFirstName", ""));
        LastNameText.setText(sharedPreferences.getString("yourLastName", ""));
        MailText.setText(sharedPreferences.getString("yourMail", ""));
    }

    /**
     * כפתור ביטול
     */
    public void onClearButton1Click(View view) {
        PhoneText.setText(""); //אפס שדה להזנת מספר טלפון
        FirstNameText.setText(""); //אפס שדה להזנת שם פרטי
        LastNameText.setText(""); //אפס שדה להזנת שם משפחה
        MailText.setText(""); //אפס שדה להזנת המייל
    }
}
