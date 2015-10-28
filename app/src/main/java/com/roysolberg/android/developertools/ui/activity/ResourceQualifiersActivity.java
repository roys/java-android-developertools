package com.roysolberg.android.developertools.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;

import com.roysolberg.android.developertools.R;
import com.roysolberg.android.developertools.ui.fragment.ResourceQualifiersFragment;

public class ResourceQualifiersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition transition = new Fade();
            getWindow().setReturnTransition(transition);
            getWindow().setEnterTransition(transition);
        }

        setContentView(R.layout.activity_resource_qualifiers);
        getSupportFragmentManager().beginTransaction().add(R.id.scrollView, new ResourceQualifiersFragment()).commit();
    }

}
