package ie.cmfbi.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.Query;

import ie.cmfbi.R;
import ie.cmfbi.helpers.DocumentHelper;
import ie.cmfbi.main.WitBookApp;
import ie.cmfbi.models.User;

public class UserListAdapter  extends FirebaseListAdapter<User> {
    public Query query;

    public WitBookApp app = WitBookApp.getInstance();

    public UserListAdapter(Activity context, Query query) {
        super(context, User.class, R.layout.adapter_home_user, query);

        this.query = query;
    }

    @Override
    protected void populateView(View row, User user, int position) {
        row.setTag(getRef(position).getKey());

        ((TextView) row.findViewById(R.id.rowUserName)).setText(user.userName);

        if (user.profileImgUrl != null && !user.profileImgUrl.isEmpty()) {
            new DocumentHelper((ImageView) row.findViewById(R.id.rowUserImg), (ProgressBar) row.findViewById((R.id.userSearchProfileImg))).execute(user.profileImgUrl);
        }

    }
}