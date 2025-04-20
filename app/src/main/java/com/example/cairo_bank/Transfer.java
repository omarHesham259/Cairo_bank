package com.example.cairo_bank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cairo_bank.DBHelper;
import com.example.cairo_bank.R;

 public  class Transfer extends AppCompatActivity {

    EditText recipientEditText, amountEditText, reasonEditText;
    Button sendButton;
    DBHelper dbHelper;
    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        // Get logged-in user
        SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        currentUser = prefs.getString("current_user", null);

        if (currentUser == null) {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Show username at top
        TextView usernameText = findViewById(R.id.username);
        if (usernameText != null) {
            usernameText.setText(currentUser);
        }

        // Bind views
        recipientEditText = findViewById(R.id.Spinner_id);
        amountEditText = findViewById(R.id.edit_amount);
        reasonEditText = findViewById(R.id.edit_user_number);
        sendButton = findViewById(R.id.transfer_btn);
        dbHelper = new DBHelper(this);

        sendButton.setOnClickListener(v -> {
            String recipient = recipientEditText.getText().toString().trim();
            String amountStr = amountEditText.getText().toString().trim();
            String reason = reasonEditText.getText().toString().trim();

            // Validate inputs
            if (recipient.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (recipient.equals(currentUser)) {
                Toast.makeText(this, "You cannot transfer to yourself", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check sender balance
            double senderBalance = dbHelper.getBalance(currentUser);
            if (senderBalance < amount) {
                Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if recipient exists
            int recipientId = dbHelper.getUserId(recipient);
            if (recipientId == -1) {
                Toast.makeText(this, "Recipient does not exist", Toast.LENGTH_SHORT).show();
                return;
            }

            // Perform transfer
            double recipientBalance = dbHelper.getBalance(recipient);
            dbHelper.updateBalance(currentUser, senderBalance - amount);
            dbHelper.updateBalance(recipient, recipientBalance + amount);

            String date = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
            dbHelper.insertTransfer(currentUser, recipient, amount, date);

            Toast.makeText(this, "Transfer successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomePage.class));
            finish();
        });
    }
}
