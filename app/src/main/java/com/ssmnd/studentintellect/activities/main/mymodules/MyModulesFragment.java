package com.ssmnd.studentintellect.activities.main.mymodules;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ssmnd.studentintellect.R;
import com.ssmnd.studentintellect.databinding.FragmentHomeBinding;

public class MyModulesFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyModulesViewModel homeViewModel =
                new ViewModelProvider(this).get(MyModulesViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        //noinspection deprecation
        setHasOptionsMenu(true);

        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("RestrictedApi")
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        ((MenuBuilder) menu).setOptionalIconsVisible(true);


    }
}