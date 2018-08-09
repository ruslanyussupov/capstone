package com.ruslaniusupov.achievity.profile;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
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
import com.ruslaniusupov.achievity.data.model.User;
import com.ruslaniusupov.achievity.data.repository.UserDataRepository;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = EditProfileActivity.class.getSimpleName();
    public static final String EXTRA_USER_ID = "com.ruslaniusupov.achievity.USER_ID";

    private EditProfileViewModel mModelView;
    private final UserDataRepository mUserDataRepository = new UserDataRepository();

    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.name_et)EditText mNameEt;
    @BindView(R.id.bio_et)EditText mBioEt;
    @BindView(R.id.name_input_layout)TextInputLayout mNameInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        mModelView = ViewModelProviders.of(this).get(EditProfileViewModel.class);

        final String userId = getIntent().getStringExtra(EXTRA_USER_ID);

        getLifecycle().addObserver(mUserDataRepository);

        mModelView.init(userId, mUserDataRepository);

        mModelView.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user == null) {
                    return;
                }
                if (!TextUtils.isEmpty(user.getName())) {
                    mNameEt.setText(user.getName());
                }
                if (!TextUtils.isEmpty(user.getBio())) {
                    mBioEt.setText(user.getBio());
                }
            }
        });

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
            case R.id.action_done:
                updateProfile();
                return true;
            case android.R.id.home:
                closeActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void closeActivity() {

        if (getChangedName() != null || getChangedBio() != null) {
            new DiscardDialog().show(getFragmentManager(), TAG);
        } else {
            finish();
        }

    }

    private void updateProfile() {
        if (getChangedName() != null) {
            if (getChangedName().length() < 1) {
                mNameInputLayout.setError(getString(R.string.error_name_cant_be_empty));
                return;
            }
            mModelView.updateName(getChangedName());
        }
        if (getChangedBio() != null) {
            mModelView.updateBio(getChangedBio());
        }
        finish();
    }

    private String getChangedName() {
        String name = mNameEt.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.equals(mModelView.getOldName(), name)) {
            return null;
        } else {
            return name;
        }
    }

    private String getChangedBio() {
        String bio = mBioEt.getText().toString().trim();
        if (TextUtils.isEmpty(bio) || TextUtils.equals(mModelView.getOldBio(), bio)) {
            return null;
        } else {
            return bio;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver(mUserDataRepository);
    }
}
