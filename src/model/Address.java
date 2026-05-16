package model;

public class Address {
    private int addressId;
    private int userId;
    private String fullName;
    private String phone;
    private String street;
    private String city;
    private String state;
    private String pincode;
    private boolean isDefault;

    public Address(int addressId, int userId, String fullName, String phone,
                   String street, String city, String state,
                   String pincode, boolean isDefault) {
        this.addressId = addressId;
        this.userId    = userId;
        this.fullName  = fullName;
        this.phone     = phone;
        this.street    = street;
        this.city      = city;
        this.state     = state;
        this.pincode   = pincode;
        this.isDefault = isDefault;
    }

    // Getters — names must match exactly what JS uses
    public int getAddressId()   { return addressId; }
    public int getUserId()      { return userId; }
    public String getFullName() { return fullName; }
    public String getPhone()    { return phone; }
    public String getStreet()   { return street; }
    public String getCity()     { return city; }
    public String getState()    { return state; }
    public String getPincode()  { return pincode; }

    // IMPORTANT — Gson uses this method name to create "default" field in JSON
    public boolean isDefault()  { return isDefault; }
}