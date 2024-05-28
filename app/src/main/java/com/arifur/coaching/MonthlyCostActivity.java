package com.arifur.coaching;


import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import java.util.Map;

public class MonthlyCostActivity extends AppCompatActivity {

    private ListView costHistoryListView;
    private TextView netCostTextView;
    private List<Cost> costList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_cost);

        costHistoryListView = findViewById(R.id.costHistoryListView);
        netCostTextView = findViewById(R.id.netCostTextView);

        loadMonthlyCosts();
    }

    private void loadMonthlyCosts() {
        FirebaseUtils.getMonthlyCosts(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    costList = task.getResult().toObjects(Cost.class);
                    Map<String, Double> monthlyCosts = new HashMap<>();
                    double netCost = 0;

                    for (Cost cost : costList) {
                        String month = cost.getDate().toString(); // Extract the "YYYY-MM" part of the date
                        double amount = cost.getAmount();

                        if (!monthlyCosts.containsKey(month)) {
                            monthlyCosts.put(month, 0.0);
                        }
                        monthlyCosts.put(month, monthlyCosts.get(month) + amount);
                        netCost += amount;
                    }

                    // Convert the monthly costs map to a list of strings for display
                    List<String> costDetails = new ArrayList<>();
                    for (Map.Entry<String, Double> entry : monthlyCosts.entrySet()) {
                        costDetails.add(entry.getKey() + ": Tk. " + String.format("%.2f", entry.getValue()));
                    }

                    // Display the monthly costs in the ListView
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MonthlyCostActivity.this, android.R.layout.simple_list_item_1, costDetails);
                    costHistoryListView.setAdapter(adapter);

                    // Display the net cost
                    netCostTextView.setText("Net Cost: Tk. " + String.format("%.2f", netCost));
                } else {
                    Toast.makeText(MonthlyCostActivity.this, "Failed to load costs", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
