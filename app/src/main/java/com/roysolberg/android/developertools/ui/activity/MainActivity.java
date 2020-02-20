package com.roysolberg.android.developertools.ui.activity;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.roysolberg.android.developertools.R;
import com.roysolberg.android.developertools.ui.fragment.InstallAppDialogFragment;
import com.roysolberg.android.developertools.ui.fragment.ResourceQualifiersFragment;
import com.roysolberg.android.developertools.ui.fragment.ScreenDimensionsFragment;
import com.roysolberg.android.developertools.ui.fragment.SystemFeaturesFragment;
import com.roysolberg.android.developertools.util.Utils;

// TODO: Extract strings
// TODO: Add utm_source to links to Play Store
// TODO: Link to ADB Wireless?
// TODO: Link to https://android.googlesource.com/platform/packages/apps/Settings/+/android-6.0.1_r68/AndroidManifest.xml#1082 ?
// TODO: Add link to new AppOps (https://android.googlesource.com/platform/packages/apps/Settings/+/android-6.0.1_r68/AndroidManifest.xml#1214 / PRIVACY_SETTINGS)
// TODO: Move app links from layout to some config xml
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    protected boolean twoPaneMode; // Tablet vs phone
    protected int sdkVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        // TODO: Find a better solution for the icon - we have to manually remove it because of how we use the fragment manager and the other fragments.
//        if (savedInstanceState == null) {
//            ImageView logoImageView = new ImageView(getApplicationContext());
//            logoImageView.eetImageDrawable(getResources().getDrawable(R.drawable.collapsable_toolbar_icon));
//            ((ScrollView) findViewById(R.id.layout_content)).addView(logoImageView);
//        }
        if (savedInstanceState != null) {
            ViewGroup viewGroup = (ViewGroup) findViewById(R.id.layout_content);
            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }
        }

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

        Toolbar toolbar = ((Toolbar) findViewById(R.id.toolbar));
        if (findViewById(R.id.layout_content) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            twoPaneMode = true;
            toolbar.setTitle(getString(R.string.app_name));

        } else { // Not two pane
            if (sdkVersion > 7) { // TODO: The collapsing toolbar and nested scroll view and stuff hardly worked on Xperia X10 (2.3.3/9) and Nexus One (2.3.6/10), any chance to get it to work?
                ((CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout)).setTitle(getString(R.string.app_name));
            } else {
                toolbar.setTitle(getString(R.string.app_name));
            }
        }
        setSupportActionBar(toolbar);

        if (sdkVersion >= 18 && sdkVersion <= 19) { // Android 4.3-4.4
            findViewById(R.id.textView_settings_permissions).setVisibility(View.VISIBLE);
            findViewById(R.id.include_list_divider).setVisibility(View.VISIBLE);
        }
        if (sdkVersion < 15) {
            findViewById(R.id.textView_app_cleanStatusBar).setVisibility(View.GONE);
            findViewById(R.id.include_list_divider_clean_status_bar).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_about_app:
                showAboutAppDialog();
                return true;
        }
        return false;
    }

    protected void showAboutAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final SpannableString spannableString = new SpannableString("This app was originally just created for myself to make some developement tasks a bit easier. I've released it to Play Store hoping that someone else might find it useful.\n\nIf you want to get in touch me, please send me a mail at dev@roysolberg.com.\n\nPlease note that I take no credit for the third party apps.");
        Linkify.addLinks(spannableString, Linkify.ALL);
        builder.setTitle("Developer Tools " + Utils.getVersion(getApplicationContext()))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(spannableString)
                .setCancelable(true)
                .setPositiveButton("Ok, thanks", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNeutralButton("Rate at Play Store", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=" + getApplicationContext().getPackageName()));
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(getApplicationContext(), "No app found for opening Play Store URL.", Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        TextView messageTextView = ((TextView) alertDialog.findViewById(android.R.id.message));
        if (messageTextView != null) {
            messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public void onListItemClicked(View view) {
        // TODO: Mark selected item in two pane mode
        switch (view.getId()) {
            case R.id.textView_resource_qualifiers:
                if (twoPaneMode) {
                    ((ViewGroup) findViewById(R.id.layout_content)).removeAllViews();
                    getSupportFragmentManager().beginTransaction().replace(R.id.layout_content, new ResourceQualifiersFragment()).commit();
                } else {
                    startActivity(new Intent(getApplicationContext(), ResourceQualifiersActivity.class));
                }
                break;
            case R.id.textView_system_features:
                if (twoPaneMode) {
                    ((ViewGroup) findViewById(R.id.layout_content)).removeAllViews();
                    getSupportFragmentManager().beginTransaction().replace(R.id.layout_content, new SystemFeaturesFragment()).commit();
                } else {
                    startActivity(new Intent(getApplicationContext(), SystemFeaturesActivity.class));
                }
                break;
            case R.id.textView_screen_dimensions:
                if (twoPaneMode) {
                    ((ViewGroup) findViewById(R.id.layout_content)).removeAllViews();
                    getSupportFragmentManager().beginTransaction().replace(R.id.layout_content, new ScreenDimensionsFragment()).commit();
                } else {
                    startActivity(new Intent(getApplicationContext(), ScreenDimensionsActivity.class));
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
            case R.id.textView_app_cleanStatusBar:
                startApp(R.string.package_name_clean_status_bar);
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
        // TODO: Add link to Clean Status Bar? Or is it out of scope?
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
                titleResId = R.string.manifestviewer;
                aboutAppResId = R.string.about_manifestviewer;
                break;
            case R.string.package_name_clean_status_bar:
                titleResId = R.string.clean_status_bar;
                aboutAppResId = R.string.about_clean_status_bar;
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
