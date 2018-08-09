package com.ruslaniusupov.achievity.auth;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.data.AuthDataSource;
import com.ruslaniusupov.achievity.data.repository.AuthRepository;
import com.ruslaniusupov.achievity.utils.Utils;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = AuthActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 100;

    private final AuthDataSource mAuthDataSource = new AuthRepository();

    @BindView(R.id.coordinator)CoordinatorLayout mCoordinator;
    @BindView(R.id.no_connection)ImageView mNoConnectionIv;
    @BindView(R.id.state_tv)TextView mStateTv;
    @BindView(R.id.toolbar)Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        initAuth();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                    mStateTv.setVisibility(View.GONE);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    mStateTv.setVisibility(View.VISIBLE);
                    mStateTv.setText(getString(R.string.state_auth_failed));
                    Log.e(TAG, "Auth failed.", response.getError());
                }
        }
    }

    private void initAuth() {
        if (Utils.hasNetworkConnection(this)) {
            mNoConnectionIv.setVisibility(View.GONE);
            if (mAuthDataSource.isAuthorized()) {
                setResult(RESULT_OK);
                finish();
            } else {
                // Choose authentication providers
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build());

                // Create and return sign-in intent
                Intent authIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build();
                startActivityForResult(authIntent, RC_SIGN_IN);
            }
        } else {
            mNoConnectionIv.setVisibility(View.VISIBLE);
            Snackbar.make(mCoordinator, getString(R.string.state_no_network_connection), Snackbar.LENGTH_LONG)
                    .show();
        }

    }

    private void refresh() {
        initAuth();
    }

}
