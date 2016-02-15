package com.roysolberg.android.developertools.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;

import com.roysolberg.android.developertools.R;
import com.roysolberg.android.developertools.ui.fragment.SystemFeaturesFragment;

public class SystemFeaturesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition transition = new Fade();
            getWindow().setReturnTransition(transition);
            getWindow().setEnterTransition(transition);
        }

        setContentView(R.layout.activity_system_features);
//        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        // We actually have to use replace() instead of add() or else this will fail with
        // java.lang.IllegalStateException: ScrollView can host only one direct child
        // even though activity (and hopefully Fragment) is recreated.
        getSupportFragmentManager().beginTransaction().replace(R.id.scrollView, new SystemFeaturesFragment()).commit();
    }

}
