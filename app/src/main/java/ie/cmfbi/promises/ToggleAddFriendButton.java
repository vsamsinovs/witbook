package ie.cmfbi.promises;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import ie.cmfbi.api.FBDBListener;
import ie.cmfbi.models.User;


public class ToggleAddFriendButton implements FBDBListener {

    private Button addFriendButton;
    private Button removeFriendButton;
    private ProgressBar loading;

    public ToggleAddFriendButton(Button addFriendButton, Button removeFriendButton, ProgressBar loading){
        this.addFriendButton = addFriendButton;
        this.removeFriendButton = removeFriendButton;
        this.loading = loading;

        addFriendButton.setVisibility(View.INVISIBLE);
        removeFriendButton.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess(DataSnapshot dataSnapshot) {
        addFriendButton.setVisibility(View.INVISIBLE);
        removeFriendButton.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFailure() {
        addFriendButton.setVisibility(View.VISIBLE);
        removeFriendButton.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.INVISIBLE);
    }
}