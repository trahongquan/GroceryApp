package com.example.slgrocery.Utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaleValidation {
    private final String itemCode;
    private final String customerName;
    private final String customerEmail;
    private final String quantitySold;
    private final String dateOfSale;
    public String errorMessage;

    public SaleValidation(String itemCode, String customerName, String customerEmail, String quantitySold, String dateOfSale) {
        this.itemCode = itemCode;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.quantitySold = quantitySold;
        this.dateOfSale = dateOfSale;
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

    public boolean customerNameValidation() {
        errorMessage = null;
        if (customerName.trim().isEmpty()) {
            errorMessage = "Customer name should not be empty";
            return false;
        }
        return true;
    }

    public boolean customerEmailValidation() {
        errorMessage = null;
        Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        Matcher matcher = pattern.matcher(customerEmail);
        if (!matcher.matches()) {
            errorMessage = "Email Format is incorrect";
            return false;
        }
        return true;
    }

    public boolean quantityValidation() {
        errorMessage = null;
        try {
            int intQuantity = Integer.parseInt(quantitySold);
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
            Date saleDate = simpleDateFormat.parse(dateOfSale);
            if ((new Date()).after(saleDate)) {
                errorMessage = "The sale date should after current date";
                return false;
            }
        } catch (Exception e) {
            errorMessage = "Incorrect date format";
            return false;
        }
        return true;
    }
}
