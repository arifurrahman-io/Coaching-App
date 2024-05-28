package com.arifur.coaching;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.arifur.coaching.R;
import com.arifur.coaching.models.Cost;
import com.arifur.coaching.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddDailyCostActivity extends AppCompatActivity {

    private EditText costDescriptionEditText;
    private EditText costAmountEditText;
    private Button costDatePickerButton;
    private Button addCostButton;
    private String selectedDate;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_daily_cost);

        costDescriptionEditText = findViewById(R.id.costDescriptionEditText);
        costAmountEditText = findViewById(R.id.costAmountEditText);
        costDatePickerButton = findViewById(R.id.costDatePickerButton);
        addCostButton = findViewById(R.id.addCostButton);
        calendar = Calendar.getInstance();

        costDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        addCostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCost();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                selectedDate = sdf.format(calendar.getTime());
                costDatePickerButton.setText(selectedDate);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void addCost() {
        String description = costDescriptionEditText.getText().toString();
        String amountStr = costAmountEditText.getText().toString();

        if (description.isEmpty() || amountStr.isEmpty() || selectedDate == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        Cost cost = new Cost(description, amount, selectedDate);

        FirebaseUtils.addDailyCost(cost, new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddDailyCostActivity.this, "Cost added successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after adding cost
                } else {
                    Toast.makeText(AddDailyCostActivity.this, "Failed to add cost", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
