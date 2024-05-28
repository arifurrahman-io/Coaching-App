package com.arifur.coaching;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.arifur.coaching.utils.PreferencesManager;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends BaseActivity {

    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Home");
        }

        logoutButton = findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                PreferencesManager.getInstance(MainActivity.this).clearLoginInfo();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void addStudent(View view) {
        startActivity(new Intent(this, AddStudentActivity.class));
    }

    public void addPayment(View view) {
        startActivity(new Intent(this, AddPaymentActivity.class));
    }

    public void addDailyCost(View view) {
        startActivity(new Intent(this, AddDailyCostActivity.class));
    }

    public void viewSummary(View view) {
        startActivity(new Intent(this, SummaryActivity.class));
    }

    public void viewMonthlyCosts(View view) {
        startActivity(new Intent(this, MonthlyCostActivity.class));
    }

    public void viewPaymentHistory(View view) {
        startActivity(new Intent(this, StudentPaymentHistoryActivity.class));
    }

    public void logout(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("CoachingAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}


