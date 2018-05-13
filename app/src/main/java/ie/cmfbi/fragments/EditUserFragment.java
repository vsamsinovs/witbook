package ie.cmfbi.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import ie.cmfbi.R;
import ie.cmfbi.api.FBDBListener;
import ie.cmfbi.helpers.DocumentHelper;
import ie.cmfbi.main.WitBookApp;
import ie.cmfbi.models.User;

public class EditUserFragment extends Fragment implements View.OnClickListener, FBDBListener {

    public EditText userNameEditText;
    public WitBookApp app = WitBookApp.getInstance();
    private String userKey;
    private User user;
    private View view;


    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private ImageView imageView;
    private Bitmap imgBitmap;

    public EditUserFragment() {
    }

    public static EditUserFragment newInstance() {
        EditUserFragment fragment = new EditUserFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_user_edit, container, false);


        userNameEditText = (EditText) view.findViewById(R.id.userNameEditText);
        imageView = (ImageView) view.findViewById(R.id.userImg);

        Button saveButton = (Button) view.findViewById(R.id.saveUser);
        Button takeASnapButton = (Button) view.findViewById(R.id.takeASnap);


        takeASnapButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        return view;
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.saveUser:
                saveUser();
                break;
            case R.id.takeASnap:
                takeASnap();
                break;
        }
    }

    private void saveUser() {
        String nameText = userNameEditText.getText().toString();

        if ((nameText.length() > 0)) {

            user.userName = nameText;
            user.userNameLower = nameText.toLowerCase();
            if(imgBitmap != null) {
                // cannot return url from within success postback
                app.mFBDBManager.updateImageUser(user, imgBitmap);
            }
            else{
                app.mFBDBManager.updateUser(user, null);
            }

            Toast.makeText(getActivity(), "User Information Successfully Updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "User Name cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        app.mFBDBManager.attachListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        app.mFBDBManager.attachListener(this);
        app.mFBDBManager.getCurrentUser();

        ((TextView)getActivity().findViewById(R.id.recentAddedBarTextView)).setText("Edit account");

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSuccess(DataSnapshot dataSnapshot) {
        user = dataSnapshot.getValue(User.class);
        Log.v("witbook", "getUser() snapshot is " + user + " for user : " + app.mFBDBManager.mFBUserId);
        if (user != null)
            updateUI();
    }

    @Override
    public void onFailure() {
        Log.v("witbook", "Unable to Read/Update Data");
        Toast.makeText(getActivity(), "Unable to Read/Update Data", Toast.LENGTH_LONG).show();
    }

    public void updateUI() {

        userNameEditText.setText(user.userName);

        if(user.profileImgUrl != null && !user.profileImgUrl.isEmpty()){
            new DocumentHelper((ImageView) view.findViewById(R.id.userImg), null).execute(user.profileImgUrl);
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
            imgBitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageResource(0);
            imageView.setImageDrawable(null);
            imageView.setImageBitmap(null);
            imageView.invalidate();
            imageView.setImageBitmap(imgBitmap);
        }
    }
}
