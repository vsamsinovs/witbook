package ie.cmfbi.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import ie.cmfbi.R;
import ie.cmfbi.adapters.UserFilter;
import ie.cmfbi.adapters.UserListAdapter;
import ie.cmfbi.api.FBDBListener;
import ie.cmfbi.api.FBDBManagerPromise;
import ie.cmfbi.helpers.DocumentHelper;
import ie.cmfbi.main.WitBookApp;
import ie.cmfbi.models.User;
import ie.cmfbi.promises.ToggleAddFriendButton;

public class UserDetailsFragment extends Fragment implements View.OnClickListener, FBDBListener {

    protected ListView listView;
    public Query query;
    protected static UserListAdapter listAdapter;
    protected UserFilter userFilter;
    private View view;
    private User user;
    private String userKey;
    private Button addFriendButton;
    private Button removeFriendButton;
    private ProgressBar friendProgressBar;
    private TextView  selfFriend;

    private FBDBManagerPromise fBDBManagerPromise;

    public WitBookApp app = WitBookApp.getInstance();

    public UserDetailsFragment() {
        this.fBDBManagerPromise = new FBDBManagerPromise();
    }

    public static UserDetailsFragment newInstance(Bundle userBundle) {
        UserDetailsFragment fragment = new UserDetailsFragment();
        fragment.setArguments(userBundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle activityInfo = new Bundle();


        activityInfo.putString("userKey", getArguments().getString("userKey"));

        view = inflater.inflate(R.layout.fragment_user_details, container, false);

        selfFriend = (TextView) view.findViewById(R.id.selfFriendTextView);

        addFriendButton = (Button) view.findViewById(R.id.friendUser);
        removeFriendButton = (Button) view.findViewById(R.id.unfriendUser);
        friendProgressBar = (ProgressBar) view.findViewById(R.id.friendProgressBar);

        addFriendButton.setOnClickListener(this);
        removeFriendButton.setOnClickListener(this);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        PostFragment postFragment = PostFragment.newInstance(activityInfo);
        ft.replace(R.id.userDetailsFrame, postFragment);
        ft.addToBackStack(null);
        ft.commit();

        //userDetailsFrame

        return view;
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.friendUser:
                addUserAsAFriend();
                break;
            case R.id.unfriendUser:
                removeFriend();
                break;
        }
    }

    public void removeFriend(){
        this.fBDBManagerPromise.removeFriend(getArguments().getString("userKey"), new ToggleAddFriendButton(removeFriendButton, addFriendButton,friendProgressBar));
        Toast.makeText(getActivity(), "Friend removed", Toast.LENGTH_LONG).show();
    }

    public void addUserAsAFriend(){
        this.fBDBManagerPromise.addFriend(user);
        this.fBDBManagerPromise.checkFriend(getArguments().getString("userKey"), new ToggleAddFriendButton(addFriendButton,removeFriendButton,friendProgressBar));
        Toast.makeText(getActivity(), "User Added as a friend", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getArguments() != null) {
            this.fBDBManagerPromise.getUserPromise(getArguments().getString("userKey"), this);

            if(!getArguments().getString("userKey").equals(app.mFirebaseUser.getUid())) {
                this.fBDBManagerPromise.checkFriend(getArguments().getString("userKey"), new ToggleAddFriendButton(addFriendButton, removeFriendButton, friendProgressBar));
            }
            else{
                selfFriend.setVisibility(View.VISIBLE);
                friendProgressBar.setVisibility(View.INVISIBLE);
            }
        }

        ((TextView)getActivity().findViewById(R.id.recentAddedBarTextView)).setText("User Details");
        ((ProgressBar)view.findViewById(R.id.userProfileImgProgressBar)).setVisibility(View.VISIBLE);
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

        ((TextView)view.findViewById(R.id.userNameLbl)).setText(user.userName);

        if(user.profileImgUrl != null && !user.profileImgUrl.isEmpty()){
            new DocumentHelper((ImageView) view.findViewById(R.id.userImg),(ProgressBar)view.findViewById(R.id.userProfileImgProgressBar)).execute(user.profileImgUrl);
        }

    }

}
