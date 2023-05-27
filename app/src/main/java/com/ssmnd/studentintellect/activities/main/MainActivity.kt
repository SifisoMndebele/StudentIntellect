package com.ssmnd.studentintellect.activities.main

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.cardview.widget.CardView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.elevation.SurfaceColors
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textview.MaterialTextView
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.ssmnd.studentintellect.R
import com.ssmnd.studentintellect.activities.ActivitiesMethods
import com.ssmnd.studentintellect.activities.auth.AppUser
import com.ssmnd.studentintellect.activities.auth.AuthActivity
import com.ssmnd.studentintellect.activities.auth.data.User
import com.ssmnd.studentintellect.activities.modules.ModulesActivity
import com.ssmnd.studentintellect.databinding.ActivityMainBinding
import com.ssmnd.studentintellect.utils.Utils2.appRateCheck
import com.ssmnd.studentintellect.utils.Utils2.appRatedCheck
import com.ssmnd.studentintellect.utils.Utils2.askPlayStoreRatings
import com.ssmnd.studentintellect.utils.Utils2.dpToPx
import com.ssmnd.studentintellect.utils.Utils2.hideKeyboard
import com.ssmnd.studentintellect.utils.Utils2.isGooglePlayServicesAvailable
import com.ssmnd.studentintellect.utils.Utils2.openPlayStore
import com.ssmnd.studentintellect.utils.Utils2.tempDisable


class MainActivity : AppCompatActivity() , OnSharedPreferenceChangeListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navView: NavigationView
    private lateinit var binding: ActivityMainBinding
    private val firebaseUser = Firebase.auth.currentUser

    companion object{
        private const val UPDATE_REQUEST_CODE = 12
        const val USER_ARG = "user_arg"
    }
    private var appUpdateManager : AppUpdateManager? = null

    private var interstitialAd : InterstitialAd? = null
    private fun loadInterstitialAd() {
        InterstitialAd.load(this, getString(R.string.activity_modulesSelectList_interstitialAdUnitId), ActivitiesMethods.adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    // The interstitialAd reference will be null until
                    // an ad is loaded.
                    interstitialAd = ad
                    //nextLevelButton.setEnabled(true)

                    interstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            // Called when fullscreen content is dismissed.
                            interstitialAd = null
                            loadInterstitialAd()
                            startActivity(Intent(this@MainActivity, ModulesActivity::class.java))
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            // Called when fullscreen content failed to show.
                            interstitialAd = null
                            loadInterstitialAd()
                            startActivity(Intent(this@MainActivity, ModulesActivity::class.java))
                        }

                        override fun onAdShowedFullScreenContent() {
                            // Called when fullscreen content is shown.
                        }
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    interstitialAd = null
                    //nextLevelButton.setEnabled(true)
                }
            })
    }
    private fun showInterstitial() {
        // Show the ad if it"s ready. Otherwise toast and reload the ad.
        if (interstitialAd != null) {
            interstitialAd!!.show(this)
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show()
            //goToNextLevel()
        }
    }

    private fun navViewMenuItemClickListeners(drawerLayout: DrawerLayout) {
        navView.menu.findItem(R.id.action_logout).setOnMenuItemClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.logout_question))
                .setMessage(getString(R.string.confirm_logout_text))
                .setNeutralButton(getString(R.string.cancel)){d,_->
                    d.dismiss()
                }
                .setPositiveButton(getString(R.string.logout)){_,_->
                    AppUser.deleteUser(firebaseUser?.uid!!)
                    AppUser.setModulesSet(AppUser.getModulesSet().value ?: setOf())
                    AppUser.deleteModules()

                    Firebase.auth.signOut()
                    startActivity(Intent(this, AuthActivity::class.java))
                    finishAffinity()
                }
                .show()
            true
        }

        navView.menu.findItem(R.id.action_timetable).setOnMenuItemClickListener {
            AlertDialog.Builder(this)
                .setTitle("TODO")
                .setMessage("Individual classes timetable for selected modules.")
                .show()
            false
        }
        navView.menu.findItem(R.id.nav_youtube).setOnMenuItemClickListener {
            AlertDialog.Builder(this)
                .setTitle("TODO")
                .setMessage("Youtube tutorial videos from trusted tutors.")
                .show()
            false
        }
        navView.menu.findItem(R.id.action_map).setOnMenuItemClickListener {
            AlertDialog.Builder(this)
                .setTitle("TODO")
                .setMessage("Map to give directions around the campus.")
                .show()
            false
        }

        //Blackboard
        navView.menu.findItem(R.id.action_blackboard).setOnMenuItemClickListener {
            drawerLayout.close()
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val openBlackboardApp = sharedPreferences.getBoolean("open_blackboard_app", false)
            if (openBlackboardApp){
                try {
                    val blackboardIntent = Intent().apply {
                        setClassName("com.blackboard.android.bbstudent", "com.blackboard.android.bbstudent.splash.SplashActivity")
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(blackboardIntent)
                }
                catch (e: ActivityNotFoundException) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Download Blackboard Learn?")
                        .setNeutralButton("Cancel"){d,_->
                            d.dismiss()
                        }
                        .setNegativeButton("Open the website"){d,_->
                            d.dismiss()
                            sharedPreferences.edit().putBoolean("open_blackboard_app", false).apply()

                            val url = "https://tmlearn.ul.ac.za/"
                            val builder = CustomTabsIntent.Builder()
                            builder.setDefaultColorSchemeParams(CustomTabColorSchemeParams.Builder()
                                .setToolbarColor(SurfaceColors.SURFACE_1.getColor(this))
                                .build())
                            builder.setCloseButtonIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_back_arrow))
                            builder.setToolbarCornerRadiusDp(15)
                            builder.setUrlBarHidingEnabled(true)
                            val customTabsIntent = builder.build()

                            customTabsIntent.launchUrl(this, Uri.parse(url))
                        }
                        .setPositiveButton("Download"){d,_->
                            d.dismiss()
                            startActivity(Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("market://details?id=com.blackboard.android.bbstudent")
                            })
                        }
                        .show()
                }
            }
            else {
                val url = "https://tmlearn.ul.ac.za/"
                val builder = CustomTabsIntent.Builder()
                builder.setDefaultColorSchemeParams(CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(SurfaceColors.SURFACE_1.getColor(this))
                    .build())
                builder.setCloseButtonIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_back_arrow))
                builder.setToolbarCornerRadiusDp(15)
                builder.setUrlBarHidingEnabled(true)
                val customTabsIntent = builder.build()

                customTabsIntent.launchUrl(this, Uri.parse(url))
            }

            false
        }

        //Intellect Calculator
        navView.menu.findItem(R.id.action_calculator).setOnMenuItemClickListener {
            drawerLayout.close()
            try {
                val calculatorIntent = Intent().apply {
                    setClassName("com.ssmnd.intellectcalculator", "com.ssmnd.intellectcalculator.MainActivity")
                    putExtra("displayHomeAsUpEnabled", true)
                }
                startActivity(calculatorIntent)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("market://details?id=com.ssmnd.intellectcalculator")
                })
            }
            false
        }

    }

    private fun onBackPressedMethod() {
        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                if (!navController.navigateUp()){
                    finish()
                    /*if (exit) {
                        finish()
                    } else {
                        exit = true
                        Toast.makeText(this, "Tab again to exit", Toast.LENGTH_SHORT).show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            exit = false
                        }, 5000)
                    }*/
                }
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    val navController = findNavController(R.id.nav_host_fragment_content_main)
                    if (!navController.navigateUp()){
                        finish()
                    }
                }
            })
        }
    }

    private fun checkUpdateAvailability() {
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo

        // Checks whether the platform allows the specified type of update,
        // and current version staleness.
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request an immediate update.
                appUpdateManager?.startUpdateFlowForResult(appUpdateInfo,
                    AppUpdateType.IMMEDIATE, this, UPDATE_REQUEST_CODE
                )
            }
        }
    }

    fun showFeedbackDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.menu_layout_feedback)
        val playStore = dialog.findViewById<MaterialTextView>(R.id.rate_app_on_play_store)
        val googleForm = dialog.findViewById<MaterialTextView>(R.id.fill_google_form)

        if (isGooglePlayServicesAvailable()) {
            appRatedCheck({
                playStore.setOnClickListener {
                    it.tempDisable()
                    dialog.dismiss()
                    openPlayStore()
                }
            },{
                playStore.setOnClickListener {
                    it.tempDisable()
                    dialog.dismiss()
                    askPlayStoreRatings()
                }
            })
        } else {
            playStore.visibility = View.GONE
        }

        googleForm.setOnClickListener {
            it.tempDisable()
            dialog.dismiss()

            val url = "https://docs.google.com/forms/d/e/1FAIpQLSd_oJoesSeXN1pu1oI0cTOU_n6LSE6wwLG-taGB7JD4X3izpQ/viewform?usp=sf_link"
            val builder = CustomTabsIntent.Builder()
            builder.setDefaultColorSchemeParams(CustomTabColorSchemeParams.Builder()
                .setToolbarColor(SurfaceColors.SURFACE_1.getColor(this))
                .build())
            builder.setCloseButtonIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_back_arrow))
            builder.setUrlBarHidingEnabled(true)
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(this, Uri.parse(url))
        }

        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupNetworkListener() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                runOnUiThread {
                    AppUser.setIsOnline(true)
                    binding.offlineView.visibility = View.GONE
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                runOnUiThread {
                    AppUser.setIsOnline(false)
                    binding.offlineView.visibility = View.VISIBLE
                }
            }
        })
    }



    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        if (Firebase.auth.currentUser == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }
        binding = ActivityMainBinding.inflate(layoutInflater)

        val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(USER_ARG, User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(USER_ARG)
        }

        if (user != null) {
            AppUser(this, user)
        } else {
            try {
                AppUser(this)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                Firebase.auth.signOut()
                startActivity(Intent(this, AuthActivity::class.java))
                finishAffinity()
                return
            }
        }

        /*MaterialAlertDialogBuilder(this)
            .setTitle("App User")
            .setMessage(
                AppUser.getUid().value+"\n"+
                        AppUser.getEmail().value+"\n"+
                        AppUser.getNames().value+"\n"+
                        AppUser.getLastName().value+"\n"+
                        AppUser.getImageUrl().value+"\n"+
                        AppUser.getBalance().value+"\n"+
                        AppUser.getPhone().value+"\n"+
                        AppUser.getModulesSet().value?.joinToString(" ; ") { it.code }
            )
            .show()*/


        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupNetworkListener()

        // Set up an OnPreDrawListener to the root view.
        /*val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check whether the initial data is ready.
                    return if (viewModel.isReady) {
                        // The content is ready. Start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        // The content isn't ready. Suspend.
                        false
                    }
                }
            }
        )*/
        //Theme
        /*val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        when (sharedPreferences.getString("theme_mode", "2")) {
            "0" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "1" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "2" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }*/
        val color = SurfaceColors.SURFACE_1.getColor(this)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = color
        window.navigationBarColor = color
        supportActionBar?.setBackgroundDrawable(ColorDrawable(color))


        if (isGooglePlayServicesAvailable()) {
            /**Check updates*/
            appUpdateManager = AppUpdateManagerFactory.create(this)
            checkUpdateAvailability()

            /**Check app rate*/
            appRateCheck ({
                askPlayStoreRatings()
            },{})
        } else {
            //Hide google services
        }




        val drawerLayout: DrawerLayout = binding.drawerLayout
        navView = binding.navView
        navView.itemIconTintList = null
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_my_materials
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        /**Back method*/
        onBackPressedMethod()


        //Profile Header
        val headerView = navView.getHeaderView(0)
        AppUser.getImageUrl().observe(this) {
            Glide.with(this)
                .load(it)
                .circleCrop()
                .into(headerView.findViewById(R.id.user_image))
        }
        AppUser.getNames().observe(this) { names ->
            AppUser.getLastName().observe(this) { lastName ->
                headerView.findViewById<TextView>(R.id.user_name).text =
                    names.split(" ").map { it[0] }.joinToString("")+" $lastName"
            }
        }
        AppUser.getEmail().observe(this) {
            headerView.findViewById<TextView>(R.id.user_email).text = it
        }
        headerView.findViewById<CardView>(R.id.profile_layout).setOnClickListener {
            navController.navigate(R.id.action_profile_fragment)
            drawerLayout.close()
        }

        navViewMenuItemClickListeners( drawerLayout)

        /**Init ads*/
        ActivitiesMethods.initializeAds(this, binding.adView)
        loadInterstitialAd()




        /*val timetable = navView.menu.findItem(R.id.action_timetable).actionView as TextView
        timetable.gravity = Gravity.CENTER_VERTICAL;
        //timetable.setTypeface(null,Typeface.BOLD);
        timetable.setTextColor(ResourcesCompat.getColor(resources, R.color.primaryVariantColor, theme));
        timetable.text = "Coming Soon";*/
        val timetable = navView.menu.findItem(R.id.action_timetable).actionView as ImageView
        timetable.adjustViewBounds = true
        timetable.maxHeight = 28.dpToPx.toInt()
        timetable.setImageResource(R.drawable.ic_coming_soon)

        val youtube = navView.menu.findItem(R.id.nav_youtube).actionView as ImageView
        youtube.adjustViewBounds = true
        youtube.maxHeight = 28.dpToPx.toInt()
        youtube.setImageResource(R.drawable.ic_coming_soon)

        val map = navView.menu.findItem(R.id.action_map).actionView as ImageView
        map.adjustViewBounds = true
        map.maxHeight = 28.dpToPx.toInt()
        map.setImageResource(R.drawable.ic_coming_soon)
    }

    override fun onSupportNavigateUp(): Boolean {
        hideKeyboard(binding.root)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // Checks that the update is not stalled during 'onResume()'.
    override fun onResume() {
        super.onResume()
        appUpdateManager?.appUpdateInfo
            ?.addOnSuccessListener { appUpdateInfo ->
                // notify the user to complete the update.
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager?.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this,
                        UPDATE_REQUEST_CODE
                    )
                }
            }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when(key){
            "under_construction_features" -> {
                val construction = sharedPreferences.getBoolean(key, true)
                navView.menu.findItem(R.id.action_timetable).isVisible = construction
                navView.menu.findItem(R.id.nav_youtube).isVisible = construction
                navView.menu.findItem(R.id.action_map).isVisible = construction
            }
            "theme_mode" -> {
                when (sharedPreferences.getString(key, "2")) {
                    "0" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    "1" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    "2" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val construction = sharedPreferences.getBoolean("under_construction_features", false)
        navView.menu.findItem(R.id.action_timetable).isVisible = construction
        navView.menu.findItem(R.id.nav_youtube).isVisible = construction
        navView.menu.findItem(R.id.action_map).isVisible = construction
    }

    override fun onStop() {
        super.onStop()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)

    }
}