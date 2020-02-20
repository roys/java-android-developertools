package com.roysolberg.android.developertools.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
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

import java.util.Locale;

// TODO: I18N
public class ScreenDimensionsFragment extends Fragment {

    private final static String TAG = ScreenDimensionsFragment.class.getSimpleName();
    private final static String TAG_CONFIG = "DeveloperTools";

    protected String screenDimensions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_screen_dimensions, container, false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        } else {
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }
        StringBuilder screenDimensions = new StringBuilder();
        screenDimensions.append("Device: ").append(Utils.getDeviceSummary()).append("\n");

        screenDimensions.append("Resolution: ");
        String temp = displayMetrics.widthPixels + " x " + displayMetrics.heightPixels + " pixels";
        ((TextView) rootView.findViewById(R.id.textView_displaySize)).setText(temp);
        screenDimensions.append(temp);
        screenDimensions.append("\n");

        screenDimensions.append("Logical density: ");
        temp = displayMetrics.density + " x dp";
        ((TextView) rootView.findViewById(R.id.textView_displayDensity)).setText(temp);
        screenDimensions.append(temp);
        screenDimensions.append("\n");

        screenDimensions.append("Font scaled density: ");
        temp = displayMetrics.scaledDensity + " x dp";
        ((TextView) rootView.findViewById(R.id.textView_displayScaledDensity)).setText(temp);
        screenDimensions.append(temp);
        screenDimensions.append("\n");

        screenDimensions.append("Density: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            temp = displayMetrics.densityDpi + " dots per inch (" + getString(R.string.qualifier_screen_pixel_density) + ")";
        } else {
            temp = "N/A";
        }
        ((TextView) rootView.findViewById(R.id.textView_displayDensityDpi)).setText(temp);
        screenDimensions.append(temp);
        screenDimensions.append("\n");

        screenDimensions.append("Exact density: ");
        temp = displayMetrics.xdpi + " x " + displayMetrics.ydpi + " dots per inch";
        ((TextView) rootView.findViewById(R.id.textView_displayExactDensityDpi)).setText(temp);
        screenDimensions.append(temp);
        screenDimensions.append("\n");

        screenDimensions.append("Approx. size: ");
        temp = getPhysicalSize(displayMetrics);
        ((TextView) rootView.findViewById(R.id.textView_approxSize)).setText(temp);
        screenDimensions.append(temp);
        screenDimensions.append("\n");

        this.screenDimensions = screenDimensions.toString();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_screen_dimensions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_share:
                sendscreenDimensions();
                return true;
            case R.id.item_log:
                logscreenDimensions();
                return true;
            case R.id.item_copy:
                copyscreenDimensions();
                return true;
        }
        return false;
    }

    protected String getPhysicalSize(DisplayMetrics displayMetrics) {
        float width = (float) displayMetrics.widthPixels / displayMetrics.xdpi;
        float height = (float) displayMetrics.heightPixels / displayMetrics.ydpi;
        return String.format(Locale.US, "%.01f", width) + "\" x " + String.format(Locale.US, "%.01f", height) + "\" (" + String.format(Locale.US, "%.01f", Math.sqrt(width * width + height * height)) + "\" diagonal)";
    }

    protected void sendscreenDimensions() {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ("Screen dimensions collected by Developer Tools " + Utils.getVersion(getActivity().getApplicationContext())));
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, screenDimensions);
        try {
            startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity().getApplicationContext(), "No activity found for sending e-mail.", Toast.LENGTH_LONG).show();
        }
    }

    protected void logscreenDimensions() {
        String[] configLines = screenDimensions.split("\n");
        for (String line : configLines) {
            Log.i(TAG_CONFIG, line);
        }
        Toast.makeText(getActivity().getApplicationContext(), "Screen dimensions were written to log at log level INFO.", Toast.LENGTH_LONG).show();
    }

    protected void copyscreenDimensions() {
        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            clipboardManager.setText(screenDimensions);
            Toast.makeText(getActivity().getApplicationContext(), "Screen dimensions copied to clipboard.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Unable to get clipboard manager.", Toast.LENGTH_LONG).show();
        }
    }

}
