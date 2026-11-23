package app;

import exceptions.FurnitureNotFoundException;
import exceptions.InsufficientStockException;
import model.*;

import java.util.Scanner;

public class WoodCraftApp {
    private static Furniture[] catalogue = new Furniture[6];
    private static Order[] orders = new Order[100]; // Fixed-size array (no ArrayList)
    private static int orderCount = 0;

    public static void main(String[] args) {
        seedCatalogue();

        // Unit V: try-with-resources
        try (Scanner sc = new Scanner(System.in)) {
            int choice = 0;

            // Unit II: do-while + switch for the main menu
            do {
                printMenu();
                try {
                    String input = sc.nextLine().trim();
                    choice = Integer.parseInt(input);

                    switch (choice) {
                        case 1:
                            displayCatalogue();
                            break;
                        case 2:
                            takeNewOrder(sc);
                            break;
                        case 3:
                            viewBillById(sc);
                            break;
                        case 4:
                            showSalesReport();
                            break;
                        case 5:
                            System.out.println("Exiting WoodCraft. Goodbye!");
                            break;
                        default:
                            System.out.println("Invalid option. Please choose 1 to 5.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Please enter a numeric menu choice.");
                    choice = 0;
                }
            } while (choice != 5);
        }
    }

    private static void seedCatalogue() {
        catalogue[0] = new Furniture(101, "Study Table", "Table", 4500.0, 10);
        catalogue[1] = new Furniture(102, "Office Chair", "Chair", 3200.0, 15);
        catalogue[2] = new Furniture(103, "Bookshelf", "Storage", 5200.0, 6);
        catalogue[3] = new Furniture(104, "Plastic Chair", "Chair", 650.0, 40);
        catalogue[4] = new Furniture(105, "Bean Bag", "Seating", 1800.0, 12);
        catalogue[5] = new Furniture(106, "Single Bed", "Bed", 9800.0, 4);
    }

    private static void printMenu() {
        System.out.println("\n--- WOODCRAFT SHOWROOM MENU ---");
        System.out.println("1. View catalogue");
        System.out.println("2. New order");
        System.out.println("3. View bill");
        System.out.println("4. Sales report");
        System.out.println("5. Exit");
        System.out.print("Enter choice: ");
    }

    private static void displayCatalogue() {
        System.out.println("\n------------------------------------------------------------");
        System.out.printf("%-5s %-16s %-10s %-10s %-6s %s%n",
                "ID", "Furniture Item", "Category", "Price", "Stock", "Status");
        System.out.println("------------------------------------------------------------");
        for (Furniture f : catalogue) {
            System.out.println(f.toString());
        }
        System.out.println("------------------------------------------------------------");
    }

    private static void takeNewOrder(Scanner sc) {
        if (orderCount >= orders.length) {
            System.out.println("Daily order capacity reached!");
            return;
        }

        System.out.print("Enter customer name: ");
        String custName = sc.nextLine().trim();
        if (custName.isEmpty()) {
            custName = "Walk-in Customer";
        }

        Customer customer = null;
        while (customer == null) {
            System.out.print("Customer type (1 for Retail, 2 for Wholesale): ");
            String typeInput = sc.nextLine().trim();
            if (typeInput.equals("1") || typeInput.equalsIgnoreCase("Retail")) {
                customer = new RetailCustomer(custName);
            } else if (typeInput.equals("2") || typeInput.equalsIgnoreCase("Wholesale")) {
                customer = new WholesaleCustomer(custName);
            } else {
                System.out.println("Invalid customer type. Enter 1 (Retail) or 2 (Wholesale).");
            }
        }

        ItemStack itemStack = new ItemStack(20);
        boolean ordering = true; // Unit I: boolean

        System.out.println("Add items (Enter ID, then Quantity | Enter -1 to Undo last item | Enter 0 to Finish):");

        // Unit V: Nested try, multiple catch, and finally
        try {
            while (ordering) {
                try {
                    System.out.print("\nEnter Furniture ID (-1 = Undo, 0 = Finish): ");
                    int id = Integer.parseInt(sc.nextLine().trim());

                    if (id == 0) {
                        ordering = false;
                        continue;
                    }

                    if (id == -1) {
                        OrderItem undone = itemStack.pop();
                        if (undone != null) {
                            System.out.println("Removed last item: " + undone.getFurniture().getName()
                                    + " (x" + undone.getQuantity() + ")");
                        } else {
                            System.out.println("Nothing to undo! Order cart is empty.");
                        }
                        continue;
                    }

                    // Throws FurnitureNotFoundException if ID does not exist
                    Furniture selected = findFurnitureById(id);

                    System.out.print("Enter quantity for " + selected.getName() + ": ");
                    int qty = Integer.parseInt(sc.nextLine().trim());

                    if (qty <= 0) {
                        System.out.println("Quantity must be greater than 0.");
                        continue;
                    }

                    // Check total requested quantity including items already in current cart
                    int alreadyInCart = getQuantityInStack(itemStack, selected.getId());
                    if (alreadyInCart + qty > selected.getStock()) {
                        throw new InsufficientStockException(
                                "Insufficient stock for " + selected.getName()
                                + ". Available: " + selected.getStock()
                                + ", Requested total: " + (alreadyInCart + qty));
                    }

                    if (itemStack.push(new OrderItem(selected, qty))) {
                        System.out.println("Added: " + qty + " x " + selected.getName());
                    } else {
                        System.out.println("Cart limit reached for a single order.");
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format! Please enter digits only.");
                } catch (FurnitureNotFoundException | InsufficientStockException e) {
                    // Unit V: Multi-catch for custom checked exceptions
                    System.out.println("Order Error: " + e.getMessage());
                }
            }

            if (itemStack.isEmpty()) {
                System.out.println("No items added. Order cancelled.");
                return;
            }

            // Deduct stock ONLY after the entire order is confirmed
            OrderItem[] finalItems = itemStack.toArray();
            for (OrderItem item : finalItems) {
                item.getFurniture().reduceStock(item.getQuantity());
            }

            Order newOrder = new Order(customer, finalItems);
            orders[orderCount++] = newOrder;
            newOrder.printBill();

        } finally {
            // Unit V: finally block executes whether order completes or exits early
            System.out.println("[Session closed — returning to main menu]");
        }
    }

    // Unit V: throws checked exception
    private static Furniture findFurnitureById(int id) throws FurnitureNotFoundException {
        for (Furniture f : catalogue) {
            if (f.getId() == id) {
                return f;
            }
        }
        throw new FurnitureNotFoundException("Furniture ID " + id + " does not exist in catalogue.");
    }

    private static int getQuantityInStack(ItemStack stack, int furnitureId) {
        int sum = 0;
        for (OrderItem item : stack.toArray()) {
            if (item.getFurniture().getId() == furnitureId) {
                sum += item.getQuantity();
            }
        }
        return sum;
    }

    private static void viewBillById(Scanner sc) {
        System.out.print("Enter Order ID (e.g., FUR1): ");
        String searchId = sc.nextLine().trim();

        for (int i = 0; i < orderCount; i++) {
            if (orders[i].getOrderId().equalsIgnoreCase(searchId)) {
                orders[i].printBill();
                return;
            }
        }
        System.out.println("Order ID '" + searchId + "' not found.");
    }

    private static void showSalesReport() {
        double totalRevenue = 0.0;
        for (int i = 0; i < orderCount; i++) {
            totalRevenue += orders[i].calculateFinalTotal();
        }

        Furniture mostPopular = null;
        int maxSold = 0;
        for (Furniture f : catalogue) {
            if (f.getTotalSold() > maxSold) {
                maxSold = f.getTotalSold();
                mostPopular = f;
            }
        }

        System.out.println("\n================ END OF DAY SALES REPORT ================");
        System.out.println("Total Orders   : " + orderCount);
        System.out.printf("Total Revenue  : ₹%.2f%n", totalRevenue);
        if (mostPopular != null && maxSold > 0) {
            System.out.println("Most Popular   : " + mostPopular.getName()
                    + " (" + maxSold + " units sold)");
        } else{
            System.out.println("Most Popular   : No items sold yet");
        }
        System.out.println("=========================================================\n");
    }
}