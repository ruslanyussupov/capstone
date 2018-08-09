package com.ruslaniusupov.achievity.addeditcomment;

import android.arch.lifecycle.ViewModelProviders;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.ruslaniusupov.achievity.DiscardDialog;
import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.data.repository.AuthRepository;
import com.ruslaniusupov.achievity.data.repository.CommentRepository;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEditCommentActivity extends AppCompatActivity {

    private static final String TAG = AddEditCommentActivity.class.getSimpleName();

    public static final String EXTRA_NOTE_ID = "com.ruslaniusupov.achievity.NOTE_ID";

    private AddEditCommentViewModel mViewModel;

    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.comment_et)EditText mCommentEt;
    @BindView(R.id.comment_input_layout)TextInputLayout mCommentInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_comment);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String noteId = getIntent().getStringExtra(EXTRA_NOTE_ID);

        mViewModel = ViewModelProviders.of(this).get(AddEditCommentViewModel.class);
        mViewModel.init(noteId, new CommentRepository(), new AuthRepository());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                closeActivity();
                return true;
            case R.id.action_done:
                postComment();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void postComment() {
        String commentText = mCommentEt.getText().toString().trim();
        if (TextUtils.isEmpty(commentText)) {
            mCommentInputLayout.setError(getString(R.string.error_comment_cant_be_empty));
        } else {
            mViewModel.addComment(mViewModel.getNoteId(), commentText);
            finish();
        }
    }

    private void closeActivity() {
        if (mCommentEt.getText().length() > 0) {
            new DiscardDialog().show(getFragmentManager(), DiscardDialog.class.getSimpleName());
        } else {
            finish();
        }
    }

}
