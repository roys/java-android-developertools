package com.roysolberg.android.developertools.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.roysolberg.android.developertools.R;
import com.roysolberg.android.developertools.ui.fragment.NetworkInfoFragment;

public class NetworkInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_qualifiers);
        getSupportFragmentManager().beginTransaction().replace(R.id.scrollView, new NetworkInfoFragment()).commit();
    }

}
