package com.ssmnd.studentintellect.activities.main.mymodules

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssmnd.studentintellect.activities.main.MainActivity
import com.ssmnd.studentintellect.R
import com.ssmnd.studentintellect.databinding.FragmentMyModulesBinding
import com.ssmnd.studentintellect.utils.Utils2.appRatedCheck
import com.ssmnd.studentintellect.utils.Utils2.askPlayStoreRatings
import com.ssmnd.studentintellect.utils.Utils2.isGooglePlayServicesAvailable
import com.ssmnd.studentintellect.utils.Utils2.openPlayStore
import com.ssmnd.studentintellect.utils.Utils2.shareApp
import com.ssmnd.studentintellect.utils.Utils2.tempDisable
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ssmnd.studentintellect.activities.modules.ModulesActivity

class MyModulesFragment : Fragment() {

    private lateinit var binding: FragmentMyModulesBinding
    private var myModulesAdapter : MyModulesAdapter? = null

    private var interstitialAd : InterstitialAd? = null
    internal fun loadInterstitialAd() {
        InterstitialAd.load(requireActivity(), getString(R.string.activity_modulesSelectList_interstitialAdUnitId),
            AdRequest.Builder().build(), object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    println("Ad was loaded.")
                    interstitialAd = ad

                    interstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                        override fun onAdClicked() {
                            // Called when a click is recorded for an ad.
                            println( "Ad was clicked.")
                        }

                        override fun onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Set the ad reference to null so you don't show the ad a second time.
                            println( "Ad dismissed fullscreen content.")
                            //startActivity(Intent(activity, ModulesSelectActivity::class.java))
                            loadInterstitialAd()
                            activity?.startActivity(Intent(activity, ModulesActivity::class.java))
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            // Called when ad fails to show.
                            println( "Ad failed to show fullscreen content.")
                            loadInterstitialAd()
                            activity?.startActivity(Intent(activity, ModulesActivity::class.java))
                        }

                        override fun onAdImpression() {
                            // Called when an impression is recorded for an ad.
                            println( "Ad recorded an impression.")
                        }

                        override fun onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            println( "Ad showed fullscreen content.")
                        }
                    }
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                }
            })
    }
    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        var granted = true
        permissions.entries.forEach {
            println("permission : "+ "${it.key} = ${it.value}")
            granted = granted && it.value
        }
        println("permissions granted : $granted")

        if(!granted){
            askPermissionsAgain()
        } else {
            myModulesAdapter?.gotoMaterialsFragment()
        }
    }
    private fun askPermissionsAgain() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                requireActivity().shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        requireActivity().shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected. In this UI,
                    // include a "cancel" or "no thanks" button that allows the user to
                    // continue using your app without granting the permission.

                    AlertDialog.Builder(requireActivity())
                        .setTitle("Storage Permission needed")
                        .setMessage("This permission is needed to save and read the study materials in your storage.\nPlease allow access to files.")
                        .setPositiveButton("Grant Permission") { _, _ ->
                            requestMultiplePermissions.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        }
                        .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
                PermissionChecker.checkCallingOrSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED ||
                        PermissionChecker.checkCallingOrSelfPermission(requireContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED ->
                {
                    AlertDialog.Builder(requireActivity())
                        .setTitle("Storage Permission needed")
                        .setMessage("This permission is needed to save and read the study materials in your storage.\nPlease allow access to files.")
                        .setPositiveButton("Grant Permission") { _, _ ->
                            AlertDialog.Builder(requireActivity())
                                .setMessage("Click:\n-Permissions\n-Files and Media\n-Allow access to media only.")
                                .setPositiveButton("OK") { _, _ ->
                                    try {
                                        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${activity?.packageName}")))
                                    } catch (e: Exception) {
                                        AlertDialog.Builder(requireActivity())
                                            .setMessage("An error occurred. Please go to App Info and follow the given instructions.")
                                            .create()
                                            .show()
                                    }
                                }
                                .create()
                                .show()
                        }
                        .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
            }
        }
        else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            when {
                PermissionChecker.checkCallingOrSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED ||
                        PermissionChecker.checkCallingOrSelfPermission(requireContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED ->
                {
                    AlertDialog.Builder(requireActivity())
                        .setTitle("Storage Permission needed")
                        .setMessage("This permission is needed to save and read the study materials in your storage.\nPlease allow access to files.")
                        .setPositiveButton("Grant Permission") { _, _ ->
                            AlertDialog.Builder(requireActivity())
                                .setMessage("Click:\n-Permissions\n-Files and Media\n-Allow access to media only.")
                                .setPositiveButton("OK") { _, _ ->
                                    try {
                                        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${activity?.packageName}")))
                                    } catch (e: Exception) {
                                        AlertDialog.Builder(requireActivity())
                                            .setMessage("An error occurred. Please go to App Info and follow the given instructions.")
                                            .create()
                                            .show()
                                    }
                                }
                                .create()
                                .show()
                        }
                        .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentMyModulesBinding.inflate(inflater, container, false)
        @Suppress("DEPRECATION")
        setHasOptionsMenu(true)
        MobileAds.initialize(requireContext()) {}
        loadInterstitialAd()

        binding.loadingAnim.visibility = View.VISIBLE
        binding.welcomeLayout.visibility = View.GONE
        binding.moduleListLayout.visibility = View.VISIBLE


        (activity as MainActivity?)?.apply {
            val databaseHelper = MyModulesLocalDatabase(this)

            databaseHelper.myModulesList.observe(this) { myModulesList ->
                if (myModulesList.isNullOrEmpty()) {
                    binding.welcomeLayout.visibility = View.VISIBLE
                    binding.moduleListLayout.visibility = View.GONE
                    binding.selectModules.setOnClickListener {
                        it.tempDisable()
                        activity?.startActivity(Intent(activity, ModulesActivity::class.java))
                    }
                    binding.loadingAnim.visibility = View.GONE
                } else {
                    myModulesAdapter = MyModulesAdapter(this, myModulesList, databaseHelper, requestMultiplePermissions)
                    binding.moduleList.layoutManager = LinearLayoutManager(this)
                    binding.moduleList.setHasFixedSize(true)
                    binding.moduleList.adapter = myModulesAdapter
                    binding.loadingAnim.visibility = View.GONE
                }
            }
        }

        return binding.root
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)

        menuInflater.inflate(R.menu.menu_main, menu)
        if (menu is MenuBuilder) menu.setOptionalIconsVisible(true)
        if (!requireActivity().isGooglePlayServicesAvailable()) {
            menu.findItem(R.id.item_rate).isVisible = false
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.item_modules -> {
                if (interstitialAd == null) {
                    activity?.startActivity(Intent(activity, ModulesActivity::class.java))
                } else {
                    interstitialAd!!.show(requireActivity())
                }
            }
            R.id.item_share -> {
                requireActivity().shareApp()
            }
            R.id.item_rate -> {
                requireActivity().appRatedCheck({
                    requireActivity().openPlayStore()
                },{
                    requireActivity().askPlayStoreRatings()
                })
            }
            R.id.item_feedback -> {
                (activity as MainActivity).showFeedbackDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}