package com.ruslaniusupov.achievity.adapters;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.data.model.Comment;
import com.ruslaniusupov.achievity.data.repository.AuthRepository;
import com.ruslaniusupov.achievity.data.repository.CommentRepository;
import com.ruslaniusupov.achievity.data.repository.UserDataRepository;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private List<Comment> mComments;

    public CommentsAdapter(List<Comment> comments) {
        mComments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mComments.get(position));
    }

    @Override
    public int getItemCount() {
        return mComments == null ? 0 : mComments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements CommentItemContract.View {

        private final CommentItemContract.Presenter mPresenter;

        @BindView(R.id.author)TextView mAuthorTv;
        @BindView(R.id.publish_date)TextView mPublishDateTv;
        @BindView(R.id.body)TextView mBodyTv;
        @BindView(R.id.likes_counter)TextView mLikesCount;
        @BindView(R.id.fav_btn)ImageButton mFavBtn;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            mPresenter = new CommentItemPresenter(this, new UserDataRepository(),
                    new CommentRepository(), new AuthRepository());

        }

        void bind(final Comment comment) {

            mPresenter.updateUi(comment);

            mFavBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFavBtn.isSelected()) {
                        mFavBtn.setSelected(false);
                        mPresenter.cancelLike(comment.getNoteId(), comment.getId());
                    } else {
                        mFavBtn.setSelected(true);
                        mPresenter.like(comment.getNoteId(), comment.getId());
                    }
                }
            });

        }

        @Override
        public void showText(String text) {
            mBodyTv.setText(text);
        }

        @Override
        public void showAuthor(String author) {
            mAuthorTv.setText(author);
        }

        @Override
        public void showPublishDate(String publishDate) {
            mPublishDateTv.setText(publishDate);
        }

        @Override
        public void showLikeCount(String count) {
            mLikesCount.setText(count);
        }

        @Override
        public void setFavBtnState(boolean isSelected) {
            mFavBtn.setSelected(isSelected);
        }
    }

    public void swapData(List<Comment> comments) {
        if (mComments == null) {
            mComments = comments;
            notifyDataSetChanged();
        } else {
            DiffUtil.DiffResult diffResult =
                    DiffUtil.calculateDiff(new CommentsDiffCallback(mComments, comments));
            mComments = comments;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    private class CommentsDiffCallback extends DiffUtil.Callback {

        private final List<Comment> mOldComments;
        private final List<Comment> mNewComments;

        CommentsDiffCallback(List<Comment> oldComments, List<Comment> newComments) {
            mOldComments = oldComments;
            mNewComments = newComments;
        }

        @Override
        public int getOldListSize() {
            return mOldComments == null ? 0 : mOldComments.size();
        }

        @Override
        public int getNewListSize() {
            return mNewComments == null ? 0 : mNewComments.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return TextUtils.equals(
                    mOldComments.get(oldItemPosition).getId(),
                    mNewComments.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return TextUtils.equals(
                    mOldComments.get(oldItemPosition).getText(),
                    mNewComments.get(newItemPosition).getText());
        }
    }

}
