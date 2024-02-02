package com.example.slgrocery.Utils;

public class SearchValidation {
    private final String itemCode;
    public String errorMessage;

    public SearchValidation(String itemCode) {
        this.itemCode = itemCode;
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
}
