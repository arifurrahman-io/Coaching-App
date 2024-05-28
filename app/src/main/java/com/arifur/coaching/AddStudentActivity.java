package com.arifur.coaching;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arifur.coaching.models.Student;
import com.arifur.coaching.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AddStudentActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.classEditText);
    }

    public void addStudent(View view) {
        String name = nameEditText.getText().toString().trim();
        String level = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(level)) {
            Toast.makeText(this, "Please enter class", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = FirebaseUtils.db.collection(Constants.STUDENTS_COLLECTION).document().getId();
        Student student = new Student(id, name, level);
        FirebaseUtils.addStudent(student, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddStudentActivity.this, "Student added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddStudentActivity.this, "Error adding student", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}