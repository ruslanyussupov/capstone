package com.ruslaniusupov.achievity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.ruslaniusupov.achievity.auth.AuthActivity;
import com.ruslaniusupov.achievity.data.repository.AuthRepository;
import com.ruslaniusupov.achievity.data.repository.UserDataRepository;
import com.ruslaniusupov.achievity.discover.DiscoverFragment;
import com.ruslaniusupov.achievity.home.HomeFragment;
import com.ruslaniusupov.achievity.profile.ProfileFragment;
import com.ruslaniusupov.achievity.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_AUTH = 200;
    private static final String TAG_HOME = "HomeFragment";
    private static final String TAG_DISCOVER = "DiscoverFragment";
    private static final String TAG_PROFILE = "ProfileFragment";
    private static final String NAV_ITEM_ID = "com.ruslaniusupov.achievity.NAV_ITEM_ID";

    private FragmentManager mFragmentManager;
    private MainViewModel mViewModel;
    private final UserDataRepository mUserDataRepository = new UserDataRepository();
    private int mNavItemId = R.id.navigation_home;

    @BindView(R.id.coordinator)CoordinatorLayout mCoordinator;
    @BindView(R.id.navigation)BottomNavigationView mNavigation;
    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.no_connection)ImageView mNoConnectionIv;

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mNavItemId = R.id.navigation_home;
                    if (Utils.hasNetworkConnection(MainActivity.this)) {
                        mNoConnectionIv.setVisibility(View.GONE);
                        addFragment(TAG_HOME);
                    } else {
                        mNoConnectionIv.setVisibility(View.VISIBLE);
                        showNoConnectionSnackbar();
                    }
                    return true;
                case R.id.navigation_discover:
                    mNavItemId = R.id.navigation_discover;
                    if (Utils.hasNetworkConnection(MainActivity.this)) {
                        mNoConnectionIv.setVisibility(View.GONE);
                        addFragment(TAG_DISCOVER);
                    } else {
                        mNoConnectionIv.setVisibility(View.VISIBLE);
                        showNoConnectionSnackbar();
                    }
                    return true;
                case R.id.navigation_profile:
                    mNavItemId = R.id.navigation_profile;
                    if (Utils.hasNetworkConnection(MainActivity.this)) {
                        mNoConnectionIv.setVisibility(View.GONE);
                        addFragment(TAG_PROFILE);
                    } else {
                        mNoConnectionIv.setVisibility(View.VISIBLE);
                        showNoConnectionSnackbar();
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        if (savedInstanceState != null) {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }

        mNavigation.setVisibility(View.GONE);

        getLifecycle().addObserver(mUserDataRepository);

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.init(new AuthRepository(), mUserDataRepository);

        if (!mViewModel.isAuthorized()) {
            startActivityForResult(new Intent(this, AuthActivity.class), RC_AUTH);
        } else {
            initBottomNav();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, mNavItemId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_AUTH) {
            if (resultCode == RESULT_OK) {
                mViewModel.initUserData();
                initBottomNav();
            }
        }

    }

    private void initBottomNav() {
        mNavigation.setVisibility(View.VISIBLE);
        mFragmentManager = getSupportFragmentManager();
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mNavigation.setSelectedItemId(mNavItemId);
    }

    private void showNoConnectionSnackbar() {
        Snackbar.make(mCoordinator, getString(R.string.state_no_network_connection), Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver(mUserDataRepository);
    }

    private void addFragment(String tag) {
        Fragment fragment = null;
        if (TextUtils.equals(tag, TAG_HOME)) {
            fragment = mFragmentManager.findFragmentByTag(TAG_HOME);
            if (fragment == null) {
                fragment = new HomeFragment();
            }
        } else if (TextUtils.equals(tag, TAG_DISCOVER)) {
            fragment = mFragmentManager.findFragmentByTag(TAG_DISCOVER);
            if (fragment == null) {
                fragment = new DiscoverFragment();
            }
        } else if (TextUtils.equals(tag, TAG_PROFILE)) {
            fragment = mFragmentManager.findFragmentByTag(TAG_PROFILE);
            if (fragment == null) {
                fragment = ProfileFragment.newInstance(mViewModel.getCurrentUserId());
            }
        }
        mFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, tag)
                .commit();
    }

}
