package com.ruslaniusupov.achievity.profile;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ruslaniusupov.achievity.MainActivity;
import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.adapters.GoalsAdapter;
import com.ruslaniusupov.achievity.addeditgoal.AddEditGoalActivity;
import com.ruslaniusupov.achievity.data.model.Goal;
import com.ruslaniusupov.achievity.data.model.User;
import com.ruslaniusupov.achievity.data.repository.AuthRepository;
import com.ruslaniusupov.achievity.data.repository.GoalRepository;
import com.ruslaniusupov.achievity.data.repository.UserDataRepository;
import com.ruslaniusupov.achievity.notes.NotesActivity;
import com.ruslaniusupov.achievity.auth.AuthActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileFragment extends Fragment implements GoalsAdapter.OnGoalClickListener {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private static final String ARG_USER_ID = "com.ruslaniusupov.achievity.USER_ID";

    private Context mContext;
    private final GoalsAdapter mAdapter = new GoalsAdapter(null, this);
    private final UserDataRepository mUserDataRepository = new UserDataRepository();
    private final GoalRepository mGoalRepository = new GoalRepository();
    private ProfileViewModel mViewModel;

    @BindView(R.id.name)TextView mNameTv;
    @BindView(R.id.bio)TextView mBioTv;
    @BindView(R.id.goals_rv)RecyclerView mGoalsRv;
    @BindView(R.id.add_goal_fab)FloatingActionButton mAddGoalFab;
    @BindView(R.id.progress_bar)ProgressBar mProgressBar;
    @BindView(R.id.state_tv)TextView mStateTv;

    public ProfileFragment() {}

    public static ProfileFragment newInstance(String userId) {
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(args);
        return profileFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        String userId = getArguments().getString(ARG_USER_ID);
        Log.d(TAG, "Current user ID: " + userId);
        getLifecycle().addObserver(mUserDataRepository);
        getLifecycle().addObserver(mGoalRepository);
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        mViewModel.init(userId, new AuthRepository(), mGoalRepository, mUserDataRepository);

        mProgressBar.setVisibility(View.VISIBLE);
        mStateTv.setVisibility(View.GONE);

        mGoalsRv.setAdapter(mAdapter);
        mGoalsRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    mAddGoalFab.hide();
                } else if (dy < 0) {
                    mAddGoalFab.show();            }
            }
        });

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

        updateUserData();

        updateUi();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_sign_out:
                signOut();
                return true;
            case R.id.action_edit_profile:
                editProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @OnClick(R.id.add_goal_fab)
    public void addGoal() {
        Intent launchAddGoal = new Intent(getActivity(), AddEditGoalActivity.class);
        launchAddGoal.putExtra(AddEditGoalActivity.EXTRA_USER_ID, mViewModel.getCurrentUserId());
        startActivity(launchAddGoal);
    }

    private void updateUserData() {
        mViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user == null) {
                    Log.d(TAG, "User is NULL");
                    return;
                }
                mNameTv.setText(user.getName());
                if (!TextUtils.isEmpty(user.getBio())) {
                    mBioTv.setText(user.getBio());
                }
            }
        });
    }

    private void editProfile() {
        Intent launchEditProfile = new Intent(getActivity(), EditProfileActivity.class);
        launchEditProfile.putExtra(EditProfileActivity.EXTRA_USER_ID, mViewModel.getUserId());
        startActivity(launchEditProfile);
    }

    private void signOut() {
        AuthUI.getInstance().signOut(getActivity())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    }
                });
    }

    private void updateUi() {
        if (TextUtils.equals(mViewModel.getCurrentUserId(), mViewModel.getUserId())) {
            mAddGoalFab.setVisibility(View.VISIBLE);
        } else {
            mAddGoalFab.setVisibility(View.GONE);
        }
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
        getLifecycle().removeObserver(mUserDataRepository);
        getLifecycle().removeObserver(mGoalRepository);
    }
}
