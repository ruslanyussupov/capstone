package com.ruslaniusupov.achievity.notes;

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
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.adapters.NotesAdapter;
import com.ruslaniusupov.achievity.addeditnote.AddEditNoteActivity;
import com.ruslaniusupov.achievity.data.model.Note;
import com.ruslaniusupov.achievity.data.repository.AuthRepository;
import com.ruslaniusupov.achievity.data.repository.NoteRepository;
import com.ruslaniusupov.achievity.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotesActivity extends AppCompatActivity implements NotesAdapter.OnNoteClickListener {

    private static final String TAG = NotesActivity.class.getSimpleName();
    public static final String EXTRA_GOAL_ID = "com.ruslaniusupov.achievity.GOAL_ID";
    public static final String EXTRA_AUTHOR_ID = "com.ruslaniusupov.achievity.AUTHOR_ID";

    private final NotesAdapter mAdapter = new NotesAdapter(null, this);
    private final NoteRepository mNoteRepository = new NoteRepository();

    private NotesViewModel mViewModel;

    @BindView(R.id.notes_rv)RecyclerView mNotesRv;
    @BindView(R.id.add_note_fab)FloatingActionButton mAddNoteFab;
    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.no_connection)ImageView mNoConnection;
    @BindView(R.id.state_tv)TextView mStateTv;
    @BindView(R.id.coordinator)CoordinatorLayout mCoordinator;
    @BindView(R.id.progress_bar)ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!Utils.hasNetworkConnection(this)) {
            mNotesRv.setVisibility(View.GONE);
            mNoConnection.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            Snackbar.make(mCoordinator, getString(R.string.state_no_network_connection),
                    Snackbar.LENGTH_LONG).show();
        }

        String goalId = getIntent().getStringExtra(EXTRA_GOAL_ID);
        String authorId = getIntent().getStringExtra(EXTRA_AUTHOR_ID);

        getLifecycle().addObserver(mNoteRepository);

        mViewModel = ViewModelProviders.of(this).get(NotesViewModel.class);
        mViewModel.init(goalId, authorId, mNoteRepository, new AuthRepository());

        mNotesRv.setAdapter(mAdapter);
        mNotesRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    mAddNoteFab.hide();
                } else if (dy < 0) {
                    mAddNoteFab.show();            }
            }
        });

        if (TextUtils.equals(mViewModel.getAuthorId(), mViewModel.getCurrentUserId())) {
            mAddNoteFab.setVisibility(View.VISIBLE);
        } else {
            mAddNoteFab.setVisibility(View.GONE);
        }

        mViewModel.getNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {
                mProgressBar.setVisibility(View.GONE);
                if (notes == null || notes.size() == 0) {
                    mStateTv.setVisibility(View.VISIBLE);
                    mStateTv.setText(getString(R.string.state_no_notes));
                } else {
                    mStateTv.setVisibility(View.GONE);
                    mAdapter.swapData(notes);
                }
            }
        });
    }

    @OnClick(R.id.add_note_fab)
    public void addNote() {
        Intent openAddEditNoteActivity = new Intent(this, AddEditNoteActivity.class);
        openAddEditNoteActivity.putExtra(AddEditNoteActivity.EXTRA_GOAL_ID, mViewModel.getGoalId());
        startActivity(openAddEditNoteActivity);
    }

    @Override
    public void onClick(Note note) {
        Intent openNoteActivity = new Intent(this, NoteActivity.class);
        openNoteActivity.putExtra(NoteActivity.EXTRA_NOTE, note);
        startActivity(openNoteActivity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver(mNoteRepository);
    }
}
