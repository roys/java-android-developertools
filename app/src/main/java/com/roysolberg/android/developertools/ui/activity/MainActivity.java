package com.roysolberg.android.developertools.ui.activity;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.roysolberg.android.developertools.R;
import com.roysolberg.android.developertools.ui.fragment.InstallAppDialogFragment;
import com.roysolberg.android.developertools.ui.fragment.ResourceQualifiersFragment;

// TODO: Extract strings
public class MainActivity extends FragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    protected boolean twoPaneMode; // Tablet vs phone
    protected int sdkVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition transition = new Fade();
            getWindow().setExitTransition(transition);
            getWindow().setReenterTransition(transition);
        }

        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this);

//        AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appbarLayout.getLayoutParams();
//        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
//        if (behavior != null) {
//            behavior.onNestedFling((CoordinatorLayout) findViewById(R.id.coordinatorLayout), appbarLayout, null, 0, 10000, true);
//        }

        try {
            sdkVersion = Integer.parseInt(Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            /* no-op */
        }

        if (findViewById(R.id.layout_content) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            twoPaneMode = true;

            ((Toolbar) findViewById(R.id.toolbar)).setTitle(getString(R.string.app_name));

        } else { // Not two pane
            if (sdkVersion > 10) { // TODO: The collapsing toolbar and nested scroll view and stuff hardly worked on Xperia X10 (2.3.3/9) and Nexus One (2.3.6/10), any chance to get it to work?
                ((CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout)).setTitle(getString(R.string.app_name));
            } else {
                ((Toolbar) findViewById(R.id.toolbar)).setTitle(getString(R.string.app_name));
            }
        }

        if (sdkVersion >= 18 && sdkVersion <= 19) { // Android 4.3-4.4
            findViewById(R.id.textView_settings_permissions).setVisibility(View.VISIBLE);
            findViewById(R.id.include_list_divider).setVisibility(View.VISIBLE);
        }
    }

    public void onListItemClicked(View view) {
        switch (view.getId()) {
            case R.id.textView_resource_qualifiers:
                if (twoPaneMode) {
                    ((ViewGroup) findViewById(R.id.layout_content)).removeAllViews();
                    getSupportFragmentManager().beginTransaction().replace(R.id.layout_content, new ResourceQualifiersFragment()).commit();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        startActivity(new Intent(getApplicationContext(), ResourceQualifiersActivity.class), ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
                    } else {
                        startActivity(new Intent(getApplicationContext(), ResourceQualifiersActivity.class));
                    }
                }
                break;
            case R.id.textView_app_dalvik_explorer:
                startApp(R.string.package_name_dalvik_explorer);
                break;
            case R.id.textView_app_alogcat:
                startApp(R.string.package_name_alogcat, R.string.package_name_alogcat_free);
                break;
            case R.id.textView_app_manifestviewer:
                startApp(R.string.package_name_manifestviewer);
                break;
            case R.id.textView_settings_development:
                startDevelopmentSettingsActivity();
                break;
            case R.id.textView_settings_apps:
                startApplicationSettingsActivity();
                break;
            case R.id.textView_settings_permissions:
                startPermissionManager();
                break;
        }
    }

    private void startApp(int... packageNameResIds) {
        boolean appFound = false;
        for (int packageNameResId : packageNameResIds) {
            try {
                startActivity(getPackageManager().getLaunchIntentForPackage(getString(packageNameResId)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                appFound = true;
            } catch (Exception e) {
                /* no-op */
            }
        }
        if (!appFound) {
            showInstallAppDialog(packageNameResIds[0]);
        }
    }

    protected void showInstallAppDialog(int packageNameResId) {
        int titleResId, aboutAppResId;
        switch (packageNameResId) {
            case R.string.package_name_dalvik_explorer:
                titleResId = R.string.dalvik_explorer;
                aboutAppResId = R.string.about_dalvik_explorer;
                break;
            case R.string.package_name_alogcat:
                packageNameResId = R.string.package_name_alogcat_free; // We send the user to the free version
                titleResId = R.string.alogcat;
                aboutAppResId = R.string.about_alogcat;
                break;
            case R.string.package_name_manifestviewer:
                titleResId = R.string.dalvik_explorer;
                aboutAppResId = R.string.about_manifestviewer;
                break;
            default:
                throw new IllegalArgumentException("Unknown app with package name [" + getString(packageNameResId) + "]. Unable to show install dialog.");
        }
        DialogFragment installAppDialogFragment = InstallAppDialogFragment.newInstance(getString(R.string.install_app, getString(titleResId)), getString(aboutAppResId), getString(packageNameResId));
        installAppDialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    protected void startDevelopmentSettingsActivity() {
        try {
            startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (ActivityNotFoundException e) {
            startDevelopmentSettingsActivityWithAlternativeMethod();
        } catch (NullPointerException e) { // This has happened in some rare (3) cases for some reason.
            Log.e(TAG, "Got NullPointerException while trying to start development settings. Trying alternative method.", e);
            startDevelopmentSettingsActivityWithAlternativeMethod();
        }
    }

    protected void startDevelopmentSettingsActivityWithAlternativeMethod() {
        try {
            startActivity(new Intent("com.android.settings.APPLICATION_DEVELOPMENT_SETTINGS").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); // Bug in FROYO
        } catch (ActivityNotFoundException e2) {
            try {
                startActivity(new Intent(Intent.ACTION_MAIN).setComponent(new ComponentName("com.android.settings", "com.android.settings.DevelopmentSettings")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); // The way to do it on at least LG GW620 Eve running Android 1.5
            } catch (ActivityNotFoundException e3) {
                Toast.makeText(getApplicationContext(), "Unable to open development settings directly.", Toast.LENGTH_SHORT).show();
                try {
                    startActivity(new Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } catch (ActivityNotFoundException e4) {
                    Toast.makeText(getApplicationContext(), "Unable to open device settings.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    protected void startApplicationSettingsActivity() {
        try {
            startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Unable to open applications.", Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void startPermissionManager() {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.Settings");
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(":android:show_fragment", "com.android.settings.applications.AppOpsSummary");
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Permission manager can only be accessed on a few Android versions (4.3-4.4.1).", Toast.LENGTH_LONG).show();
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Got ActivityNotFoundException while trying to start permission manager.", e);
            Toast.makeText(getApplicationContext(), "Unable to open permission manager.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Got Exception while trying to start permission manager.", e);
            Toast.makeText(getApplicationContext(), "Unable to open permission manager.", Toast.LENGTH_SHORT).show();
        }
    }

}
