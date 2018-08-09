package com.ruslaniusupov.achievity.home;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.adapters.GoalsAdapter;
import com.ruslaniusupov.achievity.data.model.Goal;
import com.ruslaniusupov.achievity.data.repository.AuthRepository;
import com.ruslaniusupov.achievity.data.repository.GoalRepository;
import com.ruslaniusupov.achievity.data.repository.UserDataRepository;
import com.ruslaniusupov.achievity.notes.NotesActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment implements GoalsAdapter.OnGoalClickListener {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private HomeViewModel mViewModel;
    private final GoalsAdapter mAdapter = new GoalsAdapter(null, this);
    private final GoalRepository mGoalRepository = new GoalRepository();
    private final UserDataRepository mUserDataRepository = new UserDataRepository();
    private Context mContext;

    @BindView(R.id.goals_rv)RecyclerView mGoalsRv;
    @BindView(R.id.progress_bar)ProgressBar mProgressBar;
    @BindView(R.id.state_tv)TextView mStateTv;

    public HomeFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view  = inflater.inflate(R.layout.fragment_goals, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "onActivityCreated");

        getLifecycle().addObserver(mGoalRepository);
        getLifecycle().addObserver(mUserDataRepository);

        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mViewModel.init(new AuthRepository(), mUserDataRepository, mGoalRepository);

        mProgressBar.setVisibility(View.VISIBLE);
        mStateTv.setVisibility(View.GONE);

        mGoalsRv.setAdapter(mAdapter);

        mViewModel.getGoals().observe(this, new Observer<List<Goal>>() {
            @Override
            public void onChanged(@Nullable List<Goal> goals) {
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
        Log.d(TAG, "onDestroy");
        getLifecycle().removeObserver(mUserDataRepository);
        getLifecycle().removeObserver(mGoalRepository);
    }
}
