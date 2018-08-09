package com.ruslaniusupov.achievity.notes;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.comments.CommentsActivity;
import com.ruslaniusupov.achievity.data.model.Note;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoteActivity extends AppCompatActivity {

    private static final String TAG = NoteActivity.class.getSimpleName();

    public static final String EXTRA_NOTE = "com.ruslaniusupov.achievity.NOTE";

    private NoteViewModel mViewModel;

    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.note_tv)TextView mNoteTv;
    @BindView(R.id.author_tv)TextView mAuthorTv;
    @BindView(R.id.publish_date_tv)TextView mPublishDateTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Note note = getIntent().getParcelableExtra(EXTRA_NOTE);

        mViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        mViewModel.init(note);

        mNoteTv.setText(mViewModel.getText());
        mPublishDateTv.setText(mViewModel.getPublishDate());
        mAuthorTv.setText(mViewModel.getAuthor());
    }

    @OnClick(R.id.show_comments_btn)
    public void showComments() {
        Intent comments = new Intent(this, CommentsActivity.class);
        comments.putExtra(CommentsActivity.EXTRA_NOTE_ID, mViewModel.getNoteId());
        startActivity(comments);
    }

}
