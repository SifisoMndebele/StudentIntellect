package com.ssmnd.studentintellect.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssmnd.studentintellect.R;
import com.ssmnd.studentintellect.databinding.FragmentLoginBinding;
import com.ssmnd.studentintellect.databinding.PopupTermsBinding;
import com.ssmnd.studentintellect.utils.LoadingDialog;

public class FirstFragment extends Fragment {

    private FragmentLoginBinding binding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private PopupTermsBinding terms;
    private PopupWindow popupWindow;
    private LoadingDialog loadingDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}