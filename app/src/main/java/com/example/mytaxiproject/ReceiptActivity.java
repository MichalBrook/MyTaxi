/**
 * אקטיביטי קבלה
 */

package com.example.mytaxiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ReceiptActivity extends AppCompatActivity {
    TextView PhoneNumber3; // מספר טלפון
    TextView FirstName; // שם פרטי
    TextView LastName; // שם משפחה
    TextView Mail; // מייל
    TextView CreditCard; // מספר אשראי

    TextView TotalSumText2; // סכום סופי

    Button FinishButton; // כפתור סיום

    double totalSum;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        PhoneNumber3 = findViewById(R.id.PhoneNumber3);
        FirstName = findViewById(R.id.FirstName);
        LastName = findViewById(R.id.LastName);
        Mail = findViewById(R.id.Mail);
        CreditCard = findViewById(R.id.CreditCard);
        TotalSumText2 = findViewById(R.id.TotalSumText2);

        FinishButton = findViewById(R.id.FinishButton);

        PhoneNumber3.setText(sharedPreferences.getString("yourPhoneNumber", ""));
        FirstName.setText(sharedPreferences.getString("yourFirstName", ""));
        LastName.setText(sharedPreferences.getString("yourLastName", ""));
        Mail.setText(sharedPreferences.getString("yourMail", ""));
        CreditCard.setText(sharedPreferences.getString("yourCreditCard", ""));

        totalSum = getIntent().getDoubleExtra("totalSum", 0); // קבלת פרמטר של סכום התשלום
        TotalSumText2.setText(String.format("%.2f", totalSum));
    }

    // מאזין ללחיצת כפתור סיום
    public void onFinishButtonClick(View view) {
        Intent intent = new Intent(this, OrderStatusActivity.class);
        startActivity(intent);
    }
}
