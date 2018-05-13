package ie.cmfbi.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DocumentHelper extends AsyncTask<String, Void, Bitmap> {
    ImageView imageView;
    ProgressBar progressBar;

    public DocumentHelper(ImageView imageView, @Nullable ProgressBar progressBar) {
        this.imageView = imageView;
        if (progressBar != null)
            this.progressBar = progressBar;
    }


    protected Bitmap doInBackground(String... urls) {
        String urlOfImage = urls[0];
        Bitmap logo = null;
        try {
            InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
            logo = BitmapFactory.decodeStream(is);

            if (progressBar != null)
                progressBar.setVisibility(View.INVISIBLE);
        } catch (Exception e) { // Catch the download exception
            e.printStackTrace();
        }
        return logo;
    }

    protected void onPostExecute(Bitmap result) {
        imageView.setImageBitmap(result);
    }
}