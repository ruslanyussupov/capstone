package com.ruslaniusupov.achievity.adapters;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.data.model.Goal;
import com.ruslaniusupov.achievity.data.repository.AuthRepository;
import com.ruslaniusupov.achievity.data.repository.GoalRepository;
import com.ruslaniusupov.achievity.data.repository.UserDataRepository;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private static final String TAG = GoalsAdapter.class.getSimpleName();

    private List<Goal> mGoals;
    private final OnGoalClickListener mClickListener;

    public GoalsAdapter(List<Goal> goals, OnGoalClickListener listener) {
        mGoals = goals;
        mClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mGoals.get(position));
    }

    @Override
    public int getItemCount() {
        return mGoals == null ? 0 : mGoals.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements GoalItemContract.View {

        private final GoalItemContract.Presenter mPresenter;

        @BindView(R.id.author)TextView mAuthorTv;
        @BindView(R.id.publish_date)TextView mPubDateTv;
        @BindView(R.id.body)TextView mBodyTv;
        @BindView(R.id.fav_btn)ImageButton mFavBtn;
        @BindView(R.id.notifications_btn)ImageButton mNotificationsBtn;
        @BindView(R.id.subscribers_count_tv)TextView mSubscribersCountTv;
        @BindView(R.id.likes_count_tv)TextView mLikesCountTv;
        @BindView(R.id.notes_count_tv)TextView mNotesCountTv;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mPresenter = new GoalItemPresenter(this, new UserDataRepository(),
                    new GoalRepository(), new AuthRepository());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "On Goal click");
                    mClickListener.onClick(mGoals.get(getAdapterPosition()));
                }
            });
        }

        void bind(final Goal goal) {

            mPresenter.updateUi(goal);

            mFavBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFavBtn.isSelected()) {
                        mFavBtn.setSelected(false);
                        mPresenter.cancelLike(goal.getId());
                    } else {
                        mFavBtn.setSelected(true);
                        mPresenter.like(goal.getId());
                    }
                }
            });

            mNotificationsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mNotificationsBtn.isSelected()) {
                        mNotificationsBtn.setSelected(false);
                        mPresenter.unsubscribe(goal.getId());
                    } else {
                        mNotificationsBtn.setSelected(true);
                        mPresenter.subscribe(goal.getId());
                    }
                }
            });

        }

        @Override
        public void showAuthor(String author) {
            mAuthorTv.setText(author);
        }

        @Override
        public void showPubDate(String date) {
            mPubDateTv.setText(date);
        }

        @Override
        public void showText(String text) {
            mBodyTv.setText(text);
        }

        @Override
        public void showLikesCount(String count) {
            mLikesCountTv.setText(count);
        }

        @Override
        public void showNotesCount(String count) {
            mNotesCountTv.setText(count);
        }

        @Override
        public void showSubscribersCount(String count) {
            mSubscribersCountTv.setText(count);
        }

        @Override
        public void setLikeBtnState(boolean isSelected) {
            mFavBtn.setSelected(isSelected);
        }

        @Override
        public void setSubscribeBtnState(boolean isSelected) {
            mNotificationsBtn.setSelected(isSelected);
        }
    }

    public void swapData(List<Goal> goals) {
        if (mGoals == null) {
            mGoals = goals;
            notifyDataSetChanged();
        } else {
            Log.d(TAG, "Old goals size: " + mGoals.size());
            Log.d(TAG, "New goals size: " + goals.size());
            DiffUtil.DiffResult diffResult =
                    DiffUtil.calculateDiff(new GoalDiffCallback(mGoals, goals), true);
            mGoals = goals;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    private class GoalDiffCallback extends DiffUtil.Callback {

        private final List<Goal> mOldGoals;
        private final List<Goal> mNewGoals;

        GoalDiffCallback(List<Goal> oldGoals, List<Goal> newGoals) {
            mOldGoals = oldGoals;
            mNewGoals = newGoals;
        }

        @Override
        public int getOldListSize() {
            return mOldGoals == null ? 0 : mOldGoals.size();
        }

        @Override
        public int getNewListSize() {
            return mNewGoals == null ? 0 : mNewGoals.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return TextUtils.equals(
                    mOldGoals.get(oldItemPosition).getId(),
                    mNewGoals.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return TextUtils.equals(
                    mOldGoals.get(oldItemPosition).getText(),
                    mNewGoals.get(newItemPosition).getText());
        }
    }

    public interface OnGoalClickListener {
        void onClick(Goal goal);
    }

}
