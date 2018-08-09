package com.ruslaniusupov.achievity.discover;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.adapters.GoalsAdapter;
import com.ruslaniusupov.achievity.data.model.Goal;
import com.ruslaniusupov.achievity.data.repository.GoalRepository;
import com.ruslaniusupov.achievity.discover.DiscoverViewModel.Sort;
import com.ruslaniusupov.achievity.notes.NotesActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DiscoverFragment extends Fragment implements GoalsAdapter.OnGoalClickListener {

    private static final String TAG = DiscoverFragment.class.getSimpleName();

    private Context mContext;
    private DiscoverViewModel mViewModel;
    private final GoalsAdapter mAdapter = new GoalsAdapter(null, this);
    private final GoalRepository mGoalRepository = new GoalRepository();

    @BindView(R.id.goals_rv)RecyclerView mGoalsRv;
    @BindView(R.id.progress_bar)ProgressBar mProgressBar;
    @BindView(R.id.state_tv)TextView mStateTv;

    public DiscoverFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        getLifecycle().addObserver(mGoalRepository);

        mViewModel = ViewModelProviders.of(this).get(DiscoverViewModel.class);
        mViewModel.init(mGoalRepository);

        mProgressBar.setVisibility(View.VISIBLE);
        mStateTv.setVisibility(View.GONE);

        mGoalsRv.setAdapter(mAdapter);

        getGoals();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.discover, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        switch (mViewModel.getSort()) {
            case RECENT:
                menu.findItem(R.id.action_recent).setChecked(true);
                break;
            case POPULAR:
                menu.findItem(R.id.action_popular).setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_recent:
                Log.d(TAG, "Recent selected.");
                item.setChecked(true);
                mViewModel.setSort(Sort.RECENT);
                getGoals();
                return true;
            case R.id.action_popular:
                Log.d(TAG, "Most liked selected.");
                item.setChecked(true);
                mViewModel.setSort(Sort.POPULAR);
                getGoals();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getGoals() {
        mViewModel.getGoals().observe(this, new Observer<List<Goal>>() {
            @Override
            public void onChanged(@Nullable List<Goal> goals) {
                Log.d(TAG, "onChanged");
                mProgressBar.setVisibility(View.GONE);
                if (goals == null || goals.size() == 0) {
                    mStateTv.setVisibility(View.VISIBLE);
                    mStateTv.setText(getString(R.string.state_no_goals));
                } else {
                    mStateTv.setVisibility(View.GONE);
                    mAdapter.swapData(goals);
                }
            }
        });
    }

    @Override
    public void onClick(Goal goal) {
        Intent openNotesActivity = new Intent(getActivity(), NotesActivity.class);
        openNotesActivity.putExtra(NotesActivity.EXTRA_GOAL_ID, goal.getId());
        openNotesActivity.putExtra(NotesActivity.EXTRA_AUTHOR_ID, goal.getAuthorId());
        startActivity(openNotesActivity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver(mGoalRepository);
    }
}
