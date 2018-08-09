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
import com.ruslaniusupov.achievity.data.model.Note;
import com.ruslaniusupov.achievity.data.repository.AuthRepository;
import com.ruslaniusupov.achievity.data.repository.NoteRepository;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private List<Note> mNotes;
    private final OnNoteClickListener mListener;

    public NotesAdapter(List<Note> notes, OnNoteClickListener listener) {
        mNotes = notes;
        mListener = listener;
    }

    public interface OnNoteClickListener {
        void onClick(Note note);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mNotes.get(position));
    }

    @Override
    public int getItemCount() {
        return mNotes == null ? 0 : mNotes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements NoteItemContract.View {

        private final NoteItemContract.Presenter mPresenter;

        @BindView(R.id.author)TextView mAuthorTv;
        @BindView(R.id.publish_date)TextView mPubDateTv;
        @BindView(R.id.body)TextView mBodyTv;
        @BindView(R.id.fav_btn)ImageButton mFavBtn;
        @BindView(R.id.likes_counter)TextView mLikesCounterTv;
        @BindView(R.id.comments_counter)TextView mCommentsCounter;

        ViewHolder(View itemView) {
            super(itemView);
            mPresenter = new NoteItemPresenter(this, new AuthRepository(), new NoteRepository());
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(mNotes.get(getAdapterPosition()));
                }
            });
        }

        void bind(final Note note) {

            mPresenter.updateUi(note);

            mFavBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFavBtn.isSelected()) {
                        mFavBtn.setSelected(false);
                        mPresenter.cancelLike(note.getId());
                    } else {
                        mFavBtn.setSelected(true);
                        mPresenter.like(note.getId());
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
            mLikesCounterTv.setText(count);
        }

        @Override
        public void showCommentsCount(String count) {
            mCommentsCounter.setText(count);
        }

        @Override
        public void setLikeBtnState(boolean isSelected) {
            mFavBtn.setSelected(isSelected);
        }
    }

    public void swapData(List<Note> notes) {
        if (mNotes == null) {
            mNotes = notes;
            notifyDataSetChanged();
        } else {
            DiffUtil.DiffResult diffResult =
                    DiffUtil.calculateDiff(new NotesDiffUtilCallback(mNotes, notes));
            mNotes = notes;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    private class NotesDiffUtilCallback extends DiffUtil.Callback {

        private final List<Note> mOldNotes;
        private final List<Note> mNewNotes;

        NotesDiffUtilCallback(List<Note> oldNotes, List<Note> newNotes) {
            mOldNotes = oldNotes;
            mNewNotes = newNotes;
        }

        @Override
        public int getOldListSize() {
            return mOldNotes == null ? 0 : mOldNotes.size();
        }

        @Override
        public int getNewListSize() {
            return mNewNotes == null ? 0 : mNewNotes.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return TextUtils.equals(
                    mOldNotes.get(oldItemPosition).getId(),
                    mNewNotes.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return TextUtils.equals(
                    mOldNotes.get(oldItemPosition).getText(),
                    mNewNotes.get(newItemPosition).getText());
        }
    }

}
