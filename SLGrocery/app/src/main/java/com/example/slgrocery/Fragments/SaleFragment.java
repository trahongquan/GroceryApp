package com.example.slgrocery.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.slgrocery.DbHelper;
import com.example.slgrocery.Models.Sale;
import com.example.slgrocery.R;
import com.example.slgrocery.Utils.Dialog;
import com.example.slgrocery.Utils.SaleValidation;
import com.example.slgrocery.databinding.FragmentSaleBinding;

import java.util.Calendar;

public class SaleFragment extends Fragment implements View.OnClickListener {

    FragmentSaleBinding fragmentSaleBinding;
    DbHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentSaleBinding = FragmentSaleBinding.inflate(inflater, container, false);
        init();
        return fragmentSaleBinding.getRoot();
    }

    private void init() {
        dbHelper = new DbHelper(getContext());
        // Prepare date picker controller
        fragmentSaleBinding.fragmentSaleDate.setFocusable(false);
        fragmentSaleBinding.fragmentSaleDate.setInputType(InputType.TYPE_NULL);
        fragmentSaleBinding.fragmentSaleDate.setOnClickListener(this);
        // button click event
        fragmentSaleBinding.fragmentSaleSubmitBtn.setOnClickListener(this);
        fragmentSaleBinding.fragmentSaleCancelBtn.setOnClickListener(this);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View v) {
        // data picker dialog with the EditText Widget
        if (v.getId() == fragmentSaleBinding.fragmentSaleDate.getId()) {
            Calendar cal = Calendar.getInstance();
            int saleDay = cal.get(Calendar.DAY_OF_MONTH);
            int saleMonth = cal.get(Calendar.MONTH);
            int saleYear = cal.get(Calendar.YEAR);
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(), (view, year, month, dayOfMonth) ->
                    fragmentSaleBinding.fragmentSaleDate.setText(
                            String.format("%d/%d/%d", year, month + 1, dayOfMonth)), saleYear, saleMonth, saleDay
            );
            datePickerDialog.show();
        }
        // submit event
        if (v.getId() == fragmentSaleBinding.fragmentSaleSubmitBtn.getId()) {
            EditText itemCodeInputController = fragmentSaleBinding.fragmentSaleItemCode;
            EditText customerNameInputController = fragmentSaleBinding.fragmentSaleCustomerName;
            EditText customerEmailInputController = fragmentSaleBinding.fragmentSaleCustomerEmail;
            EditText quantityInputController = fragmentSaleBinding.fragmentSaleQuantity;
            EditText saleDateInputController = fragmentSaleBinding.fragmentSaleDate;
            String itemCode = itemCodeInputController.getText().toString();
            String customerName = customerNameInputController.getText().toString();
            String customerEmail = customerEmailInputController.getText().toString();
            String quantity = quantityInputController.getText().toString();
            String saleDate = saleDateInputController.getText().toString();
            // reset errors
            itemCodeInputController.setError(null);
            customerNameInputController.setError(null);
            customerEmailInputController.setError(null);
            quantityInputController.setError(null);
            saleDateInputController.setError(null);
            // validation
            SaleValidation saleValidation = new SaleValidation(itemCode, customerName, customerEmail, quantity, saleDate);
            boolean isItemCodeValid = saleValidation.itemCodeValidation();
            if (!isItemCodeValid) {
                itemCodeInputController.setError(saleValidation.errorMessage);
            }
            boolean isCustomerNameValid = saleValidation.customerNameValidation();
            if (!isCustomerNameValid) {
                customerNameInputController.setError(saleValidation.errorMessage);
            }
            boolean isCustomerEmailValid = saleValidation.customerEmailValidation();
            if (!isCustomerEmailValid) {
                customerEmailInputController.setError(saleValidation.errorMessage);
            }
            boolean isQuantityValid = saleValidation.quantityValidation();
            if (!isQuantityValid) {
                quantityInputController.setError(saleValidation.errorMessage);
            }
            boolean isSaleDateValid = saleValidation.dateValidation();
            if (!isSaleDateValid) {
                saleDateInputController.setError(saleValidation.errorMessage);
            }
            if (isItemCodeValid && isCustomerNameValid && isCustomerEmailValid && isQuantityValid) {
                if (isSaleDateValid) {
                    Sale sale = new Sale();
                    sale.itemCode = itemCode;
                    sale.customerName = customerName;
                    sale.customerEmail = customerEmail;
                    sale.quantitySold = quantity;
                    sale.dateOfSale = saleDate;
                    String result = dbHelper.createSale(sale);
                    String dialogTitle = "Sale Successfully";
                    if (result.equals("Invalid Item Code") || result.equals("The Stock is Insufficient") || result.equals("Save Sales Failed")) {
                        dialogTitle = "Sale Failed";
                    }
                    new Dialog(dialogTitle, result).show(
                            requireActivity().getSupportFragmentManager(), "SaleSuccessfully"
                    );
                } else {
                    // The Edit of date's input type is set to Null, This is used to display error message by dialog
                    new Dialog("Validate Failed", saleValidation.errorMessage).show(
                            requireActivity().getSupportFragmentManager(), "SaleFailed"
                    );
                }
            } else {
                new Dialog("Validate Failed", "Please modify the information of the form").show(
                        requireActivity().getSupportFragmentManager(), "SaleFailed"
                );
            }
        }
        // cancel event
        else if (v.getId() == fragmentSaleBinding.fragmentSaleCancelBtn.getId()) {
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.home_frame_layout, new MainFragment());
            fragmentTransaction.commit();
        }
    }
}