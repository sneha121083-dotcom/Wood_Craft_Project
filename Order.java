package model;

public class Order implements Billable {
    private static int orderCounter = 0; // Unit III: static counter

    private String orderId;
    private int tokenNumber;
    private Customer customer;
    private OrderItem[] items;

    public Order(Customer customer, OrderItem[] items) {
        orderCounter++;
        this.orderId = "FUR" + orderCounter;
        this.tokenNumber = orderCounter;
        this.customer = customer;
        this.items = items;
    }

    public String getOrderId() { return orderId; }
    public int getTokenNumber() { return tokenNumber; }
    public Customer getCustomer() { return customer; }
    public OrderItem[] getItems() { return items; }

    // Unit II: Bitwise AND (token & 1) to assign Van 1 (odd) or Van 2 (even)
    public String getAssignedVan() {
        return ((tokenNumber & 1) == 1) ? "Van 1" : "Van 2";
    }

    // Unit III: Recursive subtotal calculation
    @Override
    public double calculateSubtotal() {
        return computeSubtotalRecursive(0);
    }

    private double computeSubtotalRecursive(int index) {
        if (index >= items.length) {
            return 0.0;
        }
        return items[index].getLineTotal() + computeSubtotalRecursive(index + 1);
    }

    @Override
    public double calculateDiscount() {
        return calculateSubtotal() * customer.getDiscountRate();
    }

    @Override
    public double calculateGST() {
        double discountedAmount = calculateSubtotal() - calculateDiscount();
        return discountedAmount * GST;
    }

    @Override
    public double calculateFinalTotal() {
        double discountedAmount = calculateSubtotal() - calculateDiscount();
        return discountedAmount + calculateGST();
    }

    @Override
    public void printBill() {
        System.out.println("\n==================================================");
        System.out.println("                 WOODCRAFT INVOICE                ");
        System.out.println("==================================================");
        System.out.printf("Order ID : %-12s Token No : %d (%s)%n",
                orderId, tokenNumber, getAssignedVan());
        System.out.println("Customer : " + customer.toString());
        System.out.println("--------------------------------------------------");
        System.out.printf("%-18s %5s %10s %12s%n", "Item", "Qty", "Price", "Total");
        System.out.println("--------------------------------------------------");
        for (OrderItem item : items) {
            System.out.printf("%-18s %5d %10.2f %12.2f%n",
                    item.getFurniture().getName(),
                    item.getQuantity(),
                    item.getFurniture().getPrice(),
                    item.getLineTotal());
        }
        System.out.println("--------------------------------------------------");
        System.out.printf("Subtotal           : ₹%.2f%n", calculateSubtotal());
        System.out.printf("Discount (%.0f%%)      : -₹%.2f%n",
                customer.getDiscountRate() * 100, calculateDiscount());
        System.out.printf("GST (18%%)          : +₹%.2f%n", calculateGST());
        System.out.printf("FINAL TOTAL        : ₹%.2f%n", calculateFinalTotal());
        System.out.println("==================================================\n");
    }
}
