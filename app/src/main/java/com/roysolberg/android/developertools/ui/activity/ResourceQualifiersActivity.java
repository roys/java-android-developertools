package com.roysolberg.android.developertools.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;

import com.roysolberg.android.developertools.R;

public class ResourceQualifiersActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_qualifiers);
        ((Toolbar) findViewById(R.id.toolbar)).setTitle(getString(R.string.resource_qualifiers));
    }

}
