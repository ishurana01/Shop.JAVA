import dao.OrderDAO;
import dao.ProductDAO;
import dao.UserDAO;
import model.CartItem;
import model.Product;
import model.User;
import service.CartService;
import service.InvoiceService;
import service.EmailService;
import java.util.ArrayList;

import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner sc             = new Scanner(System.in);
    static UserDAO userDAO        = new UserDAO();
    static ProductDAO productDAO  = new ProductDAO();
    static OrderDAO orderDAO      = new OrderDAO();
    static CartService cart       = new CartService();
    static InvoiceService invoice = new InvoiceService();
    static User currentUser       = null;

    public static void main(String[] args) {
        System.out.println("==============================");
        System.out.println("     Welcome to SHOP.JAVA     ");
        System.out.println("==============================");

        while (true) {
            System.out.println("\n1. Login\n2. Register\n0. Exit");
            System.out.print("Choice: ");
            int choice = sc.nextInt(); sc.nextLine();

            switch (choice) {
                case 1 -> loginFlow();
                case 2 -> registerFlow();
                case 0 -> { System.out.println("Goodbye!"); return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void loginFlow() {
        System.out.print("Email: ");    String email = sc.nextLine();
        System.out.print("Password: "); String pass  = sc.nextLine();

        currentUser = userDAO.login(email, pass);
        if (currentUser == null) {
            System.out.println("Invalid credentials.");
            return;
        }
        System.out.println("Welcome, " + currentUser.getName() + "!");

        if (currentUser.getRole().equals("admin")) adminMenu();
        else customerMenu();
    }

    static void registerFlow() {
        System.out.print("Name: ");     String name  = sc.nextLine();
        System.out.print("Email: ");    String email = sc.nextLine();
        System.out.print("Password: "); String pass  = sc.nextLine();

        if (userDAO.register(name, email, pass))
            System.out.println("Registered successfully! Please login.");
        else
            System.out.println("Email already exists.");
    }

    static void customerMenu() {
        while (true) {
            System.out.println("\n-- Customer Menu --");
            System.out.println("1. Browse Products");
            System.out.println("2. Add to Cart");
            System.out.println("3. View Cart");
            System.out.println("4. Remove from Cart");
            System.out.println("5. Checkout");
            System.out.println("0. Logout");
            System.out.print("Choice: ");
            int ch = sc.nextInt(); sc.nextLine();

            switch (ch) {
                case 1 -> browseProducts();
                case 2 -> addToCartFlow();
                case 3 -> cart.viewCart();
                case 4 -> removeFromCartFlow();
                case 5 -> checkoutFlow();
                case 0 -> { cart.clearCart(); currentUser = null; return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void browseProducts() {
        List<Product> products = productDAO.getAllProducts();
        System.out.println("\n Available Products:");
        System.out.println("-".repeat(65));
        products.forEach(System.out::println);
        System.out.println("-".repeat(65));
    }

    static void addToCartFlow() {
        browseProducts();
        System.out.print("Enter Product ID to add: ");
        int id = sc.nextInt();
        System.out.print("Quantity: ");
        int qty = sc.nextInt(); sc.nextLine();

        Product p = productDAO.getProductById(id);
        if (p == null)        { System.out.println("Product not found."); return; }
        if (qty > p.getStock()){ System.out.println("Not enough stock."); return; }

        cart.addToCart(p, qty);
    }

    static void removeFromCartFlow() {
        cart.viewCart();
        System.out.print("Enter Product ID to remove: ");
        int id = sc.nextInt(); sc.nextLine();
        cart.removeFromCart(id);
    }

    static void checkoutFlow() {
        if (cart.isEmpty()) { System.out.println("Cart is empty!"); return; }

        cart.viewCart();
        double subtotal = cart.getSubtotal();
        double gst      = subtotal * 0.18;
        System.out.printf("%nGST (18%%): Rs.%.2f%n", gst);
        System.out.printf("Total    : Rs.%.2f%n", subtotal + gst);
        System.out.print("\nConfirm order? (y/n): ");
        String confirm = sc.nextLine();

        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Order cancelled.");
            return;
        }

        int orderId = orderDAO.placeOrder(
                currentUser.getUserId(),
                cart.getCart(),
                subtotal + gst
        );

        if (orderId != -1) {
            invoice.printInvoice(orderId, currentUser.getName(), cart.getCart());
            cart.clearCart();
        } else {
            System.out.println("Order failed. Try again.");
        }
    }

    static void adminMenu() {
        while (true) {
            System.out.println("\n-- Admin Panel --");
            System.out.println("1. View All Products");
            System.out.println("2. Add New Product");
            System.out.println("3. Update Stock");
            System.out.println("4. Delete Product");
            System.out.println("5. View All Orders");
            System.out.println("0. Logout");
            System.out.print("Choice: ");
            int ch = sc.nextInt(); sc.nextLine();

            switch (ch) {
                case 1 -> browseProducts();
                case 2 -> addProductFlow();
                case 3 -> updateStockFlow();
                case 4 -> deleteProductFlow();
                case 5 -> orderDAO.printAllOrders();
                case 0 -> { currentUser = null; return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void addProductFlow() {
        System.out.print("Product Name: ");   String name   = sc.nextLine();
        System.out.print("Category: ");       String cat    = sc.nextLine();
        System.out.print("Price (Rs.): ");    double price  = sc.nextDouble(); sc.nextLine();
        System.out.print("Stock quantity: "); int stock     = sc.nextInt();    sc.nextLine();
        System.out.print("Image URL (or press Enter to skip): "); String image = sc.nextLine();

        if (productDAO.addProduct(name, cat, price, stock, image))
            System.out.println("Product added!");
        else
            System.out.println("Failed to add product.");
    }

    static void updateStockFlow() {
        browseProducts();
        System.out.print("Enter Product ID: "); int id    = sc.nextInt();
        System.out.print("New stock value: ");  int stock = sc.nextInt(); sc.nextLine();

        if (productDAO.updateStock(id, stock))
            System.out.println("Stock updated!");
        else
            System.out.println("Update failed.");
    }

    static void deleteProductFlow() {
        browseProducts();
        System.out.print("Enter Product ID to delete: "); int id = sc.nextInt(); sc.nextLine();
        System.out.print("Are you sure? (y/n): "); String confirm = sc.nextLine();

        if (confirm.equalsIgnoreCase("y")) {
            if (productDAO.deleteProduct(id))
                System.out.println("Product deleted.");
            else
                System.out.println("Delete failed.");
        }
    }
}