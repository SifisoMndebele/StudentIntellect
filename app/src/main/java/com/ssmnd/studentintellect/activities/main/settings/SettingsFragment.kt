package com.ssmnd.studentintellect.activities.main.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.ssmnd.studentintellect.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}