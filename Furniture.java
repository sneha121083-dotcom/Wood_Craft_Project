package model;

public class Furniture {
    private int id;
    private String name;
    private String category;
    private double price;
    private int stock;
    private int totalSold;

    public Furniture(int id, String name, String category, double price, int stock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.totalSold = 0;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public int getTotalSold() { return totalSold; }

    public void reduceStock(int qty) {
        this.stock -= qty;
        this.totalSold += qty;
    }

    // Unit II: Ternary operator (?:) for LOW (below 10) or OK
    public String getStockStatus() {
        return (this.stock < 10) ? "LOW" : "OK";
    }

    @Override
    public String toString() {
        return String.format("%-5d %-16s %-10s ₹%-9.2f %-6d %s",
                id, name, category, price, stock, getStockStatus());
    }
}
