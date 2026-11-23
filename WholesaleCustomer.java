package model;

public class WholesaleCustomer extends Customer {
    public WholesaleCustomer(String name) {
        super(name, 'W');
    }

    @Override
    public double getDiscountRate() {
        return 0.15; // 15% discount
    }
}
