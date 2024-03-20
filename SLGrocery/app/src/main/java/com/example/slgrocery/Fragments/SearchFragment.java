    package com.example.slgrocery.Fragments;

    import android.annotation.SuppressLint;
    import android.app.AlertDialog;
    import android.content.BroadcastReceiver;
    import android.content.Context;
    import android.content.Intent;
    import android.content.IntentFilter;
    import android.database.Cursor;
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
    import com.example.slgrocery.Utils.SearchValidation;
    import com.example.slgrocery.databinding.FragmentSearchBinding;
    import com.example.slgrocery.databinding.SearchResultBinding;

    public class SearchFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

        FragmentSearchBinding fragmentSearchBinding;
        boolean isSearchWithName = true;
        DbHelper dbHelper;
        HomeActivity homeActivity;
        SearchResultBinding searchResultBinding;
        BroadcastReceiver delStockResultReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean result = intent.getBooleanExtra("delStock_result", false);
                if (result) {
                    // Xóa thành công
                    Toast.makeText(homeActivity.getAppContext(), "Stock item deleted", Toast.LENGTH_SHORT).show();
                } else {
                    // Xóa không thành công
                    Toast.makeText(homeActivity.getAppContext(), "Failed to delete stock item", Toast.LENGTH_SHORT).show();
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
            fragmentSearchBinding = FragmentSearchBinding.inflate(inflater, container, false);
// Đăng ký BroadcastReceiver với action tương ứng
            IntentFilter delStockResultFilter = new IntentFilter("com.example.delStock_result");
            requireActivity().registerReceiver(delStockResultReceiver, delStockResultFilter);
            init();
            return fragmentSearchBinding.getRoot();
        }

        private void init() {
            dbHelper = new DbHelper(getContext());
            fragmentSearchBinding.fragmentSearchSearchBtn.setOnClickListener(this);
            fragmentSearchBinding.fragmentSearchCancelBtn.setOnClickListener(this);
//            fragmentSearchBinding.fragmentSearchDelBtn.setOnClickListener(this);
            fragmentSearchBinding.fragmentSearchRadioGroup.setOnCheckedChangeListener(this);
            fragmentSearchBinding.fragmentRadiobuttonSearchName.setChecked(true);
        }

        @SuppressLint("SetTextI18n")
        private void generateStockView(Cursor cursor) {
            searchResultBinding = SearchResultBinding.inflate(getLayoutInflater());
            searchResultBinding.searchItemCode.setText(cursor.getString(0));
            searchResultBinding.searchItemName.setText(cursor.getString(1));
            searchResultBinding.searchItemQuantity.setText(cursor.getString(2));
            searchResultBinding.searchItemPrice.setText("$" + cursor.getString(3));
            searchResultBinding.searchItemTaxable.setText(cursor.getInt(4) == 1 ? "Yes" : "No");
            searchResultBinding.fragmentSearchDelBtn.setOnClickListener(this);
            fragmentSearchBinding.fragmentSearchFrame.addView(searchResultBinding.getRoot());
        }

        @Override
        public void onClick(View v) {
            // Search Button Click Event
            if (v.getId() == fragmentSearchBinding.fragmentSearchSearchBtn.getId()) {
                EditText itemCodeInputController = fragmentSearchBinding.fragmentSearchItemCode;
                if(isSearchWithName) {
                    String itemName = itemCodeInputController.getText().toString();
                    fragmentSearchBinding.fragmentSearchFrame.removeAllViews();
                    Cursor cursor = dbHelper.getStockItemByName(itemName);
                    if (cursor.getCount() <= 0) {
                        new Dialog("Alert", "Item Not Found").show(
                                requireActivity().getSupportFragmentManager(), "NoResult"
                        );
                    } else {
                        cursor.moveToFirst();
                        generateStockView(cursor);
                    }
                } else {
                    String itemCode = itemCodeInputController.getText().toString();
                    // validation
                    SearchValidation searchValidation = new SearchValidation(itemCode);
                    boolean isItemCodeValid = searchValidation.itemCodeValidation();
                    if (!isItemCodeValid) {
                        itemCodeInputController.setError(searchValidation.errorMessage);
                    } else { /** Tìm theo code */
                        fragmentSearchBinding.fragmentSearchFrame.removeAllViews();
                        Cursor cursor = dbHelper.getStockItemByItemCode(itemCode);
                        if (cursor.getCount() <= 0) {
                            new Dialog("Alert", "Item Not Found").show(
                                    requireActivity().getSupportFragmentManager(), "NoResult"
                            );
                        } else {
                            cursor.moveToFirst();
                            generateStockView(cursor);
                        }
                    }
                }
            }
            // Cancel Button Click Event
            else if (v.getId() == fragmentSearchBinding.fragmentSearchCancelBtn.getId()) {
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                /**Fragment Search hiện tại bị xóa và MainFragment sẽ được hiển thị trong layout R.id.home_frame_layout*/
                fragmentTransaction.replace(R.id.home_frame_layout, new MainFragment());
                fragmentTransaction.commit(); // commit để xác nhận hành động
            }
            // Del Button Click Event
            else if (v.getId() == searchResultBinding.fragmentSearchDelBtn.getId()) {
                EditText itemCodeInputController = fragmentSearchBinding.fragmentSearchItemCode;
                if(isSearchWithName) {
                    String itemName = itemCodeInputController.getText().toString();
                    Cursor cursor = dbHelper.getStockItemByName(itemName);
                    if (cursor.getCount() <= 0) {
                        new Dialog("Alert", "Item Not Found").show(
                            requireActivity().getSupportFragmentManager(), "NoResult"
                        );
                    } else {
                        cursor.moveToFirst();
                        DelByItemCode(cursor.getString(0));
                    }
                } else {
                    String itemCode = itemCodeInputController.getText().toString();
                    SearchValidation searchValidation = new SearchValidation(itemCode);
                    boolean isItemCodeValid = searchValidation.itemCodeValidation();
                    if (!isItemCodeValid) {
                        itemCodeInputController.setError(searchValidation.errorMessage);
                    } else {
                        Cursor cursor = dbHelper.getStockItemByItemCode(itemCode);
                        if (cursor.getCount() <= 0) {
                            new Dialog("Alert", "Item Not Found").show(
                                    requireActivity().getSupportFragmentManager(), "NoResult"
                            );
                        } else {
                            DelByItemCode(itemCode);
                        }
                    }
                }
            }
        }
        private void DelByItemCode(String itemCode) {
            // Confirmation dialog before deletion
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("Are you sure you want to delete this item?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                /** Xử lý DB ở service */
                Intent intent = new Intent(homeActivity.getAppContext(), MyDatabaseService.class); // Thay thế "com.example.purchase_action" bằng action của Service
                intent.putExtra("operation", "delStcok");
                intent.putExtra("itemCode", itemCode);
                homeActivity.getAppContext().startService(intent);
                fragmentSearchBinding.fragmentSearchFrame.removeAllViews();
                fragmentSearchBinding.fragmentSearchItemCode.setText("");
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == fragmentSearchBinding.fragmentRadiobuttonSearchName.getId()) {
                isSearchWithName= true;
            } else if (checkedId == fragmentSearchBinding.fragmentRadiobuttonSearchCode.getId()) {
                isSearchWithName = false;
            }
        }
        @Override
        public void onDestroyView() {
            super.onDestroyView();
            requireActivity().unregisterReceiver(delStockResultReceiver);
        }
    }