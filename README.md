# WoodCraft – Furniture Showroom Management System

## Project Overview

WoodCraft is a menu-driven Java console application developed for managing furniture showroom operations. It simplifies furniture catalogue management, customer orders, billing, stock validation, and daily sales reporting.

The system supports both Retail and Wholesale customers, provides automatic discount and GST calculations, and prevents incorrect stock updates through order validation.

## Features

- **Furniture Catalogue:** Displays furniture ID, name, category, price, available stock, and stock status.
- **Order Management:** Creates orders for Retail and Wholesale customers.
- **Undo Functionality:** Allows users to remove the last added furniture item using a stack.
- **Stock Validation:** Checks furniture IDs and available quantities before confirming an order.
- **Automated Billing:** Generates unique order IDs, token numbers, delivery van assignments, discounts, GST, and final totals.
- **Bill Retrieval:** Retrieves previously generated bills using order IDs.
- **Sales Report:** Displays total orders, total revenue, and the most popular furniture item.
- **Input Validation:** Handles invalid inputs and prevents unexpected program termination.

## Technology Stack

- Language: Java
- Java Version: Java 17 or later
- IDE: Visual Studio Code
- Application Type: Console-based application

## Java Concepts Implemented

### Unit I – Java Fundamentals
- Arrays and primitive data types
- Variables and constants
- Type casting

### Unit II – Control Statements and Operators
- do-while loop
- switch statement
- Ternary operator
- Bitwise AND operator

### Unit III – Object-Oriented Programming
- Classes and objects
- Constructors and `this` keyword
- Method overloading
- Static variables
- Recursion
- Custom stack implementation

### Unit IV – Advanced OOP Concepts
- Packages
- Inheritance
- Abstract classes
- Method overriding
- Interfaces
- `super` and `final` keywords
- `toString()` method

### Unit V – Exception and File Handling
- Custom checked exceptions
- `throw` and `throws`
- Multiple catch blocks
- Nested try blocks
- finally block
- Try-with-resources

## Furniture Catalogue

| ID | Furniture | Category | Price (₹) | Stock |
|---|---|---|---:|---:|
| 101 | Study Table | Table | 4,500 | 10 |
| 102 | Office Chair | Chair | 3,200 | 15 |
| 103 | Bookshelf | Storage | 5,200 | 6 |
| 104 | Plastic Chair | Chair | 650 | 40 |
| 105 | Bean Bag | Seating | 1,800 | 12 |
| 106 | Single Bed | Bed | 9,800 | 4 |

## Billing Rules

| Customer Type | Discount |
|---|---:|
| Retail | 0% |
| Wholesale | 15% |

GST: 18% on the discounted subtotal.

**Final Amount = Discounted Subtotal + GST**

## Menu Options

1. View Catalogue
2. New Order
3. View Bill
4. Sales Report
5. Exit

## How to Run the Project

### Prerequisites
- Install Java JDK 17 or later.
- Install Visual Studio Code.
- Open the project folder in VS Code.

### Compile

Run the following command from the project root directory:

```bash
javac -d out src/furniture/*.java src/customer/*.java src/order/*.java src/billing/*.java src/exceptions/*.java src/main/*.java
```

### Execute

```bash
java -cp out main.Main
```

## Project Structure

```text
WoodCraft/
├── src/
│   ├── main/
│   ├── furniture/
│   ├── customer/
│   ├── order/
│   ├── billing/
│   └── exceptions/
│
├── AI_LOG.txt
└── README.md
```

## Expected Outcomes

- Accurate furniture billing and discount calculations.
- Reliable stock management.
- Prevention of duplicate or invalid stock allocation.
- Efficient order retrieval and sales tracking.
- Practical implementation of all five Java syllabus units.

## Academic Project

**Project Name:** WoodCraft – Furniture Showroom Management System

**Project Type:** Java Hackathon Problem 19

**Language:** Java 17+

**Application:** Console-based Furniture Order Management System
