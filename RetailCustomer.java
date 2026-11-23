package model;

public class RetailCustomer extends Customer {
    public RetailCustomer(String name) {
        super(name, 'R');
    }

    @Override
    public double getDiscountRate() {
        return 0.0; // 0% discount
    }
}
