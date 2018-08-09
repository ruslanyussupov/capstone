package com.ruslaniusupov.achievity.addeditnote;

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
import com.ruslaniusupov.achievity.data.repository.NoteRepository;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEditNoteActivity extends AppCompatActivity {

    private static final String TAG = AddEditNoteActivity.class.getSimpleName();
    public static final String EXTRA_GOAL_ID = "com.ruslaniusupov.achievity.GOAL_ID";

    private AddEditNoteViewModel mViewModel;

    @BindView(R.id.note_et)EditText mNoteEt;
    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.note_input_layout)TextInputLayout mNoteInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        String goalId = getIntent().getStringExtra(EXTRA_GOAL_ID);

        mViewModel = ViewModelProviders.of(this).get(AddEditNoteViewModel.class);
        mViewModel.init(goalId, new NoteRepository(), new AuthRepository());

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
                postNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void postNote() {
        String noteText = mNoteEt.getText().toString().trim();
        if (TextUtils.isEmpty(noteText)) {
            mNoteInputLayout.setError(getString(R.string.error_note_cant_be_empty));
        } else {
            mViewModel.addNote(noteText);
            finish();
        }
    }

    private void closeActivity() {
        if (mNoteEt.getText().length() > 0) {
            new DiscardDialog().show(getFragmentManager(), DiscardDialog.class.getSimpleName());
        } else {
            finish();
        }
    }

}
