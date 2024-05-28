package com.arifur.coaching;


import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.arifur.coaching.R;
import com.arifur.coaching.models.Cost;
import com.arifur.coaching.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MonthlyCostActivity extends BaseActivity {

    private TableLayout costTableLayout;
    private TextView netCostTextView;
    private List<Cost> costList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_cost);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Monthly Expenses");
        }

        costTableLayout = findViewById(R.id.costTableLayout);
        netCostTextView = findViewById(R.id.netCostTextView);

        loadMonthlyCosts();
    }

    private void loadMonthlyCosts() {
        FirebaseUtils.getCosts(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    costList = task.getResult().toObjects(Cost.class);
                    Map<String, Double> monthlyCosts = new HashMap<>();
                    double netCost = 0;

                    for (Cost cost : costList) {
                        String month = cost.getDate().substring(0, 7); // Extract the "YYYY-MM" part of the date
                        double amount = cost.getAmount();

                        if (!monthlyCosts.containsKey(month)) {
                            monthlyCosts.put(month, 0.0);
                        }
                        monthlyCosts.put(month, monthlyCosts.get(month) + amount);
                        netCost += amount;
                    }

                    // Display the monthly costs in the TableLayout
                    for (Map.Entry<String, Double> entry : monthlyCosts.entrySet()) {
                        TableRow tableRow = new TableRow(MonthlyCostActivity.this);
                        tableRow.setBackgroundResource(R.drawable.table_cell_border);

                        TextView monthTextView = new TextView(MonthlyCostActivity.this);
                        monthTextView.setText(entry.getKey());
                        monthTextView.setPadding(8, 8, 8, 8);
                        monthTextView.setBackgroundResource(R.drawable.table_cell_border);

                        TextView costTextView = new TextView(MonthlyCostActivity.this);
                        costTextView.setText(String.format(Locale.US, "Tk. %.2f", entry.getValue()));
                        costTextView.setPadding(8, 8, 8, 8);
                        costTextView.setBackgroundResource(R.drawable.table_cell_border);

                        tableRow.addView(monthTextView);
                        tableRow.addView(costTextView);

                        costTableLayout.addView(tableRow);
                    }

                    // Display the net cost
                    netCostTextView.setText(String.format(Locale.US, "Net Cost: Tk. %.2f", netCost));
                } else {
                    Toast.makeText(MonthlyCostActivity.this, "Failed to load costs", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
