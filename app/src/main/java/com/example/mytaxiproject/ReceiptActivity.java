package com.example.mytaxiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ReceiptActivity extends AppCompatActivity {
    TextView PhoneNumber3; //סך הכל
    TextView FirstName; //סך הכל
    TextView LastName; //סך הכל
    TextView Mail; //סך הכל
    TextView CreditCard; //סך הכל

    TextView TotalSumText2; //סך הכל

    Button FinishButton;

    double totalSum;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;

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

        //TotalSumText2 = findViewById(R.id.TotalSumText2); // ???

        FinishButton = findViewById(R.id.FinishButton);

        PhoneNumber3.setText(sharedPreferences.getString("yourPhoneNumber", ""));
        FirstName.setText(sharedPreferences.getString("yourFirstName", ""));
        LastName.setText(sharedPreferences.getString("yourLastName", ""));
        Mail.setText(sharedPreferences.getString("yourMail", ""));
        CreditCard.setText(sharedPreferences.getString("yourCreditCard", ""));

        totalSum = getIntent().getDoubleExtra("totalSum", 0);
        TotalSumText2.setText(String.valueOf(totalSum));
    }

    public void onFinishButtonClick(View view) {
        Intent intent = new Intent(this, OrderStatusActivity.class);
        startActivity(intent);
    }
}
