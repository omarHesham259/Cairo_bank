package com.example.cairo_bank;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cairo_bank.DBHelper;
import com.example.cairo_bank.R;
import com.example.cairo_bank.Transaction;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Transactions extends AppCompatActivity {

    LinearLayout transactionContainer;
    DBHelper dbHelper;
    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        transactionContainer = findViewById(R.id.card_transaction); // Add this ID to your XML!
        dbHelper = new DBHelper(this);

        // Get logged-in user from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        currentUser = prefs.getString("current_user", null);

        if (currentUser != null) {
            List<Transaction> transactions = dbHelper.getAllTransactions(currentUser);
            LayoutInflater inflater = LayoutInflater.from(this);

            for (Transaction t : transactions) {
                // Inflate the custom transaction layout
                LinearLayout card = (LinearLayout) inflater.inflate(R.layout.transaction_item, transactionContainer, false);

                // Fill in values
                TextView dateText = card.findViewById(R.id.tv_date);
                TextView amountText = card.findViewById(R.id.tv_amount_value);
                TextView serviceText = card.findViewById(R.id.tv_service_value);

                String formattedDate = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(t.getTimestamp());
                dateText.setText(formattedDate);
                amountText.setText("Amount: " + t.getAmount());
                serviceText.setText("Service: " + t.getType());

                transactionContainer.addView(card);
            }
        }
    }}