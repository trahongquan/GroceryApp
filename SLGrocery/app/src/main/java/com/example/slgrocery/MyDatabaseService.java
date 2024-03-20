package com.example.slgrocery;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.slgrocery.DbHelper;
import com.example.slgrocery.Models.Purchase;
import com.example.slgrocery.Models.Sale;
import com.example.slgrocery.Models.StockItem;
import com.example.slgrocery.Models.User;

import java.util.ArrayList;

// MyDatabaseService.java
public class MyDatabaseService extends Service {
    private DbHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DbHelper(this);
    }
    public void sendBroadcastIntent(String actionIntent, String name, String result){
        // Tạo và gửi phát sóng với kết quả
        Intent broadcastIntent = new Intent(actionIntent);
        broadcastIntent.putExtra(name, result);
        sendBroadcast(broadcastIntent);
    }
    public void sendBroadcastIntent(String actionIntent, String name, Boolean result){
        // Tạo và gửi phát sóng với kết quả
        Intent broadcastIntent = new Intent(actionIntent);
        broadcastIntent.putExtra(name, result);
        sendBroadcast(broadcastIntent);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(() -> {
            String result = "";
            // Lấy thao tác thực hiện từ intent
            String operation = intent.getStringExtra("operation");

            if (operation.equals("create_purchase")) {
                // Trích xuất chi tiết mua hàng từ intent
                String itemCode = intent.getStringExtra("itemCode");
                String purchaseQuantity = intent.getStringExtra("purchaseQuantity");
                String purchaseDate = intent.getStringExtra("purchaseDate");

                result = dbHelper.createPurchase(new Purchase(itemCode, purchaseQuantity, purchaseDate));
                // Gửi kết quả về activity thông qua cơ chế phát sóng hoặc gọi lại
                sendBroadcastIntent("com.example.purchase_result","purchase_result", result);
            } else if (operation.equals("create_sale")) {
                // Trích xuất chi tiết bán hàng từ intent
                Sale sale = new Sale();
                sale.itemCode = intent.getStringExtra("itemCode");
                sale.customerName = intent.getStringExtra("customerName");
                sale.customerEmail = intent.getStringExtra("customerEmail");
                sale.quantitySold = intent.getStringExtra("quantity");
                sale.dateOfSale = intent.getStringExtra("saleDate");
                result = dbHelper.createSale(sale);
                // Tạo và gửi phát sóng với kết quả
                sendBroadcastIntent("com.example.createSale_result","createSale_result", result);
            } else if (operation.equals("addStock")) {
                StockItem stockItem = new StockItem();
                stockItem.itemName = intent.getStringExtra("itemName");
                stockItem.quantity = intent.getStringExtra("quantity");
                stockItem.price = intent.getStringExtra("price");
                stockItem.taxable = intent.getBooleanExtra("taxable", false);
                boolean resultBool = dbHelper.createStockItem(stockItem);
                // Tạo và gửi phát sóng với kết quả
                sendBroadcastIntent("com.example.addStock_result","addStock_result", resultBool);
            }else if (operation.equals("delStcok")) {
                // Trích xuất dữ liệu từ intent
                int stockId = Integer.parseInt(intent.getStringExtra("itemCode"));
                boolean resultBool = dbHelper.deleteStockItem(stockId);
                // Tạo và gửi phát sóng với kết quả
                sendBroadcastIntent("com.example.delStock_result","delStock_result", resultBool);
            }
            // ... xử lý các thao tác khác

        }).start();
        /*MyAsyncTask myTask = new MyAsyncTask(intent,dbHelper);
        myTask.execute();*/
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

