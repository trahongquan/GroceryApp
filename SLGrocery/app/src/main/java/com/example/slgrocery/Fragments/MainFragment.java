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

public class MainFragment extends Fragment {

    FragmentMainBinding fragmentMainBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMainBinding = FragmentMainBinding.inflate(inflater, container, false);
        return fragmentMainBinding.getRoot();
    }
}