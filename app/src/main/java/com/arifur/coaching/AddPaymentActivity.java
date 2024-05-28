package com.arifur.coaching;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.arifur.coaching.R;
import com.arifur.coaching.models.Payment;
import com.arifur.coaching.models.Student;
import com.arifur.coaching.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddPaymentActivity extends BaseActivity {

    private Spinner studentSpinner;
    private EditText amountEditText;
    private EditText dateEditText;
    private List<Student> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Payment");
        }

        studentSpinner = findViewById(R.id.studentSpinner);
        amountEditText = findViewById(R.id.amountEditText);
        dateEditText = findViewById(R.id.dateEditText);

        // Set a click listener on the date field to show the DatePickerDialog
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        loadStudents();
    }

    private void loadStudents() {
        FirebaseUtils.getStudents(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    studentList = task.getResult().toObjects(Student.class);
                    List<String> studentNames = new ArrayList<>();
                    for (Student student : studentList) {
                        studentNames.add(student.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddPaymentActivity.this, android.R.layout.simple_spinner_item, studentNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    studentSpinner.setAdapter(adapter);
                } else {
                    Toast.makeText(AddPaymentActivity.this, "Failed to load students", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateEditText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    public void addPayment(View view) {
        int selectedPosition = studentSpinner.getSelectedItemPosition();
        if (selectedPosition == AdapterView.INVALID_POSITION) {
            Toast.makeText(this, "Please select a student", Toast.LENGTH_SHORT).show();
            return;
        }

        String studentId = studentList.get(selectedPosition).getId();
        String amountStr = amountEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        if (date.isEmpty()) {
            Toast.makeText(this, "Please enter date", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = FirebaseUtils.db.collection(Constants.PAYMENTS_COLLECTION).document().getId();
        Payment payment = new Payment(id, studentId, amount, date);
        FirebaseUtils.addPayment(payment, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddPaymentActivity.this, "Payment added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddPaymentActivity.this, "Error adding payment", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}