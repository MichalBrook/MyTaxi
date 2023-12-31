/**
 * אקטיביטי להזנת פרטי משתמש
 */

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
    Toolbar Toolbar2; // תפריט ראשי
    EditText PhoneText; // שדה להזנת מספר הטלפון
    EditText FirstNameText; // שדה להזנת השם הפרטי
    EditText LastNameText; // שדה להזנת שם המשפחה
    EditText MailText; // שדה להזנת המייל
    Button CancelButton1; // כפתור ביטול
    Button SaveButton; // כפתור שמירה

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        // תפריט ראשי
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

    // יצירת תפריט
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_menu2, menu);
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

        if (itemId == R.id.menuItem22) {
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
        } else if (itemId == R.id.menuItem23) {
            // מחיקת כל הפרטים
            builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
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

    // כפתור שמירת נתונים ומעבר לאקטיביטי חיפוש
    public void onSaveButtonClick(View view) {
        // שמור נתונים
        myEdit.putString("yourPhoneNumber", String.valueOf(PhoneText.getText())).apply();
        myEdit.putString("yourFirstName", String.valueOf(FirstNameText.getText())).apply();
        myEdit.putString("yourLastName", String.valueOf(LastNameText.getText())).apply();
        myEdit.putString("yourMail", String.valueOf(MailText.getText())).apply();

        // מעבר לאקטיביטי חיפוש
        if (sharedPreferences.getString("yourPhoneNumber", "").length() > 0) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "יש למלא מספר טלפון", Toast.LENGTH_LONG).show();
        }
    }

    // שליפת נתונים שמורים והצבה בשדות על המסך
    public void updateFields() {
        PhoneText.setText(sharedPreferences.getString("yourPhoneNumber", ""));
        FirstNameText.setText(sharedPreferences.getString("yourFirstName", ""));
        LastNameText.setText(sharedPreferences.getString("yourLastName", ""));
        MailText.setText(sharedPreferences.getString("yourMail", ""));
    }

    // כפתור ניקוי שדות
    public void onClearButton1Click(View view) {
        PhoneText.setText(""); // אפס שדה להזנת מספר טלפון
        FirstNameText.setText(""); // אפס שדה להזנת שם פרטי
        LastNameText.setText(""); // אפס שדה להזנת שם משפחה
        MailText.setText(""); // אפס שדה להזנת המייל
    }
}
