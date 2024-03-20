package com.example.slgrocery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.slgrocery.Fragments.AddStockFragment;
import com.example.slgrocery.Fragments.ListFragment;
import com.example.slgrocery.Fragments.MainFragment;
import com.example.slgrocery.Fragments.PurchaseFragment;
import com.example.slgrocery.Fragments.SaleFragment;
import com.example.slgrocery.Fragments.SearchFragment;
import com.example.slgrocery.databinding.ActivityHomeBinding;
import com.example.slgrocery.setting.SettingBiometric;
import com.example.slgrocery.setting.Settings;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityHomeBinding activityHomeBinding;
    ActionBarDrawerToggle actionBarDrawerToggle;

    /** Truyền Context từ Activity tới các fragment con */
    public Context getAppContext() {
        return getApplicationContext(); // Trả về ngữ cảnh ứng dụng của Activity
    }
    private void Logout() {
        SharedPreferences sharedPreferences = getSharedPreferences(Settings.session_key, MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        SharedPreferences sharedPreferencesBiometric = getSharedPreferences(SettingBiometric.session_key, MODE_PRIVATE);
        sharedPreferencesBiometric.edit().clear().apply();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(activityHomeBinding.getRoot());
        init();
        activityHomeBinding.imgAddStock.setOnClickListener(this);
        activityHomeBinding.imgPurchase.setOnClickListener(this);
        activityHomeBinding.imgSales.setOnClickListener(this);
        activityHomeBinding.imgSearch.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    /** Bỏ qua cảnh báo linter: Dòng này hướng dẫn trình biên dịch bỏ qua các cảnh báo liên quan đến việc sử dụng màu sắc và thiết lập văn bản trực tiếp trong code. */
    private void init() {
        activityHomeBinding.homeTopWelcomeMessage.setText("Welcome, " + getUsername());
        /** set default fragment when loading home activity */
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new MainFragment();
        fragmentTransaction.replace(activityHomeBinding.homeFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
        /** set drawer layout toggle */
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                activityHomeBinding.homeDrawerLayout,
                activityHomeBinding.homeToolbar,
                R.string.home_toolbar_open,
                R.string.home_toolbar_off
        );
        activityHomeBinding.homeDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        activityHomeBinding.homeNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                activityHomeBinding.homeDrawerLayout.closeDrawers();
                Fragment fragment = null;
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                if (item.getItemId() == R.id.menu_add_stock) {
                    fragment = new AddStockFragment();
                }
                else if (item.getItemId() == R.id.menu_sales) {
                    fragment = new SaleFragment();
                }
                else if (item.getItemId() == R.id.menu_purchase) {
                    fragment = new PurchaseFragment();
                }
                else if (item.getItemId() == R.id.menu_search_stock) {
                    fragment = new SearchFragment();
                }
                else if (item.getItemId() == R.id.menu_list_stock) {
                    fragment = new ListFragment();
                }
//                else if (item.getItemId() == R.id.Dark_Light) {
//                    fragment = new ListFragment();
//                }
                else if (item.getItemId() == R.id.menu_logout) {
                    Logout();
                }
                if (fragment != null) {
                    fragmentTransaction.replace(activityHomeBinding.homeFrameLayout.getId(), fragment);
                    fragmentTransaction.commit();
                    return true;
                }
                return false;
            }
        });
    }

    private String getUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences(Settings.session_key, MODE_PRIVATE);
        /** Lấy đối tượng SharedPreferences để lưu trữ và truy xuất dữ liệu dạng key-value trên thiết bị ở MODE_PRIVATE */
        return sharedPreferences.getString(Settings.session_username_key, null);
    }

    @Override
    public void onBackPressed() {
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(v.getId() == activityHomeBinding.imgAddStock.getId()){
            fragmentTransaction.replace(activityHomeBinding.homeFrameLayout.getId(), new AddStockFragment()); // Replace current fragment
            fragmentTransaction.commit(); // Execute transaction
        }
        else if (v.getId() == activityHomeBinding.imgSales.getId()) {
            fragmentTransaction.replace(activityHomeBinding.homeFrameLayout.getId(), new SaleFragment()); // Replace current fragment
            fragmentTransaction.commit(); // Execute transaction
        }
        else if (v.getId() == activityHomeBinding.imgPurchase.getId()) {
            fragmentTransaction.replace(activityHomeBinding.homeFrameLayout.getId(), new PurchaseFragment()); // Replace current fragment
            fragmentTransaction.commit(); // Execute transaction
        }
        else if (v.getId() == activityHomeBinding.imgSearch.getId()) {
            fragmentTransaction.replace(activityHomeBinding.homeFrameLayout.getId(), new SearchFragment()); // Replace current fragment
            fragmentTransaction.commit(); // Execute transaction
        }
    }
}