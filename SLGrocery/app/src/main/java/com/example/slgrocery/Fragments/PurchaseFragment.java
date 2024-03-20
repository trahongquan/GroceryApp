package com.example.slgrocery.Fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static androidx.core.content.ContextCompat.registerReceiver;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.slgrocery.DbHelper;
import com.example.slgrocery.HomeActivity;
import com.example.slgrocery.MyDatabaseService;
import com.example.slgrocery.R;
import com.example.slgrocery.Utils.Dialog;
import com.example.slgrocery.Utils.PurchaseValidation;
import com.example.slgrocery.databinding.FragmentPurchaseBinding;

import java.util.Calendar;

public class PurchaseFragment extends Fragment implements View.OnClickListener {
    FragmentPurchaseBinding fragmentPurchaseBinding;
    DbHelper dbHelper;
    HomeActivity homeActivity;
    public String result ="";
    /** Sử dụng BroadcastReceiver để thống báo service DB được hoàn thiện */
    private BroadcastReceiver purchaseResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            result = intent.getStringExtra("purchase_result");
            // Xử lý kết quả mua hàng nhận được (ví dụ: cập nhật giao diện, hiển thị thông báo)
            Toast.makeText(homeActivity.getAppContext(), result, Toast.LENGTH_SHORT).show();
            Log.i("purchaseResultReceiver", result);

            if (result.equals("Invalid Item Code") || result.equals("Save Purchase Failed")) {
                new Dialog("Purchase Failed", result).show(
                        requireActivity().getSupportFragmentManager(), "PurchaseFailed"
                );
            } else {
                new Dialog("Purchase Successfully", "New purchase item has been added into database").show(
                        requireActivity().getSupportFragmentManager(), "PurchaseSuccessfully"
                );
            }
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
        fragmentPurchaseBinding = FragmentPurchaseBinding.inflate(inflater, container, false);
        init();
        IntentFilter filter = new IntentFilter("com.example.purchase_result");
        requireActivity().registerReceiver(purchaseResultReceiver, filter);
        return fragmentPurchaseBinding.getRoot();
    }

    private void init() {
        dbHelper = new DbHelper(getContext());
        fragmentPurchaseBinding.fragmentPurchaseSubmitBtn.setOnClickListener(this);
        fragmentPurchaseBinding.fragmentPurchaseCancelBtn.setOnClickListener(this);
        // set date picker
        fragmentPurchaseBinding.fragmentPurchaseDate.setInputType(InputType.TYPE_NULL);
        fragmentPurchaseBinding.fragmentPurchaseDate.setFocusable(false);
        fragmentPurchaseBinding.fragmentPurchaseDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // date picker
        if (v.getId() == fragmentPurchaseBinding.fragmentPurchaseDate.getId()) {
            Calendar calendar = Calendar.getInstance();
            int purchaseDate = calendar.get(Calendar.DAY_OF_MONTH);
            int purchaseMonth = calendar.get(Calendar.MONDAY);
            int purchaseYear = calendar.get(Calendar.YEAR);
            @SuppressLint("DefaultLocale") DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(), (view, year, month, dayOfMonth) ->
                    fragmentPurchaseBinding.fragmentPurchaseDate.setText(String.format("%d/%d/%d", year, month +1 , dayOfMonth)),
                    purchaseYear, purchaseMonth, purchaseDate);
            datePickerDialog.show();
        }
        // Submit Button Click Event
        else if (v.getId() == fragmentPurchaseBinding.fragmentPurchaseSubmitBtn.getId()) {
            EditText itemCodeInputController = fragmentPurchaseBinding.fragmentPurchaseItemCode;
            EditText purchaseQuantityInputController = fragmentPurchaseBinding.fragmentPurchaseQuantity;
            EditText purchaseDateInputController = fragmentPurchaseBinding.fragmentPurchaseDate;
            String itemCode = itemCodeInputController.getText().toString();
            String purchaseQuantity = purchaseQuantityInputController.getText().toString();
            String purchaseDate = purchaseDateInputController.getText().toString();
            // reset errors
            itemCodeInputController.setError(null);
            purchaseQuantityInputController.setError(null);
            purchaseDateInputController.setError(null);
            // validation
            PurchaseValidation purchaseValidation = new PurchaseValidation(itemCode, purchaseQuantity, purchaseDate);
            boolean isItemCodeValid = purchaseValidation.itemCodeValidation();
            if (!isItemCodeValid) {
                itemCodeInputController.setError(purchaseValidation.errorMessage);
            }
            boolean isPurchaseQuantityValid = purchaseValidation.quantityValidation();
            if (!isPurchaseQuantityValid) {
                purchaseQuantityInputController.setError(purchaseValidation.errorMessage);
            }
            boolean isPurchaseDateValid = purchaseValidation.dateValidation();
            if (!isPurchaseDateValid) {
                purchaseDateInputController.setError(purchaseValidation.errorMessage);
            }
            if (isItemCodeValid && isPurchaseQuantityValid) {
                if (isPurchaseDateValid) {
                    /** Xử lý DB ở service */
                    Intent intent = new Intent(homeActivity.getAppContext(), MyDatabaseService.class); // Thay thế "com.example.purchase_action" bằng action của Service
                    intent.putExtra("operation", "create_purchase");
                    intent.putExtra("itemCode", itemCode);
                    intent.putExtra("purchaseQuantity", purchaseQuantity);
                    intent.putExtra("purchaseDate", purchaseDate);
                    // Sử dụng ngữ cảnh của Activity để khởi động Service
                    homeActivity.getAppContext().startService(intent);
                        Log.i("purchaseResultReceiverResult", result);

                } else {
                    new Dialog("Validate Failed", purchaseValidation.errorMessage).show(
                            requireActivity().getSupportFragmentManager(), "PurchaseFailed"
                    );
                }
            } else {
                new Dialog("Validate Failed", "Please modify the information of the form").show(
                        requireActivity().getSupportFragmentManager(), "PurchaseFailed"
                );
            }
            result = "";
        }
        // Cancel Button Click Event
        else if (v.getId() == fragmentPurchaseBinding.fragmentPurchaseCancelBtn.getId()) {
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.home_frame_layout, new MainFragment());
            fragmentTransaction.commit();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().unregisterReceiver(purchaseResultReceiver); // Hủy đăng ký để tránh rò rỉ bộ nhớ
    }
}