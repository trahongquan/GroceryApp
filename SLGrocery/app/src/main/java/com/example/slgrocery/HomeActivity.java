package com.example.slgrocery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

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
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding activityHomeBinding;
    ActionBarDrawerToggle actionBarDrawerToggle;

    private void Logout() {
        SharedPreferences sharedPreferences = getSharedPreferences(Settings.session_key, MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(activityHomeBinding.getRoot());
        init();
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    /** Bỏ qua cảnh báo linter: Dòng này hướng dẫn trình biên dịch bỏ qua các cảnh báo liên quan đến việc sử dụng màu sắc và thiết lập văn bản trực tiếp trong code. */
    private void init() {
        activityHomeBinding.homeTopWelcomeMessage.setText("Welcome, " + getUsername());
        // set default fragment when loading home activity
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new MainFragment();
        fragmentTransaction.replace(activityHomeBinding.homeFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
        // set drawer layout toggle
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
        return sharedPreferences.getString(Settings.session_username_key, null);
    }

    @Override
    public void onBackPressed() {
    }

}