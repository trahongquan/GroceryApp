package com.example.slgrocery;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.slgrocery.DbHelper;
import com.example.slgrocery.Models.Purchase;
import com.example.slgrocery.Models.Sale;

// MyDatabaseService.java
public class MyDatabaseService extends Service {
    private DbHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DbHelper(this);
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
                // Tạo và gửi phát sóng với kết quả
                Intent broadcastIntent = new Intent("com.example.purchase_result");
                broadcastIntent.putExtra("purchase_result", result);
                sendBroadcast(broadcastIntent);
            } else if (operation.equals("create_sale")) {
                // Trích xuất chi tiết bán hàng từ intent
                result = dbHelper.createSale(new Sale());
                // Tạo và gửi phát sóng với kết quả
                Intent broadcastIntent = new Intent("com.example.sale_result");
                broadcastIntent.putExtra("create_sale", result);
                sendBroadcast(broadcastIntent);
            }
            // ... xử lý các thao tác khác

        }).start();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

