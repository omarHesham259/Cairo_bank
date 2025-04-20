package com.example.cairo_bank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class HomePage extends AppCompatActivity {

    DBHelper dbHelper;
    TextView balanceText;
    TextView usernameText;
    String currentUser;

    @Override
    protected void onResume() {
        super.onResume();
        updateBalance(); // Refresh balance every time the screen resumes
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        dbHelper = new DBHelper(this);
        balanceText = findViewById(R.id.balance_value);
        usernameText = findViewById(R.id.username); // Shows "Welcome, USERNAME!"

        // Try to get username from Intent
        Intent intent = getIntent();
        currentUser = intent.getStringExtra("username");

        // Fallback to SharedPreferences
        if (currentUser == null) {
            SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
            currentUser = prefs.getString("current_user", null);
        }

        if (currentUser == null) {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        usernameText.setText("Welcome, " + currentUser + "!");
        updateBalance();

        // Set up button navigation to other screens
        findViewById(R.id.Tran_Lin).setOnClickListener(v ->
                startActivity(new Intent(this, Transaction.class))
        );

        findViewById(R.id.Transfer_Lin).setOnClickListener(v -> {
            Intent i = new Intent(this, Transfer.class);
            i.putExtra("username", currentUser);
            startActivity(i);
        });

        findViewById(R.id.flash_Lin).setOnClickListener(v -> {
            Intent i = new Intent(this, Electricity_Payment.class);
            i.putExtra("username", currentUser);
            startActivity(i);
        });

        findViewById(R.id.Internet).setOnClickListener(v -> {
            Intent i = new Intent(this, Internet_payment.class);
            i.putExtra("username", currentUser);
            startActivity(i);
        });

        findViewById(R.id.drop).setOnClickListener(v -> {
            Intent i = new Intent(this, Water_Payment.class);
            i.putExtra("username", currentUser);
            startActivity(i);
        });

        findViewById(R.id.flame).setOnClickListener(v -> {
            Intent i = new Intent(this, Gas_payment.class);
            i.putExtra("username", currentUser);
            startActivity(i);
        });
    }

    private void updateBalance() {
        double balance = dbHelper.getBalance(currentUser);
        balanceText.setText("Balance: " + balance + " EGP");
    }
}
