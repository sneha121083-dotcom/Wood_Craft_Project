package model;

public abstract class Customer extends Person {
    protected char customerCode; // Unit I: char ('R' or 'W')

    public Customer(String name, char customerCode) {
        super(name); // Unit IV: super
        this.customerCode = customerCode;
    }

    public abstract double getDiscountRate();

    // Unit IV: One final method that subclasses cannot override
    public final String getCustomerTypeLabel() {
        return (customerCode == 'W') ? "Wholesale" : "Retail";
    }

    @Override
    public String toString() {
        return super.toString() + " (" + getCustomerTypeLabel() + ")";
    }
}
