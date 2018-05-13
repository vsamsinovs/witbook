package ie.cmfbi.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import ie.cmfbi.R;
import ie.cmfbi.main.WitBookApp;
import ie.cmfbi.models.Post;

public class PostAddFragment extends Fragment implements View.OnClickListener {

    public EditText post;
    public WitBookApp app = WitBookApp.getInstance();


    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int REQUEST_VIDEO_CAPTURE = 1;

    private Bitmap imgBitmap;
    private ImageView imageView;
    private VideoView videoView;
    private Uri videoUri;

    public PostAddFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TextView)getActivity().findViewById(R.id.recentAddedBarTextView)).setText("Create a post");
    }

    public static PostAddFragment newInstance() {
        PostAddFragment fragment = new PostAddFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post_add, container, false);

        Button saveButton = (Button) v.findViewById(R.id.savePost);
        Button takeASnapButton = (Button) v.findViewById(R.id.takeASnap);
        Button takeAVidButton = (Button) v.findViewById(R.id.takeAVid);

        imageView = (ImageView) v.findViewById(R.id.imageView);
        videoView = (VideoView) v.findViewById(R.id.videoView);


        post = (EditText) v.findViewById(R.id.post);

        imageView.setVisibility(View.INVISIBLE);
        videoView.setVisibility(View.INVISIBLE);

        takeAVidButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        takeASnapButton.setOnClickListener(this);

        return v;
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.savePost:
                savePost();
                break;
            case R.id.takeASnap:
                takeASnap();
                break;
            case R.id.takeAVid:
                takeAVid();
                break;
        }
    }

    private void savePost() {
        String postText = post.getText().toString();

        if ((post.length() > 0)) {

            Post p = new Post(app.mFBDBManager.mFBUserId, postText);

            if (imgBitmap != null) {
                // cannot return url from within success postback
                app.mFBDBManager.addImagePost(p, imgBitmap);
            }
            else if(videoUri != null) {
                app.mFBDBManager.addVideoPost(p, videoUri);
            }
            else {
                app.mFBDBManager.addPost(p, null,null);
            }

            Toast.makeText(getActivity(), "Post saved successfully", Toast.LENGTH_SHORT).show();
            resetFields();
        } else {
            Toast.makeText(getActivity(), "Post cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void takeASnap() {
        if (app.checkSelfPermission("android.permission.CAMERA")
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{"android.permission.CAMERA"},
                    MY_CAMERA_PERMISSION_CODE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    private void takeAVid() {
        if (app.checkSelfPermission("android.permission.CAMERA")
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{"android.permission.CAMERA"},
                    MY_CAMERA_PERMISSION_CODE);
        } else {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(app, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(app, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.INVISIBLE);

            imgBitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(imgBitmap);
        }
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {

            imageView.setVisibility(View.INVISIBLE);
            videoView.setVisibility(View.VISIBLE);

            videoUri = data.getData();
            videoView.setVideoURI(videoUri);
        }
    }

    private void resetFields() {
        post.setText("");
    }
}
