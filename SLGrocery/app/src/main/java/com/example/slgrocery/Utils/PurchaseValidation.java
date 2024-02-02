package com.example.slgrocery.Utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PurchaseValidation {
    private final String itemCode;
    private final String purchaseQuantity;
    private final String purchaseDate;

    public String errorMessage;

    public PurchaseValidation(String itemCode, String purchaseQuantity, String purchaseDate) {
        this.itemCode = itemCode;
        this.purchaseQuantity = purchaseQuantity;
        this.purchaseDate = purchaseDate;
    }

    public boolean itemCodeValidation() {
        errorMessage = null;
        try {
            int intQuantity = Integer.parseInt(itemCode);
            if (intQuantity <= 0) {
                errorMessage = "Invalid item code";
                return false;
            }
        } catch (Exception e) {
            errorMessage = "Item code should be integer";
            return false;
        }
        return true;
    }

    public boolean quantityValidation() {
        errorMessage = null;
        try {
            int intQuantity = Integer.parseInt(purchaseQuantity);
            if (intQuantity <= 0) {
                errorMessage = "Quantity should be greater than 0";
                return false;
            }
        } catch (Exception e) {
            errorMessage = "Quantity should be integer";
            return false;
        }
        return true;
    }

    public boolean dateValidation() {
        errorMessage = null;
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("y/M/d");
            Date saleDate = simpleDateFormat.parse(purchaseDate);
            if ((new Date()).after(saleDate)) {
                errorMessage = "The purchase date should after current date";
                return false;
            }
        } catch (Exception e) {
            errorMessage = "Incorrect date format";
            return false;
        }
        return true;
    }
}
