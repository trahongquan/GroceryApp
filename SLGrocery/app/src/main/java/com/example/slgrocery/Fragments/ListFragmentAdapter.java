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
        /** Tạo các ViewHolder: Phương thức onCreateViewHolder được gọi khi cần tạo một ViewHolder mới để hiển thị dữ liệu
         * cho mục hàng trong danh sách. Nó sử dụng LayoutInflater để tạo ra một View từ layout đã được định nghĩa
         * trong file StockListItemBinding và trả về một ViewHolder mới chứa View này.*/
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        StockListItemBinding stockListItemBinding = StockListItemBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(stockListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        /** Gắn dữ liệu vào ViewHolder: Phương thức onBindViewHolder được gọi để gắn dữ liệu từ mục hàng tương ứng
         * trong danh sách vào ViewHolder. Nó gọi phương thức bindView của ViewHolder và
         * truyền dữ liệu từ StockItem vào các trường dữ liệu của ViewHolder. */
        ((ViewHolder) holder).bindView(stockItemList.get(position));
    }


    @Override
    public int getItemCount() {
        return stockItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        /**  sử dụng một lớp con ViewHolder được định nghĩa bên trong nó để giữ các tham chiếu
         * đến các thành phần giao diện của mỗi mục hàng trong danh sách. Phương thức bindView của ViewHolder
         * được sử dụng để gắn dữ liệu từ StockItem vào các thành phần giao diện tương ứng. */
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
