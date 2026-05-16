package service;

import model.CartItem;
import model.Product;
import java.util.ArrayList;
import java.util.List;

public class CartService {
    private List<CartItem> cart = new ArrayList<>();

    public void addToCart(Product product, int quantity) {
        for (CartItem item : cart) {
            if (item.getProduct().getProductId() == product.getProductId()) {
                item.setQuantity(item.getQuantity() + quantity);
                System.out.println("Quantity updated in cart.");
                return;
            }
        }
        cart.add(new CartItem(product, quantity));
        System.out.println(product.getName() + " added to cart.");
    }

    public void removeFromCart(int productId) {
        cart.removeIf(item -> item.getProduct().getProductId() == productId);
        System.out.println("Item removed from cart.");
    }

    public void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        System.out.println("\n Your Cart:");
        System.out.println("-".repeat(50));
        for (CartItem item : cart) {
            System.out.printf("%-20s x%d  = Rs.%.2f%n",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getSubtotal());
        }
        System.out.println("-".repeat(50));
        System.out.printf("%-20s       Rs.%.2f%n", "Subtotal", getSubtotal());
    }

    public double getSubtotal() {
        return cart.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    public List<CartItem> getCart() { return cart; }
    public void clearCart()         { cart.clear(); }
    public boolean isEmpty()        { return cart.isEmpty(); }
}