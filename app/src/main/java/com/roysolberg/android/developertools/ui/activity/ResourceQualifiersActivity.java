package com.roysolberg.android.developertools.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.roysolberg.android.developertools.R;
import com.roysolberg.android.developertools.ui.fragment.ResourceQualifiersFragment;

public class ResourceQualifiersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_resource_qualifiers);
//        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        // We actually have to use replace() instead of add() or else this will fail with
        // java.lang.IllegalStateException: ScrollView can host only one direct child
        // even though activity (and hopefully Fragment) is recreated.
        getSupportFragmentManager().beginTransaction().replace(R.id.scrollView, new ResourceQualifiersFragment()).commit();
    }

}
