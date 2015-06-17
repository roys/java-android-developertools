package com.roysolberg.android.developertools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * An activity representing a list of Tools. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ToolDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ToolListFragment} and the item details
 * (if present) is a {@link ToolDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link ToolListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ToolListActivity extends Activity
        implements ToolListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_list);

        ((CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout)).setTitle(getString(R.string.app_name));
//        AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appbarLayout.getLayoutParams();
//        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
//        if (behavior != null) {
//            behavior.onNestedFling((CoordinatorLayout) findViewById(R.id.coordinatorLayout), appbarLayout, null, 0, 10000, true);
//        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_test);
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            recyclerView.setAdapter(new RecyclerView.Adapter() {

                class ViewHolder extends RecyclerView.ViewHolder {
                    // each data item is just a string in this case
                    public TextView mTextView;

                    public ViewHolder(TextView v) {
                        super(v);
                        mTextView = v;
                    }
                }

                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    return new ViewHolder(new TextView(getApplicationContext()));
                }

                @Override
                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    ((ViewHolder) holder).mTextView.setText(":" + position);
                }

                @Override
                public int getItemCount() {
                    return 20;
                }
            });
        }

        if (findViewById(R.id.tool_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ToolListFragment) getFragmentManager()
                    .findFragmentById(R.id.tool_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link ToolListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
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
