package com.arifur.coaching;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.arifur.coaching.R;
import com.arifur.coaching.models.Payment;
import com.arifur.coaching.models.Student;
import com.arifur.coaching.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StudentPaymentHistoryActivity extends BaseActivity {

    private Spinner studentSpinner;
    private ListView paymentHistoryListView;
    private Button downloadPdfButton;
    private List<Student> studentList;
    private List<Payment> paymentList;

    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_payment_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Payment History");
        }

        studentSpinner = findViewById(R.id.studentSpinner);
        paymentHistoryListView = findViewById(R.id.paymentHistoryListView);
        downloadPdfButton = findViewById(R.id.downloadPdfButton);

        loadStudents();

        downloadPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    createPdf();
                } else {
                    requestPermission();
                }
            }
        });
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

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(StudentPaymentHistoryActivity.this, android.R.layout.simple_spinner_item, studentNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    studentSpinner.setAdapter(adapter);

                    // Load payment history when a student is selected
                    studentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            loadPaymentHistory(studentList.get(position).getId());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Do nothing
                        }
                    });
                } else {
                    Toast.makeText(StudentPaymentHistoryActivity.this, "Failed to load students", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadPaymentHistory(String studentId) {
        FirebaseUtils.getPaymentsByStudentId(studentId, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    paymentList = task.getResult().toObjects(Payment.class);
                    List<String> paymentDetails = new ArrayList<>();
                    for (Payment payment : paymentList) {
                        paymentDetails.add(payment.getDate() + ": Tk. " + String.format("%.2f", payment.getAmount()));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(StudentPaymentHistoryActivity.this, android.R.layout.simple_list_item_1, paymentDetails);
                    paymentHistoryListView.setAdapter(adapter);
                } else {
                    Toast.makeText(StudentPaymentHistoryActivity.this, "Failed to load payment history", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(StudentPaymentHistoryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(StudentPaymentHistoryActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }

    private void createPdf() {
        String selectedStudent = studentSpinner.getSelectedItem().toString();
        String fileName = selectedStudent + "_payment_history.pdf";
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fileName;

        try {
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(36, 36, 36, 36);

            // Add title
            Paragraph title = new Paragraph("Payment History for " + selectedStudent)
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // Add table
            float[] columnWidths = {1, 3, 3};
            Table table = new Table(columnWidths);
            table.addCell(new Paragraph("#").setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addCell(new Paragraph("Date").setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addCell(new Paragraph("Amount").setBackgroundColor(ColorConstants.LIGHT_GRAY));

            for (int i = 0; i < paymentList.size(); i++) {
                Payment payment = paymentList.get(i);
                table.addCell(new Paragraph(String.valueOf(i + 1)));
                table.addCell(new Paragraph(payment.getDate()));
                table.addCell(new Paragraph("Tk. " + String.format("%.2f", payment.getAmount())));
            }

            document.add(table);
            document.close();

            Toast.makeText(this, "PDF created at: " + filePath, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error creating PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createPdf();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}