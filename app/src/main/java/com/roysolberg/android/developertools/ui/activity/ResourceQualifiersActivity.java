package com.roysolberg.android.developertools.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.view.View;

import com.roysolberg.android.developertools.R;

public class ResourceQualifiersActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition transition = new Fade();
            getWindow().setReturnTransition(transition);
            getWindow().setEnterTransition(transition);
        }

        setContentView(R.layout.activity_resource_qualifiers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.resource_qualifiers));
        toolbar.setNavigationIcon(R.drawable.tinted_arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(ResourceQualifiersActivity.this);
            }
        });
    }

}
