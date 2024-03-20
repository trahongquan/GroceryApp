package com.example.slgrocery.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.slgrocery.DbHelper;
import com.example.slgrocery.HomeActivity;
import com.example.slgrocery.MyDatabaseService;
import com.example.slgrocery.R;
import com.example.slgrocery.Utils.Dialog;
import com.example.slgrocery.Utils.StockValidation;
import com.example.slgrocery.databinding.FragmentAddStockBinding;

public class AddStockFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    FragmentAddStockBinding fragmentAddStockBinding;
    boolean isTaxable = true;
    DbHelper dbHelper;
    HomeActivity homeActivity;
    public boolean result = false;
    /** Sử dụng BroadcastReceiver để thống báo service DB được hoàn thiện */
    private BroadcastReceiver AddStockResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            result = intent.getBooleanExtra("addStock_result",false);
            // Xử lý kết quả mua hàng nhận được (ví dụ: cập nhật giao diện, hiển thị thông báo)
            if (result){
                Toast.makeText(homeActivity.getAppContext(), "Đã lưu vào List", Toast.LENGTH_SHORT).show();
                new Dialog("Create Stock Item Successfully", "The item has been added to database").show(
                        requireActivity().getSupportFragmentManager(), "AddStockSuccessfully"
                );
            } else {
                Toast.makeText(homeActivity.getAppContext(), "Lỗi lưu vào List", Toast.LENGTH_SHORT).show();
                new Dialog("Create Stock Item Failed", "Please try again").show(
                        requireActivity().getSupportFragmentManager(), "AddStockFailed"
                );
            }
            Log.i("addStock_result", String.valueOf(result));
        }
    };

    @Override /** Lấy context từ Activity chứa fragmentPurchaseBinding */
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentAddStockBinding = FragmentAddStockBinding.inflate(inflater, container, false);
        IntentFilter filter = new IntentFilter("com.example.addStock_result");
        requireActivity().registerReceiver(AddStockResultReceiver, filter);
        init();
        return fragmentAddStockBinding.getRoot();
    }

    private void init() {
        dbHelper = new DbHelper(getContext());
        fragmentAddStockBinding.fragmentAddStockSaveBtn.setOnClickListener(this);
        fragmentAddStockBinding.fragmentAddStockCancelBtn.setOnClickListener(this);
        fragmentAddStockBinding.fragmentAddStockTaxableRadioGroup.setOnCheckedChangeListener(this);
        // set taxable as true as default
        fragmentAddStockBinding.fragmentAddStockTaxableYes.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        // save the new stock item info
        if (v.getId() == fragmentAddStockBinding.fragmentAddStockSaveBtn.getId()) {
            EditText itemNameInputController = fragmentAddStockBinding.fragmentAddStockItemName;
            EditText quantityInputController = fragmentAddStockBinding.fragmentAddStockItemQuantity;
            EditText priceInputController = fragmentAddStockBinding.fragmentAddStockItemPrice;
            String itemName = itemNameInputController.getText().toString();
            String quantity = quantityInputController.getText().toString();
            String price = priceInputController.getText().toString();
            // reset errors
            itemNameInputController.setError(null);
            quantityInputController.setError(null);
            priceInputController.setError(null);
            // validation
            StockValidation stockValidation = new StockValidation(itemName, quantity, price);
            boolean isItemNameValid = stockValidation.itemNameValidation();
            if (!isItemNameValid) {
                itemNameInputController.setError(stockValidation.errorMessage);
            }
            if (itemNameDuplicateValidation(itemName)) {
                itemNameInputController.setError("Item name is already exits");
            }
            boolean isQuantityValid = stockValidation.quantityValidation();
            if (!isQuantityValid) {
                quantityInputController.setError(stockValidation.errorMessage);
            }
            boolean isPriceValid = stockValidation.priceValidation();
            if (!isPriceValid) {
                priceInputController.setError(stockValidation.errorMessage);
            }
            if (isItemNameValid && isQuantityValid && isPriceValid && !itemNameDuplicateValidation(itemName)) {
                // add stock item into database
//                StockItem stockItem = new StockItem();
//                stockItem.itemName = itemName;
//                stockItem.quantity = quantity;
//                stockItem.price = price;
//                stockItem.taxable = isTaxable;

                /** Xử lý DB ở service */
                Intent intent = new Intent(homeActivity.getAppContext(), MyDatabaseService.class); // Thay thế "com.example.purchase_action" bằng action của Service
                intent.putExtra("operation", "addStock");
                intent.putExtra("itemName", itemName);
                intent.putExtra("quantity", quantity);
                intent.putExtra("price", price);
                intent.putExtra("taxable", isTaxable);
                // Sử dụng ngữ cảnh của Activity để khởi động Service
                homeActivity.getAppContext().startService(intent);
                Log.i("addStock_result", String.valueOf(result));

//                if (!result) {
//                    new Dialog("Create Stock Item Successfully", "The item has been added to database").show(
//                            requireActivity().getSupportFragmentManager(), "AddStockSuccessfully"
//                    );
//                } else {
//                    new Dialog("Create Stock Item Failed", "Please try again").show(
//                            requireActivity().getSupportFragmentManager(), "AddStockFailed"
//                    );
//                }
            } else {
                new Dialog("Validate Failed", "Please modify the information of the stock item").show(
                        requireActivity().getSupportFragmentManager(), "AddStockFailed"
                );
            }
            // turn back to the home fragment
        } else if (v.getId() == fragmentAddStockBinding.fragmentAddStockCancelBtn.getId()) {
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.home_frame_layout, new MainFragment());
            fragmentTransaction.commit();
        }
    }

    // when the radio group radio items have been clicked
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == fragmentAddStockBinding.fragmentAddStockTaxableYes.getId()) {
            isTaxable = true;
        } else if (checkedId == fragmentAddStockBinding.fragmentAddStockTaxableNo.getId()) {
            isTaxable = false;
        }
    }
    private boolean itemNameDuplicateValidation(String itemName){
        boolean checkStock = dbHelper.hasStockItemByName(itemName);
        return checkStock;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().unregisterReceiver(AddStockResultReceiver);
    }
}