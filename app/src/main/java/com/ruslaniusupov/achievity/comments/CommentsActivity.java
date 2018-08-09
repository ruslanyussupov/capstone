package com.ruslaniusupov.achievity.comments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.adapters.CommentsAdapter;
import com.ruslaniusupov.achievity.addeditcomment.AddEditCommentActivity;
import com.ruslaniusupov.achievity.data.model.Comment;
import com.ruslaniusupov.achievity.data.repository.CommentRepository;
import com.ruslaniusupov.achievity.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommentsActivity extends AppCompatActivity {

    private static final String TAG = CommentsActivity.class.getSimpleName();

    public static final String EXTRA_NOTE_ID = "com.ruslaniusupov.achievity.NOTE_ID";

    private final CommentsAdapter mAdapter = new CommentsAdapter(null);
    private final CommentRepository mCommentRepository = new CommentRepository();
    private CommentsViewModel mViewModel;

    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.comments_rv)RecyclerView mCommentsRv;
    @BindView(R.id.no_connection)ImageView mNoConnection;
    @BindView(R.id.state_tv)TextView mStateTv;
    @BindView(R.id.coordinator)CoordinatorLayout mCoordinator;
    @BindView(R.id.progress_bar)ProgressBar mProgressBar;
    @BindView(R.id.add_comment_fab)FloatingActionButton mAddComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!Utils.hasNetworkConnection(this)) {
            mCommentsRv.setVisibility(View.GONE);
            mNoConnection.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            Snackbar.make(mCoordinator, getString(R.string.state_no_network_connection),
                    Snackbar.LENGTH_LONG).show();
        }

        String noteId = getIntent().getStringExtra(EXTRA_NOTE_ID);

        getLifecycle().addObserver(mCommentRepository);

        mViewModel = ViewModelProviders.of(this).get(CommentsViewModel.class);
        mViewModel.init(noteId, mCommentRepository);

        mCommentsRv.setAdapter(mAdapter);
        mCommentsRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    mAddComment.hide();
                } else if (dy < 0) {
                    mAddComment.show();            }
            }
        });

        mViewModel.getComments().observe(this, new Observer<List<Comment>>() {
            @Override
            public void onChanged(@Nullable List<Comment> comments) {
                mProgressBar.setVisibility(View.GONE);
                if (comments == null || comments.size() == 0) {
                    mStateTv.setVisibility(View.VISIBLE);
                    mStateTv.setText(getString(R.string.state_no_comments));
                } else {
                    mStateTv.setVisibility(View.GONE);
                    mAdapter.swapData(comments);
                }
            }
        });

    }

    @OnClick(R.id.add_comment_fab)
    public void addComment() {
        Intent openAddEditComment = new Intent(this, AddEditCommentActivity.class);
        openAddEditComment.putExtra(AddEditCommentActivity.EXTRA_NOTE_ID, mViewModel.getNoteId());
        startActivity(openAddEditComment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver(mCommentRepository);
    }
}
