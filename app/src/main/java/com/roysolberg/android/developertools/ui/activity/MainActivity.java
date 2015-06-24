package com.roysolberg.android.developertools.ui.activity;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.roysolberg.android.developertools.R;
import com.roysolberg.android.developertools.ToolDetailActivity;
import com.roysolberg.android.developertools.ToolDetailFragment;

// TODO: Extract strings
public class MainActivity extends FragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    protected boolean twoPaneMode; // Tablet vs phone
    protected int sdkVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_list);

//        AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appbarLayout.getLayoutParams();
//        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
//        if (behavior != null) {
//            behavior.onNestedFling((CoordinatorLayout) findViewById(R.id.coordinatorLayout), appbarLayout, null, 0, 10000, true);
//        }

        if (findViewById(R.id.tool_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            twoPaneMode = true;

            ((Toolbar) findViewById(R.id.toolbar)).setTitle(getString(R.string.app_name));

        } else {
            ((CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout)).setTitle(getString(R.string.app_name));
        }

        try {
            sdkVersion = Integer.parseInt(Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            /* no-op */
        }
        if (sdkVersion >= 18 && sdkVersion <= 19) { // Android 4.3-4.4
            findViewById(R.id.textView_settings_permissions).setVisibility(View.VISIBLE);
        }
    }

    public void onListItemClicked(View view) {
        switch (view.getId()) {
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

    /**
     * Callback method from {@link MainFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    public void onItemSelected(String id) {
        if (twoPaneMode) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ToolDetailFragment.ARG_ITEM_ID, id);
            ToolDetailFragment fragment = new ToolDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.tool_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ToolDetailActivity.class);
            detailIntent.putExtra(ToolDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
