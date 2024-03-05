package com.example.slgrocery.Fragments;

import static java.util.Objects.requireNonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.slgrocery.R;
import com.example.slgrocery.databinding.ActivityHomeBinding;
import com.example.slgrocery.databinding.FragmentMainBinding;

public class MainFragment extends Fragment implements View.OnClickListener {

    FragmentMainBinding fragmentMainBinding;
    ActivityHomeBinding activityHomeBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMainBinding = FragmentMainBinding.inflate(inflater, container, false);
        fragmentMainBinding.imgAddStock.setOnClickListener(this);
        return fragmentMainBinding.getRoot();
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == fragmentMainBinding.imgAddStock.getId()){
            AddStockFragment addStockFragment = new AddStockFragment(); // Create new instance
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.replace(activityHomeBinding.homeFrameLayout.getId(), new AddStockFragment()); // Replace current fragment
            fragmentTransaction.commit(); // Execute transaction
        }
    }
}