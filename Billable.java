package model;

public interface Billable {
    double GST = 0.18; // Unit I & IV: public static final constant

    double calculateSubtotal();
    double calculateDiscount();
    double calculateGST();
    double calculateFinalTotal();
    void printBill();
}
