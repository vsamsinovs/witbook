package ie.cmfbi.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import ie.cmfbi.R;
import ie.cmfbi.api.GoogleApi;
import ie.cmfbi.fragments.EditUserFragment;
import ie.cmfbi.fragments.MapsFragment;
import ie.cmfbi.fragments.PostAddFragment;
import ie.cmfbi.fragments.PostFragment;
import ie.cmfbi.fragments.UserSearchFragment;
import ie.cmfbi.main.WitBookApp;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener {

    public WitBookApp app = WitBookApp.getInstance();
    private ImageView googlePhoto;
    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //SetUp GooglePhoto and Email for Drawer here
        googlePhoto = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.googlephoto);
        GoogleApi.getGooglePhoto(app.googlePhotoURL, googlePhoto);

        TextView googleName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.googlename);
        googleName.setText(app.googleName);

        TextView googleMail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.googlemail);
        googleMail.setText(app.googleMail);

        dialog = new ProgressDialog(this);
        GoogleApi.attachDialog(dialog);

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        PostFragment postFragment = PostFragment.newInstance(savedInstanceState);
        ft.replace(R.id.homeFrame, postFragment);

        ft.addToBackStack(null);

        ft.commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        // http://stackoverflow.com/questions/32944798/switch-between-fragments-with-onnavigationitemselected-in-new-navigation-drawer

        int id = item.getItemId();
        Fragment fragment;
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (id == R.id.nav_home) {
            Bundle activityInfo = new Bundle();
            fragment = PostFragment.newInstance(activityInfo);
            ft.replace(R.id.homeFrame, fragment);

            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.nav_account) {
            fragment = EditUserFragment.newInstance();
            ft.replace(R.id.homeFrame, fragment);

            ft.addToBackStack(null);
            ft.commit();

        } else if (id == R.id.nav_add_post) {
            fragment = PostAddFragment.newInstance();
            ft.replace(R.id.homeFrame, fragment);

            ft.addToBackStack(null);
            ft.commit();

        } else if (id == R.id.nav_user_search) {
            fragment = UserSearchFragment.newInstance();
            ft.replace(R.id.homeFrame, fragment);

            ft.addToBackStack(null);
            ft.commit();

        } else if (id == R.id.nav_map) {
            fragment = MapsFragment.newInstance();
            ft.replace(R.id.homeFrame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void openInfoDialog(Activity current) {
        Dialog dialog = new Dialog(current);
        dialog.setTitle("About WitBook");
        dialog.setContentView(R.layout.info);

        TextView currentVersion = (TextView) dialog
                .findViewById(R.id.versionTextView);
        currentVersion.setText("1.0.0");

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    public void menuInfo(MenuItem m) {
        openInfoDialog(this);
    }


    public void menuHome(MenuItem m) {
        startActivity(new Intent(this, Home.class));
    }

    // [START signOut]

    public void menuSignOut(MenuItem m) {

        //https://stackoverflow.com/questions/38039320/googleapiclient-is-not-connected-yet-on-logout-when-using-firebase-auth-with-g
        app.mGoogleApiClient.connect();
        app.mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                FirebaseAuth.getInstance().signOut();
                app.mFirebaseUser = null;
                Log.v("witbook", "User Logged out of Firebase");
                if (app.mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(app.mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.v("witbook", "User Logged out ");
                                Intent intent = new Intent(Home.this, Login.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("witbook", "Google API Client Connection Suspended");
            }
        });
    }
    // [END signOut]
}
