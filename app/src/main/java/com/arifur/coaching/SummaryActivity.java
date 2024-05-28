package com.arifur.coaching;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.arifur.coaching.R;
import com.arifur.coaching.models.Cost;
import com.arifur.coaching.models.Payment;
import com.arifur.coaching.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Locale;

public class SummaryActivity extends BaseActivity {

    private TextView totalPaymentTextView;
    private TextView totalCostTextView;
    private TextView balanceTextView;
    private List<Payment> paymentList;
    private List<Cost> costList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Transaction Summary");
        }

        totalPaymentTextView = findViewById(R.id.totalPaymentTextView);
        totalCostTextView = findViewById(R.id.totalCostTextView);
        balanceTextView = findViewById(R.id.balanceTextView);

        loadSummary();
    }

    private void loadSummary() {
        FirebaseUtils.getPayments(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    paymentList = task.getResult().toObjects(Payment.class);
                    calculateSummary();
                } else {
                    Toast.makeText(SummaryActivity.this, "Failed to load payments", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FirebaseUtils.getCosts(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    costList = task.getResult().toObjects(Cost.class);
                    calculateSummary();
                } else {
                    Toast.makeText(SummaryActivity.this, "Failed to load costs", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void calculateSummary() {
        if (paymentList == null || costList == null) {
            return; // Wait until both lists are loaded
        }

        double totalPayments = 0;
        for (Payment payment : paymentList) {
            totalPayments += payment.getAmount();
        }

        double totalCosts = 0;
        for (Cost cost : costList) {
            totalCosts += cost.getAmount();
        }

        double balance = totalPayments - totalCosts;

        totalPaymentTextView.setText(String.format(Locale.US, "Tk. %.2f", totalPayments));
        totalCostTextView.setText(String.format(Locale.US, "Tk. %.2f", totalCosts));
        balanceTextView.setText(String.format(Locale.US, "Tk. %.2f", balance));
    }
}