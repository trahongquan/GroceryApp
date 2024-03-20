package com.example.slgrocery;

import android.content.Intent;
import android.os.AsyncTask;

import com.example.slgrocery.Models.Purchase;
import com.example.slgrocery.Models.Sale;
import com.example.slgrocery.Models.StockItem;

public class MyAsyncTask extends AsyncTask<Void, Void, String> {
    private Intent intent;
    private DbHelper dbHelper;

    public MyAsyncTask(Intent intent, DbHelper dbHelper) {
        this.intent = intent;
        this.dbHelper = dbHelper;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String result = "";
        String operation = intent.getStringExtra("operation");

        if (operation.equals("create_purchase")) {
            String itemCode = intent.getStringExtra("itemCode");
            String purchaseQuantity = intent.getStringExtra("purchaseQuantity");
            String purchaseDate = intent.getStringExtra("purchaseDate");

            result = dbHelper.createPurchase(new Purchase(itemCode, purchaseQuantity, purchaseDate));
        } else if (operation.equals("create_sale")) {
            Sale sale = new Sale();
            sale.itemCode = intent.getStringExtra("itemCode");
            sale.customerName = intent.getStringExtra("customerName");
            sale.customerEmail = intent.getStringExtra("customerEmail");
            sale.quantitySold = intent.getStringExtra("quantity");
            sale.dateOfSale = intent.getStringExtra("saleDate");

            result = dbHelper.createSale(sale);
        } else if (operation.equals("addStock")) {
            StockItem stockItem = new StockItem();
            stockItem.itemName = intent.getStringExtra("itemName");
            stockItem.quantity = intent.getStringExtra("quantity");
            stockItem.price = intent.getStringExtra("price");
            stockItem.taxable = intent.getBooleanExtra("taxable", false);

            boolean resultBool = dbHelper.createStockItem(stockItem);
            result = String.valueOf(resultBool);
        } else if (operation.equals("delStcok")) {
            int stockId = Integer.parseInt(intent.getStringExtra("itemCode"));

            boolean resultBool = dbHelper.deleteStockItem(stockId);
            result = String.valueOf(resultBool);
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        new MyDatabaseService().sendBroadcastIntent("com.example.purchase_result", "purchase_result", result);
    }
}