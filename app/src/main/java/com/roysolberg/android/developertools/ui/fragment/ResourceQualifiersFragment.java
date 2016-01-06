package com.roysolberg.android.developertools.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roysolberg.android.developertools.AppSettings;
import com.roysolberg.android.developertools.R;

public class ResourceQualifiersFragment extends Fragment {

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
        if (appSettings.showResourceQualifiersCollapsed()) {
            return inflater.inflate(R.layout.fragment_resource_qualifiers_collapsed, container, false);
        }
        return inflater.inflate(R.layout.fragment_resource_qualifiers_expanded, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpManualResourceQualifiers(view);
    }

    private void setUpManualResourceQualifiers(View view) {
        Configuration configuration = getResources().getConfiguration();
        String mcc = getString(R.string.fallback_no_qualifier);
        if(configuration.mcc != 0){
            mcc = Integer.toString(configuration.mcc);
        }
        String mnc = getString(R.string.fallback_no_qualifier);
        if(configuration.mnc != 0){
            mnc = Integer.toString(configuration.mnc);
        }
        ((TextView)view.findViewById(R.id.textView_mcc)).setText(mcc);
        ((TextView)view.findViewById(R.id.textView_mnc)).setText(mnc);
        ((TextView)view.findViewById(R.id.textView_locale)).setText(configuration.locale.toString());
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
        switch (item.getItemId()) {
            case R.id.item_collapse:
                appSettings.setShowResourceQualifiersCollapsed(true);
                getFragmentManager().beginTransaction().replace(R.id.scrollView, new ResourceQualifiersFragment()).commit();
                return true;
            case R.id.item_expand:
                appSettings.setShowResourceQualifiersCollapsed(false);
                getFragmentManager().beginTransaction().replace(R.id.scrollView, new ResourceQualifiersFragment()).commit();
                return true;
        }
        return false;
    }

}
