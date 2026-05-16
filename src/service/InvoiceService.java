package service;

import model.CartItem;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoiceService {

    public void printInvoice(int orderId, String customerName, List<CartItem> items) {
        double subtotal = items.stream().mapToDouble(CartItem::getSubtotal).sum();
        double gst      = subtotal * 0.18;
        double total    = subtotal + gst;

        String border = "=".repeat(50);
        StringBuilder sb = new StringBuilder();

        sb.append("\n").append(border).append("\n");
        sb.append("         SHOP.JAVA - INVOICE\n");
        sb.append(border).append("\n");
        sb.append(String.format("Order ID   : #%d%n", orderId));
        sb.append(String.format("Customer   : %s%n", customerName));
        sb.append(String.format("Date       : %s%n",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));
        sb.append("-".repeat(50)).append("\n");
        sb.append(String.format("%-20s %6s %10s%n", "Product", "Qty", "Amount"));
        sb.append("-".repeat(50)).append("\n");

        for (CartItem item : items) {
            sb.append(String.format("%-20s %6d %10.2f%n",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getSubtotal()));
        }

        sb.append("-".repeat(50)).append("\n");
        sb.append(String.format("%-28s %10.2f%n", "Subtotal (Rs.)", subtotal));
        sb.append(String.format("%-28s %10.2f%n", "GST 18% (Rs.)", gst));
        sb.append("-".repeat(50)).append("\n");
        sb.append(String.format("%-28s %10.2f%n", "GRAND TOTAL (Rs.)", total));
        sb.append(border).append("\n");
        sb.append("      Thank you for shopping with us!\n");
        sb.append(border).append("\n");

        System.out.println(sb.toString());
        saveInvoiceToFile(orderId, sb.toString());
    }

    private void saveInvoiceToFile(int orderId, String content) {
        String filename = "Invoice_" + orderId + ".txt";
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write(content);
            System.out.println("Invoice saved as: " + filename);
        } catch (IOException e) {
            System.out.println("Could not save invoice: " + e.getMessage());
        }
    }
}