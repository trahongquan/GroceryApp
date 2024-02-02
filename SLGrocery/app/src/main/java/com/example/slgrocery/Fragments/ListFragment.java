package com.example.slgrocery.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.slgrocery.DbHelper;
import com.example.slgrocery.Models.StockItem;
import com.example.slgrocery.R;
import com.example.slgrocery.databinding.FragmentListBinding;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment implements View.OnClickListener {

    FragmentListBinding fragmentListBinding;
    DbHelper dbHelper;
    List<StockItem> stockItemList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentListBinding = FragmentListBinding.inflate(inflater, container, false);
        init();
        return fragmentListBinding.getRoot();
    }

    private void storeStockItems() {
        dbHelper = new DbHelper(getContext());
        Cursor cursor = dbHelper.getStockItems();
        if (cursor.getCount() == 0) {
            Toast.makeText(getContext(), "There's no item in stock", Toast.LENGTH_LONG).show();
        } else {
            cursor.moveToFirst();
            do {
                StockItem stockItem = new StockItem();
                stockItem.itemCode = cursor.getString(0);
                stockItem.itemName = cursor.getString(1);
                stockItem.quantity = cursor.getString(2);
                stockItem.price = cursor.getString(3);
                stockItem.taxable = cursor.getInt(4) == 1;
                stockItemList.add(stockItem);
            } while (cursor.moveToNext());
        }
    }

    private void init() {
        // get stock item list
        storeStockItems();
        // button
        fragmentListBinding.fragmentListCancelBtn.setOnClickListener(this);
        // Set Recycler Layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        fragmentListBinding.fragmentListRecyclerView.setLayoutManager(linearLayoutManager);
        ListFragmentAdapter listFragmentAdapter = new ListFragmentAdapter(stockItemList);
        fragmentListBinding.fragmentListRecyclerView.setAdapter(listFragmentAdapter);
        listFragmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == fragmentListBinding.fragmentListCancelBtn.getId()) {
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.home_frame_layout, new MainFragment());
            fragmentTransaction.commit();
        }
    }
}