/**
 * אקטיביטי תשלום הזמנה
 */

package com.example.mytaxiproject;

import static com.example.mytaxiproject.firebase.FBRef.db;
import static com.example.mytaxiproject.firebase.FBRef.refOrders;
import static com.example.mytaxiproject.firebase.FBRef.rootOrders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytaxiproject.firebase.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;

public class PaymentActivity extends AppCompatActivity {
    String orderId = ""; // מזהה הזמנה

    EditText CreditCardText; //שדה להזנת מספר אשראי
    EditText CardValidityText; //שדה להזנת תוקף אשראי
    EditText ThreeNumbersText; //שדה להזנת שלוש ספרות

    Button CancelButton2; //כפתור ביטול
    Button PayButton2; //כפתור שמירה

    TextView CompanyText; //חברת מוניות

    TextView FixedPriceText; //מחיר קבוע
    TextView FixedPriceSumText; //סכום מחיר קבוע

    TextView KmPriceText; //מחיר לקילומטר (ק"מ)
    TextView KmAmountText; //כמות קילומטרים (ק"מ)
    TextView KmPriceSumText; //סכום מחיר לכל קילומטרים (ק"מ)

    TextView TotalSumText; //סך הכל

    Order order; // עצם הזמנה

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        orderId = getIntent().getStringExtra("orderId"); // קבלת פרמטר של מזהה הזמנה

        CreditCardText = findViewById(R.id.CreditCardText);
        CardValidityText = findViewById(R.id.CardValidityText);
        ThreeNumbersText = findViewById(R.id.ThreeNumbersText);

        CancelButton2 = findViewById(R.id.CancelButton2);
        PayButton2 = findViewById(R.id.PayButton2);

        CompanyText = findViewById(R.id.CompanyText);

        FixedPriceText = findViewById(R.id.FixedPriceText);
        FixedPriceSumText = findViewById(R.id.FixedPriceSumText);

        KmPriceText = findViewById(R.id.KmPriceText);
        KmAmountText = findViewById(R.id.KmAmountText);
        KmPriceSumText = findViewById(R.id.KmPriceSumText);

        TotalSumText = findViewById(R.id.TotalSumText);

        CreditCardText.setText(sharedPreferences.getString("yourCreditCard", ""));
        CardValidityText.setText(sharedPreferences.getString("yourCardValidity", ""));
        ThreeNumbersText.setText(sharedPreferences.getString("yourThreeNumbers", ""));

        String path = rootOrders + "/" + orderId; // יצירת נתיב להזמנה ספציפית בבסיס נתונים
        // מאזין חד פעמי להזמנה ספיפית בבסיס נתונים
        db.getReference(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                order = snapshot.getValue(Order.class); // הכנס הזמנה לעצם

                // הצג פרטי הזמנה
                assert order != null;
                CompanyText.setText(String.valueOf(order.getStationName()));
                FixedPriceText.setText(String.valueOf(order.getOrderPrice()));
                FixedPriceSumText.setText(String.valueOf(order.getOrderPrice()));
                KmPriceText.setText(String.valueOf(order.getKmPrice()));
                KmAmountText.setText(String.format("%.1f", order.getRideDistance()));
                KmPriceSumText.setText(String.format("%.2f", order.getKmPrice() * order.getRideDistance()));
                TotalSumText.setText(String.format("%.2f", order.getTotalPrice()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // שגיאה בקריאת נתונים
            }
        });
    }

    // מאזין ללחיצת כפתור ביטול
    public void onCancelButton2Click(View view) {
        CreditCardText.setText(""); //אפס שדה להזנת מספר אשראי
        CardValidityText.setText(""); //אפס שדה להזנת תוקף אשראי
        ThreeNumbersText.setText(""); //אפס שדה להזנת שלוש ספרות
    }

    // מאזין ללחיצת כפתור תשלום
    public void onPayButton2Click(View view) {
        if (
                CreditCardText.getText().length() > 0 &&
                CardValidityText.getText().length() > 0 &&
                ThreeNumbersText.getText().length() > 0
        ) {
            // הדמיית תשלום - יצירת מספר אישור תשלום
            Instant instant = Instant.now();
            long timestamp = instant.getEpochSecond();
            int nanosecond = instant.getNano();
            String cid = "CID" + timestamp + nanosecond;

            order.updatePaid(cid, timestamp); // עדכן עצם ההזמנה כמשולמת
            refOrders.child(order.getOid()).setValue(order); // עדכון ההזמנה בבסיס הנתונים

            // שמירת מספר כרטיס אשראי
            myEdit.putString("yourCreditCard", String.valueOf(CreditCardText.getText())).apply();

            // מעבר לאקטיביטי קבלה
            Intent intent = new Intent(this, ReceiptActivity.class);
            intent.putExtra("totalSum", order.getTotalPrice());
            startActivity(intent);
        } else {
            Toast.makeText(this, "יש למלא פרטי אשראי", Toast.LENGTH_SHORT).show();
        }
    }
}
