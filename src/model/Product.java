package model;

public class Product {
    private int productId;
    private String name;
    private String category;
    private double price;
    private int stock;
    private String imageUrl; // ← ADD THIS

    public Product(int productId, String name, String category,
                   double price, int stock, String imageUrl) {
        this.productId = productId;
        this.name      = name;
        this.category  = category;
        this.price     = price;
        this.stock     = stock;
        this.imageUrl  = imageUrl; // ← ADD THIS
    }

    // keep all existing getters, add this new one:
    public int getProductId()    { return productId; }
    public String getName()      { return name; }
    public String getCategory()  { return category; }
    public double getPrice()     { return price; }
    public int getStock()        { return stock; }
    public void setStock(int s)  { this.stock = s; }
    public String getImageUrl()  { return imageUrl; } // ← ADD THIS

    @Override
    public String toString() {
        return String.format("[%d] %-20s | %-15s | Rs.%-8.2f | Stock: %d",
                productId, name, category, price, stock);
    }
}