package com.example.slgrocery.Fragments;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.slgrocery.Models.StockItem;
import com.example.slgrocery.databinding.StockListItemBinding;

import java.util.List;

public class ListFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<StockItem> stockItemList;

    public ListFragmentAdapter(List<StockItem> stockItemList) {
        this.stockItemList = stockItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        StockListItemBinding stockListItemBinding = StockListItemBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(stockListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bindView(stockItemList.get(position));
    }


    @Override
    public int getItemCount() {
        return stockItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        StockListItemBinding stockListItemBinding;

        public ViewHolder(@NonNull StockListItemBinding stockListItemBinding) {
            super(stockListItemBinding.getRoot());
            this.stockListItemBinding = stockListItemBinding;
        }

        public void bindView(StockItem stockItem) {
            stockListItemBinding.gridCol1.setText(stockItem.itemCode);
            stockListItemBinding.gridCol2.setText(stockItem.itemName);
            stockListItemBinding.gridCol3.setText(stockItem.quantity);
            stockListItemBinding.gridCol4.setText(stockItem.price);
            stockListItemBinding.gridCol5.setText(stockItem.taxable ? "Yes" : "No");
        }
    }
}
