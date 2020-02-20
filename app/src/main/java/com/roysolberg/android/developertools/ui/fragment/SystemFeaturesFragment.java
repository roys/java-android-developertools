package com.roysolberg.android.developertools.ui.fragment;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.os.Build;
import android.os.Bundle;
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

import com.roysolberg.android.developertools.R;
import com.roysolberg.android.developertools.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// TODO: I18N
@TargetApi(Build.VERSION_CODES.ECLAIR)
public class SystemFeaturesFragment extends Fragment {

    private final static String TAG = SystemFeaturesFragment.class.getSimpleName();
    private final static String TAG_CONFIG = "DeveloperTools";

    protected String systemFeatures;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_system_features, container, false);
        setUpSystemFeatures((TextView) rootView);
        return rootView;
    }

    private void setUpSystemFeatures(TextView systemFeaturesTextView) {
        FeatureInfo[] featureInfoArray = getActivity().getPackageManager().getSystemAvailableFeatures();
        List<String> features = new ArrayList<>();
        for (FeatureInfo featureInfo : featureInfoArray) {
            final String name = featureInfo.name;
            if (name != null) {
                features.add(name);
            }
        }
        Collections.sort(features, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });
        StringBuilder systemFeatures = new StringBuilder();
        for (String feature : features) {
            systemFeatures.append(feature);
            systemFeatures.append("\n");
        }
        this.systemFeatures = systemFeatures.toString();
        systemFeaturesTextView.setText(this.systemFeatures);
        this.systemFeatures = "Device: " + Utils.getDeviceSummary() + "\n" + this.systemFeatures;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_system_features, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_share:
                sendConfiguration();
                return true;
            case R.id.item_log:
                logConfiguration();
                return true;
            case R.id.item_copy:
                copyConfiguration();
                return true;
        }
        return false;
    }

    protected void sendConfiguration() {
        final Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, ("System features collected by Developer Tools " + Utils.getVersion(getContext())));
        sendIntent.putExtra(Intent.EXTRA_TEXT, systemFeatures);
        try {
            startActivity(sendIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "No activity found for sending configuration.", Toast.LENGTH_LONG).show();
        }
    }

    protected void logConfiguration() {
        String[] configLines = systemFeatures.split("\n");
        for (String line : configLines) {
            Log.i(TAG_CONFIG, line);
        }
        Toast.makeText(getContext(), "System features were written to log at log level INFO.", Toast.LENGTH_LONG).show();
    }

    protected void copyConfiguration() {
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            clipboardManager.setText(systemFeatures);
            Toast.makeText(getContext(), "System features copied to clipboard.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Unable to get clipboard manager.", Toast.LENGTH_LONG).show();
        }
    }

}
