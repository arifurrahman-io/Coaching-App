package com.arifur.coaching;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        SharedPreferences sharedPreferences = getSharedPreferences("MySchoolApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}


