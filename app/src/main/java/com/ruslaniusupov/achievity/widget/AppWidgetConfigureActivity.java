package com.ruslaniusupov.achievity.widget;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.adapters.GoalsAdapter;
import com.ruslaniusupov.achievity.auth.AuthActivity;
import com.ruslaniusupov.achievity.data.model.Goal;
import com.ruslaniusupov.achievity.data.model.Note;
import com.ruslaniusupov.achievity.data.repository.AuthRepository;
import com.ruslaniusupov.achievity.data.repository.GoalRepository;
import com.ruslaniusupov.achievity.data.repository.NoteRepository;
import com.ruslaniusupov.achievity.data.repository.UserDataRepository;
import com.ruslaniusupov.achievity.data.repository.WidgetRepository;
import com.ruslaniusupov.achievity.sync.WidgetDataSynchronizer;
import com.ruslaniusupov.achievity.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AppWidgetConfigureActivity extends AppCompatActivity
        implements GoalsAdapter.OnGoalClickListener {

    private static final String TAG = AppWidgetConfigureActivity.class.getSimpleName();
    private static final int RC_AUTH = 200;

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private WidgetConfigureViewModel mViewModel;
    private final GoalsAdapter mAdapter = new GoalsAdapter(null, this);
    private final NoteRepository mNoteRepository = new NoteRepository();
    private final GoalRepository mGoalRepository = new GoalRepository();
    private final UserDataRepository mUserDataRepository = new UserDataRepository();

    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.goals_rv)RecyclerView mGoalsRv;
    @BindView(R.id.coordinator)CoordinatorLayout mCoordinator;
    @BindView(R.id.progress_bar)ProgressBar mProgressBar;
    @BindView(R.id.state_tv)TextView mStateTv;
    @BindView(R.id.no_connection)ImageView mNoConnectionIv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fav_goal_widget_configure);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        mProgressBar.setVisibility(View.VISIBLE);
        mStateTv.setVisibility(View.GONE);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.e(TAG, "Widget ID is invalid.");
            finish();
            return;
        }

        getLifecycle().addObserver(mNoteRepository);
        getLifecycle().addObserver(mGoalRepository);
        getLifecycle().addObserver(mUserDataRepository);

        mViewModel = ViewModelProviders.of(this).get(WidgetConfigureViewModel.class);
        mViewModel.init(new AuthRepository(), mGoalRepository, mUserDataRepository,
                mNoteRepository, new WidgetRepository(this));

        if (mViewModel.isAuthoried()) {
            initAdapter();
        } else {
            startActivityForResult(new Intent(this, AuthActivity.class), RC_AUTH);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_AUTH) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Auth passed.");
                mViewModel.initUserData();
                initAdapter();
            }
        }

    }

    @Override
    public void onClick(final Goal goal) {

        Log.d(TAG, "On click");

        mGoalsRv.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        mViewModel.getNotes(goal.getId()).observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {
                mViewModel.getWidgetDataSource().addNotes(mAppWidgetId, notes);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(AppWidgetConfigureActivity.this);
                AppWidget.updateAppWidget(AppWidgetConfigureActivity.this,
                        appWidgetManager, mAppWidgetId);

                WidgetDataSynchronizer.scheduleSync(AppWidgetConfigureActivity.this,
                        goal.getId(), mAppWidgetId);

                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver(mNoteRepository);
        getLifecycle().removeObserver(mGoalRepository);
        getLifecycle().removeObserver(mUserDataRepository);
    }

    private void initAdapter() {
        if (Utils.hasNetworkConnection(this)) {
            mNoConnectionIv.setVisibility(View.GONE);
            mGoalsRv.setAdapter(mAdapter);
            mViewModel.getFavGoals().observe(this, new Observer<List<Goal>>() {
                @Override
                public void onChanged(@Nullable List<Goal> goals) {
                    mProgressBar.setVisibility(View.GONE);
                    if (goals == null || goals.size() == 0) {
                        mStateTv.setVisibility(View.VISIBLE);
                        mStateTv.setText(getString(R.string.state_no_goals));
                        return;
                    }
                    mStateTv.setVisibility(View.GONE);
                    mAdapter.swapData(goals);
                }
            });
        } else {
            mNoConnectionIv.setVisibility(View.VISIBLE);
            showNoConnectionSnackbar();
        }
    }

    private void showNoConnectionSnackbar() {
        Snackbar.make(mCoordinator, getString(R.string.state_no_network_connection), Snackbar.LENGTH_LONG)
                .show();
    }

}

