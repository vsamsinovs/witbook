package ie.cmfbi.main;

import android.app.Application;
import android.graphics.Bitmap;
import android.location.Location;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import ie.cmfbi.api.FBDBManager;

public class WitBookApp extends Application
{
    private RequestQueue mRequestQueue;
    private static WitBookApp mInstance;

    /* Client used to interact with Google APIs. */
    public GoogleApiClient      mGoogleApiClient;
    public GoogleSignInOptions  mGoogleSignInOptions;
    public Location             mCurrentLocation;
    public FirebaseAuth         mFirebaseAuth;
    public FirebaseUser         mFirebaseUser;

    public FBDBManager mFBDBManager;

    public boolean signedIn = false;
    public String googleToken;
    public String googleName;
    public String googleMail;
    public String googlePhotoURL;
    public Bitmap googlePhoto;

    public static final String TAG = WitBookApp.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mFBDBManager = new FBDBManager();
        mFBDBManager.open();
    }

    public static synchronized WitBookApp getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public <T> void add(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancel() {
        mRequestQueue.cancelAll(TAG);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}