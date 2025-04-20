package com.example.cairo_bank;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;



public class Internet_payment extends AppCompatActivity {
    EditText serviceNumberEditText, amountEditText;
    Button payButton;
    Spinner serviceProviderSpinner;
    DBHelper dbHelper;
    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electricity_payment);
        dbHelper = new DBHelper(this);

        SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        currentUser = prefs.getString("current_user", null);

        if (currentUser == null) {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView usernameText = findViewById(R.id.UserName);
        if (usernameText != null) {
            usernameText.setText(currentUser);
        }

        serviceProviderSpinner = findViewById(R.id.Spinner_Electricity_id);
        serviceNumberEditText = findViewById(R.id.edit_user_number);
        amountEditText = findViewById(R.id.edit_amount);
        payButton = findViewById(R.id.btn_pay);



        // Pay button click listener
        payButton.setOnClickListener(v -> {
            String serviceNumber = serviceNumberEditText.getText().toString().trim();
            String amountStr = amountEditText.getText().toString().trim();

            if (serviceNumber.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double balance = dbHelper.getBalance(currentUser);

            if (amount > balance) {
                Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show();
            } else {
                // Deduct amount and log transaction
                dbHelper.updateBalance(currentUser, balance - amount);
                String provider = serviceProviderSpinner.getSelectedItem().toString();
                String details = provider + " - " + serviceNumber;

                dbHelper.insertTransaction(currentUser, "Internet", amount, details);

                // Go back to home page
                startActivity(new Intent(this, HomePage.class));
                finish();
                Toast.makeText(this, "Electricity bill paid successfully!", Toast.LENGTH_SHORT).show();

            }
        });
    }
}

