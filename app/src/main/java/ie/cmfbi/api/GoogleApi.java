package ie.cmfbi.api;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import ie.cmfbi.main.WitBookApp;

public class GoogleApi {

    public static WitBookApp app = WitBookApp.getInstance();
    public static ProgressDialog dialog;


    public static void attachDialog(ProgressDialog mDialog) {
        dialog = mDialog;
    }

    public static void getGooglePhoto(String url, final ImageView googlePhoto) {

        ImageRequest imgRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        app.googlePhoto = response;
                        googlePhoto.setImageBitmap(app.googlePhoto);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Something went wrong!");
                error.printStackTrace();
            }
        });

        app.add(imgRequest);
    }
}