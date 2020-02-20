package com.roysolberg.android.developertools.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.roysolberg.android.developertools.AppSettings;
import com.roysolberg.android.developertools.R;
import com.roysolberg.android.developertools.util.Utils;

import java.util.Locale;

// TODO: I18N
public class ResourceQualifiersFragment extends Fragment {

    private final static String TAG = ResourceQualifiersFragment.class.getSimpleName();
    private final static String TAG_CONFIG = "DeveloperTools";

    protected AppSettings appSettings;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appSettings = AppSettings.getInstance(getContext());
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        if (appSettings.showResourceQualifiersCollapsed()) {
            rootView = inflater.inflate(R.layout.fragment_resource_qualifiers_collapsed, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_resource_qualifiers_expanded, container, false);
        }
        setUpManualResourceQualifiers(rootView);
        return rootView;
    }

    private void setUpManualResourceQualifiers(View view) {
        final Configuration configuration = getResources().getConfiguration();

        String mccAndMnc = getString(R.string.fallback_no_qualifier);
        if (configuration.mcc != 0) { // NOTE: According to the docs, 0 if undefined, even though Wikipedia says 0 is used for test networks
            mccAndMnc = "mcc" + String.format(Locale.US, "%03d", configuration.mcc); // Mobile country codes should always be 3 decimals, but can be prefixed with 0
            mccAndMnc += "-mnc" + getBestGuessMobileNetworkCode(configuration);
        }
        ((TextView) view.findViewById(R.id.textView_mcc_and_mnc)).setText(mccAndMnc);

        ((TextView) view.findViewById(R.id.textView_locale)).setText(configuration.locale.getLanguage() + "-r" + configuration.locale.getCountry());

        String smallestWidth = getString(R.string.fallback_no_qualifier);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            if (configuration.smallestScreenWidthDp != Configuration.SMALLEST_SCREEN_WIDTH_DP_UNDEFINED) {
                smallestWidth = "sw" + configuration.smallestScreenWidthDp + "dp";
            }
        }
        ((TextView) view.findViewById(R.id.textView_smallest_width)).setText(smallestWidth);

        String availableWidth = getString(R.string.fallback_no_qualifier);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            if (configuration.screenWidthDp != Configuration.SCREEN_WIDTH_DP_UNDEFINED) {
                availableWidth = "w" + configuration.screenWidthDp + "dp";
            }
        }
        ((TextView) view.findViewById(R.id.textView_available_width)).setText(availableWidth);

        String availableHeight = getString(R.string.fallback_no_qualifier);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            if (configuration.screenHeightDp != Configuration.SCREEN_HEIGHT_DP_UNDEFINED) {
                availableHeight = "h" + configuration.screenHeightDp + "dp";
            }
        }
        ((TextView) view.findViewById(R.id.textView_available_height)).setText(availableHeight);
    }

    protected String getBestGuessMobileNetworkCode(Configuration configuration) {
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
                String simOperator = telephonyManager.getSimOperator();
                if (simOperator != null && simOperator.length() > 3) {
                    return simOperator.substring(3);
                }
            }
        }
        Log.w(TAG, "Falling back to configuration's MNC which is missing info about any 0 prefixing. MNC is [" + configuration.mnc + "]");
        return Integer.toString(configuration.mnc); // TODO: Should we warn the user that if the is number 10-99 it might have 0 prefixed and if the number is 0-9 it might have 0 or 00 prefixed?
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_resource_qualifiers, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        boolean folded = appSettings.showResourceQualifiersCollapsed();
        menu.findItem(R.id.item_expand).setVisible(folded);
        menu.findItem(R.id.item_collapse).setVisible(!folded);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int layoutIdToReplace = getActivity().findViewById(R.id.scrollView) == null ? R.id.layout_content : R.id.scrollView;
        switch (item.getItemId()) {
            case R.id.item_collapse:
                appSettings.setShowResourceQualifiersCollapsed(true);
                getFragmentManager().beginTransaction().replace(layoutIdToReplace, new ResourceQualifiersFragment()).commit();
                return true;
            case R.id.item_expand:
                appSettings.setShowResourceQualifiersCollapsed(false);
                getFragmentManager().beginTransaction().replace(layoutIdToReplace, new ResourceQualifiersFragment()).commit();
                return true;
            case R.id.item_share:
                sendConfiguration(getConfiguration());
                return true;
            case R.id.item_log:
                logConfiguration(getConfiguration());
                return true;
            case R.id.item_copy:
                copyConfiguration(getConfiguration());
                return true;
        }
        return false;
    }

    protected String getConfiguration() {
        StringBuilder configuration = new StringBuilder();
        configuration.append("Device: ").append(Utils.getDeviceSummary()).append("\n");
        configuration.append(getString(R.string.mcc_and_mnc));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_mcc_and_mnc));
        configuration.append("\n");
        configuration.append(getString(R.string.language_and_region));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_locale));
        configuration.append("\n");
        configuration.append(getString(R.string.layout_direction));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_layout_direction));
        configuration.append("\n");
        configuration.append(getString(R.string.smallestWidth));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_smallest_width));
        configuration.append("\n");
        configuration.append(getString(R.string.available_width));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_available_width));
        configuration.append("\n");
        configuration.append(getString(R.string.available_height));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_available_height));
        configuration.append("\n");
        configuration.append(getString(R.string.screen_size));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_screen_size));
        configuration.append("\n");
        configuration.append(getString(R.string.screen_aspect));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_screen_aspect));
        configuration.append("\n");
        configuration.append(getString(R.string.round_screen));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_round_screen));
        configuration.append("\n");
        configuration.append(getString(R.string.wide_color_gamut));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_wide_color_gamut));
        configuration.append("\n");
        configuration.append(getString(R.string.hdr));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_hdr));
        configuration.append("\n");
        configuration.append(getString(R.string.screen_orientation));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_screen_orientation));
        configuration.append("\n");
        configuration.append(getString(R.string.ui_mode));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_ui_mode));
        configuration.append("\n");
        configuration.append(getString(R.string.night_mode));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_night_mode));
        configuration.append("\n");
        configuration.append(getString(R.string.screen_pixel_density));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_dpi));
        configuration.append("\n");
        configuration.append(getString(R.string.touchscreen_type));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_touchscreen_type));
        configuration.append("\n");
        configuration.append(getString(R.string.keyboard_availability));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_keyboard_availability));
        configuration.append("\n");
        configuration.append(getString(R.string.primary_text_input_method));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_primary_text_input_method));
        configuration.append("\n");
        configuration.append(getString(R.string.navigation_key_availability));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_nav_key_availability));
        configuration.append("\n");
        configuration.append(getString(R.string.primary_nontouch_navigation_method));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_primary_nontouch_nav_method));
        configuration.append("\n");
        configuration.append(getString(R.string.platform_version));
        configuration.append(": ");
        configuration.append(getTextFromTextView(R.id.textView_platform_version));
        configuration.append("\n");
        return configuration.toString();
    }

    protected CharSequence getTextFromTextView(int resourceId) {
        return ((TextView) getView().findViewById(resourceId)).getText();
    }

    protected void sendConfiguration(String configuration) {
        final Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ("Resource qualifiers collected by Developer Tools " + Utils.getVersion(getContext())));
        sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, configuration);
        try {
            startActivity(sendIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "No activity found for sending configuration.", Toast.LENGTH_LONG).show();
        }
    }

    protected void logConfiguration(String configuration) {
        String[] configLines = configuration.split("\n");
        for (String line : configLines) {
            Log.i(TAG_CONFIG, line);
        }
        Toast.makeText(getContext(), "Resource qualifiers were written to log at log level INFO.", Toast.LENGTH_LONG).show();
    }

    protected void copyConfiguration(String configuration) {
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            clipboardManager.setText(configuration);
            Toast.makeText(getContext(), "Resource qualifiers copied to clipboard.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Unable to get clipboard manager.", Toast.LENGTH_LONG).show();
        }
    }

}
