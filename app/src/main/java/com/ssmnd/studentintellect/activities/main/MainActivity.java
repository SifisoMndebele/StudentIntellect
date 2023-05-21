package com.ssmnd.studentintellect.activities.main;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssmnd.studentintellect.R;
import com.ssmnd.studentintellect.activities.auth.AuthActivity;
import com.ssmnd.studentintellect.databinding.ActivityMainBinding;
import com.ssmnd.studentintellect.utils.NetworkObserver;
import com.ssmnd.studentintellect.utils.Utils;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //splashScreen.setKeepOnScreenCondition(() -> true);
        if(/*currentUser != null*/ false){
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        //Theme
        int colorInt = SurfaceColors.SURFACE_1.getColor(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(colorInt);
        getWindow().setNavigationBarColor(colorInt);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(colorInt));


        DrawerLayout drawer = binding.drawerLayout;
        navigationView = binding.navView;
        navigationView.setItemIconTintList(null);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_my_materials)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        setNavigationViewMenuClickListener(drawer);


        //Profile Header
        /*View headerView = navigationView.getHeaderView(0);
        assert currentUser != null;
        Glide.with(this)
                .load(currentUser.getPhotoUrl())
                .circleCrop()
                .into((ImageView) headerView.findViewById(R.id.user_image));

        TextView userNameView = headerView.findViewById(R.id.user_name);
        userNameView.setText(currentUser.getDisplayName());

        TextView userEmailView = headerView.findViewById(R.id.user_email);
        userEmailView.setText(currentUser.getEmail());

        CardView profileLayoutView = headerView.findViewById(R.id.profile_layout);
        profileLayoutView.setOnClickListener(view -> {
            navController.navigate(R.id.profile_layout);
            drawer.close();
        });*/


        //Coming soon
        ImageView timetable = (ImageView) navigationView.getMenu().findItem(R.id.action_timetable).getActionView();
        timetable.setAdjustViewBounds(true);
        timetable.setMaxHeight(Utils.dpToPx(28));
        timetable.setImageResource(R.drawable.ic_coming_soon);
        ImageView youtube = (ImageView) navigationView.getMenu().findItem(R.id.nav_youtube).getActionView();
        youtube.setAdjustViewBounds(true);
        youtube.setMaxHeight(Utils.dpToPx(28));
        youtube.setImageResource(R.drawable.ic_coming_soon);
        ImageView map = (ImageView) navigationView.getMenu().findItem(R.id.action_map).getActionView();
        map.setAdjustViewBounds(true);
        map.setMaxHeight(Utils.dpToPx(28));
        map.setImageResource(R.drawable.ic_coming_soon);



        //Network Callback
        NetworkObserver networkObserver = new NetworkObserver(this);
        networkObserver.getIsOnline().observe(this, isOnline -> {
            if (isOnline) {
                binding.offlineView.setVisibility(View.GONE);
            } else {
                binding.offlineView.setVisibility(View.VISIBLE);
            }
        });
        initializeAds();
    }

    private void initializeAds() {
        MobileAds.initialize(this, initializationStatus -> {
            AdRequest adRequest = new AdRequest.Builder().build();
            binding.adView.loadAd(adRequest);
            binding.adView.setAdListener(new AdListener() {
                @Override
                public void onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }
                @Override
                public void onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                }
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                    // Code to be executed when an ad request fails.
                }
                @Override
                public void onAdImpression() {
                    // Code to be executed when an impression is recorded
                    // for an ad.
                }
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }
                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    private void setNavigationViewMenuClickListener(DrawerLayout drawerLayout) {
        navigationView.getMenu().findItem(R.id.action_logout).setOnMenuItemClickListener(view -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.logout_question))
                    .setMessage(getString(R.string.confirm_logout_text))
                    .setNeutralButton(getString(R.string.cancel), (dialog, i)->{
                        dialog.dismiss();
                    })
                    .setPositiveButton(getString(R.string.logout), (dialog, i)-> {
                        dialog.dismiss();
                        drawerLayout.close();
                        //TODO("Logout the user")
                        startActivity(new Intent(this, AuthActivity.class));
                        finishAffinity();
                    })
                    .show();
            return true;
        });

        //Todo features
        navigationView.getMenu().findItem(R.id.action_timetable).setOnMenuItemClickListener(view -> {
            //TODO("Individual classes timetable for selected modules.")
            new MaterialAlertDialogBuilder(this)
                    .setTitle("TODO")
                    .setMessage("Individual classes timetable for selected modules.")
                    .show();
            return false;
        });
        navigationView.getMenu().findItem(R.id.nav_youtube).setOnMenuItemClickListener(view -> {
            //TODO("Youtube tutorial videos from trusted tutors.")
            new MaterialAlertDialogBuilder(this)
                    .setTitle("TODO")
                    .setMessage("Youtube tutorial videos from trusted tutors.")
                    .show();
            return false;
        });
        navigationView.getMenu().findItem(R.id.action_map).setOnMenuItemClickListener(view -> {
            //TODO("Map to give directions around the campus.")
            new MaterialAlertDialogBuilder(this)
                    .setTitle("TODO")
                    .setMessage("Map to give directions around the campus.")
                    .show();
            return false;
        });


        //Blackboard
        navigationView.getMenu().findItem(R.id.action_blackboard).setOnMenuItemClickListener(view -> {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            boolean openBlackboardApp = sharedPreferences.getBoolean("open_blackboard_app", false);
            if (openBlackboardApp){
                try {
                    Intent blackboardIntent = new Intent();
                    blackboardIntent.setClassName("com.blackboard.android.bbstudent", "com.blackboard.android.bbstudent.splash.SplashActivity");
                    blackboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    blackboardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(blackboardIntent);
                }
                catch (ActivityNotFoundException ignored) {
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Download Blackboard Learn?")
                            .setNeutralButton("Cancel", (dialog, i)-> dialog.dismiss())
                            .setNegativeButton("Open the website", (dialog, i)-> {
                                dialog.dismiss();
                                sharedPreferences.edit().putBoolean("open_blackboard_app", false).apply();
                                openBlackboardWebsiteOnApp();
                            })
                            .setPositiveButton("Download", (dialog, i) -> {
                                dialog.dismiss();
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("market://details?id=com.blackboard.android.bbstudent"));
                                startActivity(intent);
                            })
                            .show();
                }
            }
            else {
                openBlackboardWebsiteOnApp();
            }

            drawerLayout.close();
            return false;
        });


        //Intellect Calculator
        navigationView.getMenu().findItem(R.id.action_calculator).setOnMenuItemClickListener(view -> {
            try {
                Intent calculatorIntent = new Intent();
                calculatorIntent.setClassName("com.ssmnd.intellectcalculator", "com.ssmnd.intellectcalculator.MainActivity");
                calculatorIntent.putExtra("displayHomeAsUpEnabled", true);

                startActivity(calculatorIntent);
            } catch (ActivityNotFoundException ignored) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.ssmnd.intellectcalculator"));
                startActivity(intent);
            }
            drawerLayout.close();
            return false;
        });
    }

    private void openBlackboardWebsiteOnApp() {
        String blackboardUrl = "https://tmlearn.ul.ac.za/";

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setDefaultColorSchemeParams(new CustomTabColorSchemeParams.Builder()
                .setToolbarColor(SurfaceColors.SURFACE_1.getColor(this))
                .build());
        builder.setCloseButtonIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_back_arrow));
        builder.setToolbarCornerRadiusDp(15);
        builder.setUrlBarHidingEnabled(true);
        CustomTabsIntent customTabsIntent = builder.build();

        customTabsIntent.launchUrl(this, Uri.parse(blackboardUrl));
    }
}