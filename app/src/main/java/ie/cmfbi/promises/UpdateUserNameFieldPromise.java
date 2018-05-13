package ie.cmfbi.promises;

import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import ie.cmfbi.api.FBDBListener;
import ie.cmfbi.models.User;

public class UpdateUserNameFieldPromise implements FBDBListener {

    private TextView userNameTextView;

    public UpdateUserNameFieldPromise(TextView userNameTextView){
        this.userNameTextView = userNameTextView;
    }

    @Override
    public void onSuccess(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        userNameTextView.setText(user.userName.toString());
    }

    @Override
    public void onFailure() {
        // TODO
    }
}