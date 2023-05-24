package com.ssmnd.studentintellect.activities.auth;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.google.android.material.elevation.SurfaceColors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssmnd.studentintellect.activities.ActivitiesMethods;
import com.ssmnd.studentintellect.activities.main.MainActivity;
import com.ssmnd.studentintellect.databinding.ActivityAuthBinding;
import com.ssmnd.studentintellect.R;
import com.ssmnd.studentintellect.utils.NetworkObserver;
import java.util.Objects;

public class AuthActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityAuthBinding binding;
    public MutableLiveData<Boolean> isOnline = new MutableLiveData<>(true);
    public static final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*SplashScreen splashScreen = */SplashScreen.installSplashScreen(this);
        //splashScreen.setKeepOnScreenCondition(() -> true);

        if(currentUser != null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        switch (PreferenceManager.getDefaultSharedPreferences(this).getString("theme_mode", "2")) {
            case "0" : AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            case "1" : AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            case "2" : AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        //Theme
        int colorInt = SurfaceColors.SURFACE_1.getColor(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(colorInt);
        getWindow().setNavigationBarColor(colorInt);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(colorInt));


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_auth);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            if (navDestination.getId() == R.id.LoginFragment) {
                binding.iconImageView.setVisibility(View.VISIBLE);
            } else {
                binding.iconImageView.setVisibility(View.GONE);
            }
        });


        //Network Callback
        NetworkObserver networkObserver = new NetworkObserver(this);
        networkObserver.getIsOnline().observe(this, isOnline -> {
            this.isOnline.postValue(isOnline);
            if (isOnline) {
                binding.offlineView.setVisibility(View.GONE);
            } else {
                binding.offlineView.setVisibility(View.VISIBLE);
            }
        });
        ActivitiesMethods.initializeAds(this, binding.adView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser != null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_auth);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}