package com.example.slgrocery.Models;

public class Purchase {
    public String itemCode;
    public String purchaseQuantity;
    public String purchaseDate;

    public Purchase() {
    }

    public Purchase(String itemCode, String purchaseQuantity, String purchaseDate) {
        this.itemCode = itemCode;
        this.purchaseQuantity = purchaseQuantity;
        this.purchaseDate = purchaseDate;
    }
}
