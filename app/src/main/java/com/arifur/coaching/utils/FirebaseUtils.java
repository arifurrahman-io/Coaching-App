package com.arifur.coaching.utils;

import com.arifur.coaching.Constants;
import com.arifur.coaching.models.Cost;
import com.arifur.coaching.models.Payment;
import com.arifur.coaching.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseUtils {
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void addStudent(Student student, OnCompleteListener<Void> onComplete) {
        db.collection("students").document(student.getId())
                .set(student)
                .addOnCompleteListener(onComplete);
    }

    public static void addPayment(Payment payment, OnCompleteListener<Void> onComplete) {
        db.collection("payments").document(payment.getId())
                .set(payment)
                .addOnCompleteListener(onComplete);
    }


    public static void addDailyCost(Cost cost, OnCompleteListener<DocumentReference> onCompleteListener) {
        FirebaseFirestore.getInstance().collection("costs").add(cost).addOnCompleteListener(onCompleteListener);
    }


    public static void getStudents(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(Constants.STUDENTS_COLLECTION).get().addOnCompleteListener(onCompleteListener);
    }


    public static void getPayments(OnCompleteListener<QuerySnapshot> onComplete) {
        db.collection("payments").get().addOnCompleteListener(onComplete);
    }

    public static void getCosts(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(Constants.COSTS_COLLECTION).get().addOnCompleteListener(onCompleteListener);
    }


    public static void getPaymentsByStudentId(String studentId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(Constants.PAYMENTS_COLLECTION)
                .whereEqualTo("studentId", studentId)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public static void getMonthlyCosts(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        FirebaseFirestore.getInstance().collection("costs").get().addOnCompleteListener(onCompleteListener);
    }

}
