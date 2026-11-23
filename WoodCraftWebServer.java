package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import exceptions.FurnitureNotFoundException;
import exceptions.InsufficientStockException;
import model.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class WoodCraftWebServer {
    private static Furniture[] catalogue = new Furniture[6];
    private static Order[] orders = new Order[100];
    private static int orderCount = 0;

    public static void main(String[] args) throws IOException {
        seedCatalogue();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Serve the HTML frontend
        server.createContext("/", exchange -> {
            byte[] html = Files.readAllBytes(Path.of("web/index.html"));
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, html.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(html);
            }
        });

        // API 1: Return live catalogue as JSON
        server.createContext("/api/catalogue", exchange -> {
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < catalogue.length; i++) {
                Furniture f = catalogue[i];
                json.append(String.format(
                        "{\"id\":%d,\"name\":\"%s\",\"category\":\"%s\",\"price\":%.2f,\"stock\":%d,\"status\":\"%s\"}",
                        f.getId(), f.getName(), f.getCategory(), f.getPrice(), f.getStock(), f.getStockStatus()));
                if (i < catalogue.length - 1) json.append(",");
            }
            json.append("]");
            sendJson(exchange, 200, json.toString());
        });

        // API 2: Process order
        server.createContext("/api/order", exchange -> {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            try {
                String[] parts = body.split("\\|");
                String custName = parts[0].trim().isEmpty() ? "Walk-in" : parts[0].trim();
                String custType = parts[1].trim();
                String[] itemPairs = parts[2].split(",");

                Customer customer = custType.equalsIgnoreCase("Wholesale")
                        ? new WholesaleCustomer(custName)
                        : new RetailCustomer(custName);

                ItemStack stack = new ItemStack(20);
                for (String pair : itemPairs) {
                    String[] kv = pair.split(":");
                    int id = Integer.parseInt(kv[0].trim());
                    int qty = Integer.parseInt(kv[1].trim());
                    Furniture f = findById(id);
                    if (qty <= 0 || qty > f.getStock()) {
                        throw new InsufficientStockException("Insufficient stock for " + f.getName());
                    }
                    stack.push(new OrderItem(f, qty));
                }

                OrderItem[] finalItems = stack.toArray();
                for (OrderItem item : finalItems) {
                    item.getFurniture().reduceStock(item.getQuantity());
                }

                Order order = new Order(customer, finalItems);
                orders[orderCount++] = order;
                sendJson(exchange, 200, orderToJson(order));

            } catch (FurnitureNotFoundException | InsufficientStockException e) {
                sendJson(exchange, 400, "{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
            } catch (Exception e) {
                sendJson(exchange, 400, "{\"error\":\"Invalid order input format\"}");
            }
        });

        // API 3: Search past order by Order ID (e.g., /api/bill?id=FUR1)
        server.createContext("/api/bill", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            String searchId = (query != null && query.startsWith("id="))
                    ? query.substring(3).trim()
                    : "";

            for (int i = 0; i < orderCount; i++) {
                if (orders[i].getOrderId().equalsIgnoreCase(searchId)) {
                    sendJson(exchange, 200, orderToJson(orders[i]));
                    return;
                }
            }
            sendJson(exchange, 404, "{\"error\":\"Order ID '" + searchId + "' not found.\"}");
        });

        server.start();
        System.out.println("WoodCraft Web UI running at: http://localhost:8080");
    }

    private static String orderToJson(Order order) {
        StringBuilder itemsJson = new StringBuilder("[");
        OrderItem[] items = order.getItems();
        for (int i = 0; i < items.length; i++) {
            itemsJson.append(String.format(
                    "{\"name\":\"%s\",\"qty\":%d,\"price\":%.2f,\"lineTotal\":%.2f}",
                    items[i].getFurniture().getName(),
                    items[i].getQuantity(),
                    items[i].getFurniture().getPrice(),
                    items[i].getLineTotal()));
            if (i < items.length - 1) itemsJson.append(",");
        }
        itemsJson.append("]");

        return String.format(
                "{\"orderId\":\"%s\",\"token\":%d,\"van\":\"%s\",\"customer\":\"%s\",\"items\":%s,\"subtotal\":%.2f,\"discount\":%.2f,\"gst\":%.2f,\"total\":%.2f}",
                order.getOrderId(), order.getTokenNumber(), order.getAssignedVan(),
                order.getCustomer().toString(), itemsJson.toString(),
                order.calculateSubtotal(), order.calculateDiscount(),
                order.calculateGST(), order.calculateFinalTotal());
    }

    private static Furniture findById(int id) throws FurnitureNotFoundException {
        for (Furniture f : catalogue) {
            if (f.getId() == id) return f;
        }
        throw new FurnitureNotFoundException("Furniture ID " + id + " not found.");
    }

    private static void sendJson(HttpExchange ex, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
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
}
