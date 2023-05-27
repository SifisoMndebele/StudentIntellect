package com.ssmnd.studentintellect.activities.main.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.ssmnd.studentintellect.BuildConfig
import com.ssmnd.studentintellect.R
import com.ssmnd.studentintellect.activities.auth.AuthActivity
import com.ssmnd.studentintellect.databinding.FragmentProfileBinding
import com.ssmnd.studentintellect.databinding.SheetAboutAppBinding
import com.ssmnd.studentintellect.activities.auth.data.UserType
import com.ssmnd.studentintellect.activities.main.MainActivity
import com.ssmnd.studentintellect.utils.LoadingDialog
import com.ssmnd.studentintellect.utils.OpenPicturesContract
import com.ssmnd.studentintellect.utils.Utils2.hideKeyboard
import com.ssmnd.studentintellect.utils.Utils2.isGooglePlayServicesAvailable
import com.ssmnd.studentintellect.utils.Utils2.openPlayStore
import com.ssmnd.studentintellect.utils.Utils2.toRand
import com.ssmnd.studentintellect.utils.Utils2.shareApp
import com.ssmnd.studentintellect.utils.Utils2.tempDisable
import com.bumptech.glide.Glide
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.ssmnd.studentintellect.activities.auth.AppUser
import com.ssmnd.studentintellect.activities.modules.ModulesActivity
import java.io.IOException

class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    private val auth = Firebase.auth
    private var interstitialAd: InterstitialAd? = null

    private fun loadAd() {
        InterstitialAd.load(requireContext(),
            getString(R.string.activity_modulesSelectList_interstitialAdUnitId),
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    println("Ad was loaded.")
                    interstitialAd = ad
                    interstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdClicked() {
                                // Called when a click is recorded for an ad.
                                println("Ad was clicked.")
                            }

                            override fun onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                println("Ad dismissed fullscreen content.")
                                activity?.startActivity(Intent(activity, ModulesActivity::class.java))
                                loadAd()
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                // Called when ad fails to show.
                                println("Ad failed to show fullscreen content. $adError")
                                interstitialAd = null
                            }

                            override fun onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                println("Ad recorded an impression.")
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                println("Ad showed fullscreen content.")
                            }
                        }
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                }
            })
    }


    private val selectImageResult = registerForActivityResult(OpenPicturesContract()) { uri: Uri? ->
        uri?.let { setImage(uri) }
    }
    private fun setImage(imageUri : Uri?) {
        val loadingDialog = LoadingDialog(requireActivity())
        loadingDialog.show("Saving Picture...")
        try {
            if (imageUri != null) {
                val userImagesRef =  Firebase.storage.getReference("Users")
                    .child("Images/${AppUser.getUid()}.png")
                val uploadTask = userImagesRef.putFile(imageUri)
                loadingDialog.cancel {
                    uploadTask.cancel()
                }
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            loadingDialog.showError(it.message) {  }
                            throw it
                        }
                    }
                    userImagesRef.downloadUrl
                }
                    .addOnSuccessListener { imageUrl ->
                        AppUser.setImageUrl(imageUrl.toString())
                        loadingDialog.showDone {  }
                    }
                    .addOnFailureListener {
                        loadingDialog.showError(it.message) {  }
                    }

            } else {
                binding.userImage.setImageResource(R.drawable.ic_user)
                AppUser.setImageUrl(null)
            }


        } catch (e: IOException) {
            loadingDialog.showError(e.message) {  }
            e.printStackTrace()
        }
    }


    override fun onCreateView (
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.userEmail.text = AppUser.getEmail().value

        /*profileViewModel.modulesList.observe(viewLifecycleOwner) { list ->
            val modules = StringBuilder()
            var b = false
            list?.forEach {
                if (b) modules.append(", ")
                modules.append(it.code +" - "+it.name)
                b = true
            }
            binding.modulesPreview.setText(modules.toString().trim().ifEmpty { "Select your modules" })
        }*/
        AppUser.getImageUrl().observe(viewLifecycleOwner) {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.ic_user)
                .into(binding.userImage)
        }

        AppUser.getBalance().observe(viewLifecycleOwner) {
            @SuppressLint("SetTextI18n")
            binding.userBalance.text = it.toRand()
        }

        binding.username.setText(AppUser.getNames().value?:getString(R.string.display_picture_and_name))

        var names = AppUser.getNames().value ?: ""
        binding.username.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty() || names == text.toString().trim()) {
                binding.saveButton.visibility = View.GONE
            } else {
                binding.saveButton.visibility = View.VISIBLE
            }
        }

        binding.saveButton.setOnClickListener {
            it.tempDisable()
            context?.hideKeyboard(binding.root)
            names = binding.username.text.toString().trim()
            AppUser.setNames(names)
            binding.saveButton.visibility = View.GONE
        }

        AppUser.getUserType().observe(viewLifecycleOwner) {
            when (it) {
                UserType.STUDENT -> {

                }
                UserType.TUTOR -> {

                }
                UserType.ADMIN -> {

                }
                else -> {}
            }
        }

        binding.editImageButton.setOnClickListener {
            it.tempDisable()
            selectImageResult.launch(arrayOf("image/*"))
        }
        binding.removeImageButton.setOnClickListener {
            it.tempDisable()
            binding.userImage.setImageResource(R.drawable.ic_user)
            setImage(null)
        }




        binding.modules.setOnClickListener {
            it.tempDisable()
            if (interstitialAd == null) {
                activity?.startActivity(Intent(activity, ModulesActivity::class.java))
            } else {
                interstitialAd!!.show(requireActivity())
            }
        }
        binding.modulesPreview.setOnClickListener {
            it.tempDisable()
            if (interstitialAd == null) {
                activity?.startActivity(Intent(activity, ModulesActivity::class.java))
            } else {
                interstitialAd!!.show(requireActivity())
            }
        }

        binding.logout.setOnClickListener {
            it.tempDisable()
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(getString(R.string.logout_question))
                .setMessage(getString(R.string.confirm_logout_text))
                .setNeutralButton(getString(R.string.cancel)){d,_->
                    d.dismiss()
                }
                .setPositiveButton(getString(R.string.logout)){d,_->
                    d.dismiss()
                    AppUser.deleteUser(auth.currentUser!!.uid)
                    AppUser.setModulesSet(AppUser.getModulesSet().value ?: setOf())
                    AppUser.deleteModules()

                    Firebase.auth.signOut()
                    startActivity(Intent(requireActivity(), AuthActivity::class.java))
                    requireActivity().finishAffinity()
                }
                .show()
        }

        binding.feedback.setOnClickListener {
            it.tempDisable()
            (activity as MainActivity).showFeedbackDialog()
        }

        binding.appInfo.setOnClickListener {
            it.tempDisable()
            val sheet = AppInfoSheetDialog()
            sheet.show(childFragmentManager, "AppInfoSheetDialog")
        }

        MobileAds.initialize(requireContext()) { loadAd() }
        return binding.root
    }

    class AppInfoSheetDialog : BottomSheetDialogFragment() {
        private lateinit var binding: SheetAboutAppBinding
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            super.onCreateView(inflater, container, savedInstanceState)
            binding = SheetAboutAppBinding.inflate(inflater,container,false)

            binding.appVersion.text = BuildConfig.VERSION_NAME
            binding.shareApp.setOnClickListener {
                it.tempDisable()
                dismiss()
                activity?.shareApp()
            }
            if (requireContext().isGooglePlayServicesAvailable()) {
                binding.openPlayStore.setOnClickListener {
                    it.tempDisable()
                    dismiss()
                    activity?.openPlayStore()
                }
            } else {
                binding.openPlayStore.visibility = View.GONE
            }

            return binding.root
        }
    }
}

