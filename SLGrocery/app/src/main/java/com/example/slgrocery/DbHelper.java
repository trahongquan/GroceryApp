package com.example.slgrocery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.slgrocery.Models.Purchase;
import com.example.slgrocery.Models.Sale;
import com.example.slgrocery.Models.StockItem;
import com.example.slgrocery.Models.User;

public class DbHelper extends SQLiteOpenHelper {
    // db settings
    private static final String TAG = "DbDebug";
    private static final String DBNAME = "SLGrocery.db";
    private static final int VERSION = 1;
    // User Table Fields
    private final String userTableName = "USER";
    private final String userCol1 = "userId";
    private final String userCol2 = "username";
    private final String userCol3 = "emailId";
    private final String userCol4 = "password";
    // Stock Table Fields
    private final String stockTableName = "STOCK";
    private final String stockCol1 = "itemCode";
    private final String stockCol2 = "itemName";
    private final String stockCol3 = "qtyStock";
    private final String stockCol4 = "price";
    private final String stockCol5 = "taxable";
    // Sales Table Fields
    private final String saleTableName = "SALE";
    private final String saleCol1 = "orderNumber";
    private final String saleCol2 = "itemCode";
    private final String saleCol3 = "customerName";
    private final String saleCol4 = "customerEmail";
    private final String saleCol5 = "quantitySold";
    private final String saleCol6 = "dateOfSale";
    // Purchase Table Fields
    private final String purchaseTableName = "PURCHASE";
    private final String purchaseCol1 = "invoiceNumber";
    private final String purchaseCol2 = "itemCode";
    private final String purchaseCol3 = "qtyPurchased";
    private final String purchaseCol4 = "dateOfPurchase";

    /** Tạo DB */
    public DbHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    /** Tạo Table */
    private String createUserTable() {
        String query = "";
        query += "CREATE TABLE " + userTableName + " (";
        query += userCol1 + " INTEGER PRIMARY KEY AUTOINCREMENT,";
        query += userCol2 + " TEXT NOT NULL UNIQUE,";
        query += userCol3 + " TEXT NOT NULL UNIQUE,";
        query += userCol4 + " TEXT NOT NULL";
        query += ");";
        return query;
    }
    private String createStockTable() {
        String query = "";
        query += "CREATE TABLE " + stockTableName + " (";
        query += stockCol1 + " INTEGER PRIMARY KEY AUTOINCREMENT,";
        query += stockCol2 + " TEXT NOT NULL,";
        query += stockCol3 + " INTEGER NOT NULL,";
        query += stockCol4 + " FLOAT NOT NULL,";
        query += stockCol5 + " BOOLEAN NOT NULL";
        query += ");";
        return query;
    }
    private String createSaleTable() {
        String query = "";
        query += "CREATE TABLE " + saleTableName + " (";
        query += saleCol1 + " INTEGER PRIMARY KEY AUTOINCREMENT,";
        query += saleCol2 + " INTEGER NOT NULL,";
        query += saleCol3 + " TEXT NOT NULL,";
        query += saleCol4 + " TEXT NOT NULL,";
        query += saleCol5 + " INT NOT NULL,";
        query += saleCol6 + " DATE NOT NULL,";
        query += "FOREIGN KEY (" + saleCol2 + ") REFERENCES " + stockTableName + " (" + stockCol1 + ")";
        query += ");";
        return query;
    }
    private String createPurchaseTable() {
        String query = "";
        query += "CREATE TABLE " + purchaseTableName + " (";
        query += purchaseCol1 + " INTEGER PRIMARY KEY AUTOINCREMENT,";
        query += purchaseCol2 + " INTEGER NOT NULL,";
        query += purchaseCol3 + " INTEGER NOT NULL,";
        query += purchaseCol4 + " DATE NOT NULL,";
        query += "FOREIGN KEY (" + purchaseCol2 + ") REFERENCES " + stockTableName + " (" + stockCol1 + ")";
        query += ");";
        return query;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createUserTable());
        db.execSQL(createStockTable());
        db.execSQL(createSaleTable());
        db.execSQL(createPurchaseTable());
    }

    public void clearTables() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + userTableName);
        db.execSQL("DELETE FROM " + stockTableName);
        db.execSQL("DELETE FROM " + saleTableName);
        db.execSQL("DELETE FROM " + purchaseTableName);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + userTableName);
        db.execSQL("DROP TABLE IF EXISTS " + stockTableName);
        db.execSQL("DROP TABLE IF EXISTS " + saleTableName);
        db.execSQL("DROP TABLE IF EXISTS " + purchaseTableName);
        onCreate(db);
    }

    /** create a new user */
    public String createUser(User user) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(userCol2, user.username);
            cv.put(userCol3, user.email);
            cv.put(userCol4, user.password);
            long result = db.insertOrThrow(userTableName, null, cv);
            return result != -1 ? "DONE" : "";
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return e.getMessage();
        }
    }

    /** login checking */
    public boolean loginChecking(String email, String password) {
        SQLiteDatabase db = getWritableDatabase();

        String query = "";
        query += "SELECT * ";
        query += "FROM " + userTableName + " ";
        query += "WHERE " + userCol3 + "='" + email + "' ";
        query += "AND " + userCol4 + "='" + password + "';";

        Cursor cursor = db.rawQuery(query, null);
        return cursor.getCount() == 1;
    }

    /** get username By Email when login successfully */
    public String getUsernameByEmail(String email) {
        SQLiteDatabase db = getWritableDatabase();

        String query = "";
        query += "SELECT " + userCol2 + " FROM " + userTableName + " ";
        query += "WHERE " + userCol3 + "='" + email + "';";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            return cursor.getString(0);
        }
        return null;
    }


    /** create và get stock */
    public boolean createStockItem(StockItem stockItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(stockCol2, stockItem.itemName);
        cv.put(stockCol3, stockItem.quantity);
        cv.put(stockCol4, stockItem.price);
        cv.put(stockCol5, stockItem.taxable);
        return db.insert(stockTableName, null, cv) != -1;
    }

    public String createSale(Sale sale) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = getStockItemByItemCode(sale.itemCode);
        if (cursor.getCount() == 0) {
            return "Invalid Item Code";
        } else {
            cursor.moveToFirst();
            int stockQuantity = cursor.getInt(2);
            int decreasedQuantity = stockQuantity - Integer.parseInt(sale.quantitySold);
            if (decreasedQuantity < 0) {
                return "The Stock is Insufficient";
            } else {
                cv.put(saleCol2, sale.itemCode);
                cv.put(saleCol3, sale.customerName);
                cv.put(saleCol4, sale.customerEmail);
                cv.put(saleCol5, sale.quantitySold);
                cv.put(saleCol6, sale.dateOfSale);
                if ((db.insert(saleTableName, null, cv) != -1) &&
                        updateStockQuantity(sale.itemCode, decreasedQuantity)
                ) {
                    // update the quantity in the Stock Table
                    return "Save Sales Successfully";
                } else {
                    return "Save Sales Failed";
                }
            }
        }
    }

    private boolean updateStockQuantity(String itemCode, int updatedQuantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(stockCol3, updatedQuantity);
        return db.update(stockTableName, cv, stockCol1 + "=" + itemCode, null) == 1;
    }

    public Cursor getStockItemByItemCode(String itemCode) {
        SQLiteDatabase db = getWritableDatabase();

        String query = "";
        query += "SELECT * FROM " + stockTableName + " ";
        query += "WHERE " + stockCol1 + "='" + itemCode + "';";

        return db.rawQuery(query, null);
    }

    public Cursor getStockItemByName(String name) {
        SQLiteDatabase db = getWritableDatabase();

        String query = "";
        query += "SELECT * FROM " + stockTableName + " ";
        query += "WHERE " + stockCol2 + "='" + name + "';";
        return db.rawQuery(query, null);
    }


    /** Kiểm tra trùng tên Stock */
    public boolean hasStockItemByName(String name) {
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT COUNT(*) FROM " + stockTableName + " ";
        query += "WHERE " + stockCol2 + "='" + name + "';";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        return count > 0;
    }

    public String createPurchase(Purchase purchase) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = getStockItemByItemCode(purchase.itemCode);
        if (cursor.getCount() == 0) {
            return "Invalid Item Code";
        } else {
            cursor.moveToFirst();
            int stockQuantity = cursor.getInt(2);
            int increasedQuantity = stockQuantity + Integer.parseInt(purchase.purchaseQuantity);
            cv.put(purchaseCol2, purchase.itemCode);
            cv.put(purchaseCol3, purchase.purchaseQuantity);
            cv.put(purchaseCol4, purchase.purchaseDate);
            if ((db.insert(purchaseTableName, null, cv) != -1) &&
                    updateStockQuantity(purchase.itemCode, increasedQuantity)) {
                return "Save Purchase Successfully";
            } else {
                return "Save Purchase Failed";
            }
        }
    }

    public Cursor getStockItems() { /** Lấy list stock */
        SQLiteDatabase db = getWritableDatabase();

        String query = "";
        query += "SELECT * FROM " + stockTableName + " ;";

        return db.rawQuery(query, null);
    }
}
