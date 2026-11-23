package model;

public class OrderItem {
    private Furniture furniture;
    private int quantity;

    public OrderItem(Furniture furniture, int quantity) {
        this.furniture = furniture;
        this.quantity = quantity;
    }

    public Furniture getFurniture() { return furniture; }
    public int getQuantity() { return quantity; }

    // Overloaded methods (Unit III): default price vs custom price
    public double getLineTotal() {
        return furniture.getPrice() * quantity;
    }

    public double getLineTotal(double customUnitPrice) {
        return customUnitPrice * quantity;
    }
}
