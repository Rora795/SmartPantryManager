package com.smartpantry.app.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.smartpantry.app.R;

/**
 * Settings / profile screen (Section 2.2). Two simple preferences, persisted
 * in SharedPreferences:
 *   - expiring-soon alerts toggle
 *   - preferred unit system (metric / imperial)
 */
public class SettingsFragment extends Fragment {

    public static final String KEY_ALERTS = "pref_expiry_alerts";
    public static final String KEY_UNITS = "pref_units";

    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        MaterialSwitch alerts = view.findViewById(R.id.switch_alerts);
        alerts.setChecked(prefs.getBoolean(KEY_ALERTS, true));
        alerts.setOnCheckedChangeListener((btn, checked) ->
                prefs.edit().putBoolean(KEY_ALERTS, checked).apply());

        RadioGroup units = view.findViewById(R.id.group_units);
        String current = prefs.getString(KEY_UNITS, "metric");
        ((RadioButton) view.findViewById(
                "imperial".equals(current) ? R.id.radio_imperial : R.id.radio_metric))
                .setChecked(true);
        units.setOnCheckedChangeListener((group, checkedId) -> {
            String value = checkedId == R.id.radio_imperial ? "imperial" : "metric";
            prefs.edit().putString(KEY_UNITS, value).apply();
        });
    }
}
