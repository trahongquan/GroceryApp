package com.example.slgrocery.Fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.slgrocery.DbHelper;
import com.example.slgrocery.R;
import com.example.slgrocery.Utils.Dialog;
import com.example.slgrocery.Utils.SearchValidation;
import com.example.slgrocery.databinding.FragmentSearchBinding;
import com.example.slgrocery.databinding.SearchResultBinding;

public class SearchFragment extends Fragment implements View.OnClickListener {

    FragmentSearchBinding fragmentSearchBinding;
    DbHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentSearchBinding = FragmentSearchBinding.inflate(inflater, container, false);
        init();
        return fragmentSearchBinding.getRoot();
    }

    private void init() {
        dbHelper = new DbHelper(getContext());
        fragmentSearchBinding.fragmentSearchSearchBtn.setOnClickListener(this);
        fragmentSearchBinding.fragmentSearchCancelBtn.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void generateStockView(Cursor cursor) {
        SearchResultBinding searchResultBinding = SearchResultBinding.inflate(getLayoutInflater());
        searchResultBinding.searchItemCode.setText(cursor.getString(0));
        searchResultBinding.searchItemName.setText(cursor.getString(1));
        searchResultBinding.searchItemQuantity.setText(cursor.getString(2));
        searchResultBinding.searchItemPrice.setText("$" + cursor.getString(3));
        searchResultBinding.searchItemTaxable.setText(cursor.getInt(4) == 1 ? "Yes" : "No");
        fragmentSearchBinding.fragmentSearchFrame.addView(searchResultBinding.getRoot());
    }

    @Override
    public void onClick(View v) {
        // Search Button Click Event
        if (v.getId() == fragmentSearchBinding.fragmentSearchSearchBtn.getId()) {
            EditText itemCodeInputController = fragmentSearchBinding.fragmentSearchItemCode;
            String itemCode = itemCodeInputController.getText().toString();
            // validation
            SearchValidation searchValidation = new SearchValidation(itemCode);
            boolean isItemCodeValid = searchValidation.itemCodeValidation();
            if (!isItemCodeValid) {
                itemCodeInputController.setError(searchValidation.errorMessage);
            } else {
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
        // Cancel Button Click Event
        else if (v.getId() == fragmentSearchBinding.fragmentSearchCancelBtn.getId()) {
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            /**Fragment Search hiện tại bị xóa và MainFragment sẽ được hiển thị trong layout R.id.home_frame_layout*/
            fragmentTransaction.replace(R.id.home_frame_layout, new MainFragment());
            fragmentTransaction.commit(); // commit để xác nhận hành động
        }
    }
}