package com.example.sevillatraffic.ui.options

import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.example.sevillatraffic.R

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var voice: SwitchPreferenceCompat
    private lateinit var voiceCar: SwitchPreferenceCompat

    companion object {

        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->

            val stringValue = value.toString()
            preference.summary = stringValue

            true
        }

        private fun bindPreferenceSummaryToValue(preference: Preference) {

            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                    .getDefaultSharedPreferences(preference.context)
                    .getBoolean(preference.key, false))
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.root_preferences)

        bindPreferenceSummaryToValue(findPreference<SwitchPreferenceCompat>("detectors") as SwitchPreferenceCompat)
        bindPreferenceSummaryToValue(findPreference<SwitchPreferenceCompat>("fluid") as SwitchPreferenceCompat)
        bindPreferenceSummaryToValue(findPreference<SwitchPreferenceCompat>("voice") as SwitchPreferenceCompat)

        var info = findPreference<Preference>("info") as Preference

        voice = findPreference<SwitchPreferenceCompat>("voice") as SwitchPreferenceCompat
        voiceCar = findPreference<SwitchPreferenceCompat>("voiceCar") as SwitchPreferenceCompat

        voice.setOnPreferenceClickListener{
            toggleButtons()
            true
        }

        info.setOnPreferenceClickListener {_->
            findNavController().navigate(R.id.nav_app_info)
            true
        }
    }

    private fun toggleButtons() {
        if (voice.isChecked) {
            voiceCar.isEnabled = true
        } else {
            voiceCar.isChecked = false
            voiceCar.isEnabled = false
        }
    }
}