package com.arifur.coaching;

import com.arifur.coaching.models.Cost;
import com.arifur.coaching.models.Payment;

import java.util.List;

public class SummaryUtils {
    public static double calculateTotalPayments(List<Payment> payments) {
        double total = 0;
        for (Payment payment : payments) {
            total += payment.getAmount();
        }
        return total;
    }

    public static double calculateTotalCosts(List<Cost> costs) {
        double total = 0;
        for (Cost cost : costs) {
            total += cost.getAmount();
        }
        return total;
    }

    public static double calculateBalance(double totalPayments, double totalCosts) {
        return totalPayments - totalCosts;
    }
}