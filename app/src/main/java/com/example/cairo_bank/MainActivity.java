package com.example.cairo_bank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;



import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    Button loginButton;
    ImageView fingerprintIcon;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views
        usernameEditText = findViewById(R.id.username_input);
        passwordEditText = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_btn);
        fingerprintIcon = findViewById(R.id.authenticate_button);
        dbHelper = new DBHelper(this);

        // Predefined users (test/demo)
        dbHelper.insertUser("hamza", "1234");
        dbHelper.insertUser("Ahmed", "123");
        dbHelper.insertUser("omar", "admin");

        // Normal login
        loginButton.setOnClickListener(v -> {
            String u = usernameEditText.getText().toString().trim();
            String p = passwordEditText.getText().toString().trim();

            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.checkUser(u, p)) {
                SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
                prefs.edit().putString("current_user", u).apply();

                // For testing: add 1000 EGP to balance
                double currentBalance = dbHelper.getBalance(u);
                dbHelper.updateBalance(u, currentBalance);

                Intent intent = new Intent(this, HomePage.class);
                intent.putExtra("username", u);  // Pass username
                startActivity(intent);
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });

        // Fingerprint login setup
        BiometricManager bm = BiometricManager.from(this);
        if (bm.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            fingerprintIcon.setOnClickListener(v -> showBiometricPrompt());
        } else {
            Toast.makeText(this, "Fingerprint not supported or not set up", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt prompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
                        String user = prefs.getString("current_user", null);
                        if (user != null) {
                            Toast.makeText(MainActivity.this, "Welcome back, " + user + "!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, HomePage.class);
                            intent.putExtra("username", user);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Please log in with username/password first.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(MainActivity.this, "Fingerprint not recognized", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationError(int errCode, CharSequence err) {
                        super.onAuthenticationError(errCode, err);
                        Toast.makeText(MainActivity.this, "Auth error: " + err, Toast.LENGTH_SHORT).show();
                    }
                });

        BiometricPrompt.PromptInfo info = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login with Fingerprint")
                .setSubtitle("Authenticate to access your account")
                .setNegativeButtonText("Cancel")
                .build();

        prompt.authenticate(info);
    }
}
