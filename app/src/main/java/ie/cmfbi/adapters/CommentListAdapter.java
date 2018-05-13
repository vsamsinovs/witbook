package ie.cmfbi.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import ie.cmfbi.R;
import ie.cmfbi.api.FBDBListener;
import ie.cmfbi.api.FBDBManagerPromise;
import ie.cmfbi.main.WitBookApp;
import ie.cmfbi.models.Comment;
import ie.cmfbi.models.User;

public class CommentListAdapter extends FirebaseListAdapter<Comment> implements FBDBListener {
    public Query query;
    private View.OnClickListener saveListener;
    private TextView commentUser;
    private FBDBManagerPromise fBDBManagerPromise;
    public WitBookApp app = WitBookApp.getInstance();

    public CommentListAdapter(Activity context, Query query) {
        super(context, Comment.class, R.layout.adapter_comment, query);

        this.query = query;
        this.fBDBManagerPromise = new FBDBManagerPromise();
    }

    @Override
    protected void populateView(View row, Comment comment, int position) {
        row.setTag(getRef(position).getKey());

        this.fBDBManagerPromise.getUserPromise(comment.uid, this);
        commentUser = (TextView) row.findViewById(R.id.commentUserTxt);

        ((TextView) row.findViewById(R.id.commentTxt)).setText(comment.comment);
    }

    @Override
    public void onSuccess(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        commentUser.setText(user.userName.toString());
    }

    @Override
    public void onFailure() {
    }
}
