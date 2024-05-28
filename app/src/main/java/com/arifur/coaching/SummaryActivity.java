package com.arifur.coaching;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arifur.coaching.models.Cost;
import com.arifur.coaching.models.Payment;
import com.arifur.coaching.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class SummaryActivity extends AppCompatActivity {

    private TextView totalPaymentsTextView;
    private TextView totalCostsTextView;
    private TextView balanceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        totalPaymentsTextView = findViewById(R.id.totalPaymentsTextView);
        totalCostsTextView = findViewById(R.id.totalCostsTextView);
        balanceTextView = findViewById(R.id.balanceTextView);

        loadSummary();
    }

    private void loadSummary() {
        FirebaseUtils.getPayments(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Payment> payments = task.getResult().toObjects(Payment.class);
                    double totalPayments = SummaryUtils.calculateTotalPayments(payments);
                    totalPaymentsTextView.setText(String.format("Total Payments: Tk. %.2f", totalPayments));

                    FirebaseUtils.getCosts(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<Cost> costs = task.getResult().toObjects(Cost.class);
                                double totalCosts = SummaryUtils.calculateTotalCosts(costs);
                                totalCostsTextView.setText(String.format("Total Costs: Tk. %.2f", totalCosts));

                                double balance = SummaryUtils.calculateBalance(totalPayments, totalCosts);
                                balanceTextView.setText(String.format("Balance: Tk. %.2f", balance));
                            } else {
                                Toast.makeText(SummaryActivity.this, "Error loading costs", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(SummaryActivity.this, "Error loading payments", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}