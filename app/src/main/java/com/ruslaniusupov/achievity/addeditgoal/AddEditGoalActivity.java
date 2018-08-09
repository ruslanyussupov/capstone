package com.ruslaniusupov.achievity.addeditgoal;

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
import com.ruslaniusupov.achievity.data.repository.GoalRepository;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEditGoalActivity extends AppCompatActivity {

    private static final String TAG = AddEditGoalActivity.class.getSimpleName();

    public static final String EXTRA_USER_ID = "com.ruslaniusupov.achievity.USER_ID";

    private AddEditGoalViewModel mViewModel;

    @BindView(R.id.goal_et)EditText mGoalEt;
    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.goal_input_layout)TextInputLayout mGoalInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_goal);

        ButterKnife.bind(this);

        String userId = getIntent().getStringExtra(EXTRA_USER_ID);

        mViewModel = ViewModelProviders.of(this).get(AddEditGoalViewModel.class);
        mViewModel.init(userId, new GoalRepository());

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);



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
                postGoal();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void postGoal() {
        String goalText = mGoalEt.getText().toString().trim();
        if (TextUtils.isEmpty(goalText)) {
            mGoalInputLayout.setError(getString(R.string.error_goal_cant_be_empty));
        } else {
            mViewModel.addGoal(goalText);
            finish();
        }
    }

    private void closeActivity() {
        if (mGoalEt.getText().length() > 0) {
            new DiscardDialog().show(getFragmentManager(), DiscardDialog.class.getSimpleName());
        } else {
            finish();
        }
    }

}
