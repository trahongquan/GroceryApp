    package com.example.slgrocery.Fragments;

    import android.annotation.SuppressLint;
    import android.database.Cursor;
    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.EditText;
    import android.widget.RadioGroup;

    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentTransaction;

    import com.example.slgrocery.DbHelper;
    import com.example.slgrocery.R;
    import com.example.slgrocery.Utils.Dialog;
    import com.example.slgrocery.Utils.SearchValidation;
    import com.example.slgrocery.databinding.FragmentSearchBinding;
    import com.example.slgrocery.databinding.SearchResultBinding;

    public class SearchFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

        FragmentSearchBinding fragmentSearchBinding;
        boolean isSearchWithName = true;
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
            fragmentSearchBinding.fragmentSearchRadioGroup.setOnCheckedChangeListener(this);
            fragmentSearchBinding.fragmentRadiobuttonSearchName.setChecked(true);
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
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == fragmentSearchBinding.fragmentRadiobuttonSearchName.getId()) {
                isSearchWithName= true;
            } else if (checkedId == fragmentSearchBinding.fragmentRadiobuttonSearchCode.getId()) {
                isSearchWithName = false;
            }
        }
    }