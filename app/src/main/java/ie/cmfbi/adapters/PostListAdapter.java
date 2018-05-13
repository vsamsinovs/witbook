package ie.cmfbi.adapters;

import android.app.Activity;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import ie.cmfbi.R;
import ie.cmfbi.api.FBDBListener;
import ie.cmfbi.api.FBDBManagerPromise;
import ie.cmfbi.helpers.DocumentHelper;
import ie.cmfbi.main.WitBookApp;
import ie.cmfbi.models.Post;
import ie.cmfbi.models.User;

public class PostListAdapter extends FirebaseListAdapter<Post> implements FBDBListener {
    public Query query;
    private View.OnClickListener likeListener;
    private View.OnClickListener commentListener;
    private FBDBManagerPromise fBDBManagerPromise;
    private TextView rowUser;
    private User user;
    private ImageView userProfileImg;
    private ProgressBar userProfileImgProgressBar;

    private FBDBListener _promise;

    public WitBookApp app = WitBookApp.getInstance();

    public PostListAdapter(Activity context, Query query, View.OnClickListener likeListener, View.OnClickListener commentListener) {
        super(context, Post.class, R.layout.adapter_post, query);

        this.query = query;
        this.likeListener = likeListener;
        this.commentListener = commentListener;

        this.fBDBManagerPromise = new FBDBManagerPromise();
    }

    @Override
    protected void populateView(View row, Post post, int position) {
        row.setTag(getRef(position).getKey());

        this.fBDBManagerPromise.getUserPromise(post.uid, this);
        rowUser = (TextView) row.findViewById(R.id.rowUser);
        userProfileImg = ((ImageView)row.findViewById(R.id.userProfileImg));
        userProfileImgProgressBar = ((ProgressBar)row.findViewById(R.id.userProfileImgProgressBar));

        //this.fBDBManagerPromise.getUserPromise(post.uid, new UpdateUserNameFieldPromise((TextView)row.findViewById(R.id.rowUser)));

        ((TextView) row.findViewById(R.id.rowPost)).setText(post.post);
        ((TextView) row.findViewById(R.id.rowNumLikes)).setText(post.numLikes.toString());
        ((TextView) row.findViewById(R.id.rowNumComments)).setText(post.numComments.toString());

        ImageView imgLike = (ImageView) row.findViewById(R.id.rowLikeImg);
        imgLike.setTag(getRef(position).getKey());
        imgLike.setOnClickListener(likeListener);

        ImageView imgComment = (ImageView) row.findViewById(R.id.rowCommentImg);
        imgComment.setTag(getRef(position).getKey());
        imgComment.setOnClickListener(commentListener);

        ((VideoView)row.findViewById(R.id.videoView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((VideoView)view).isPlaying())
                    ((VideoView)view).pause();
                else
                    ((VideoView)view).start();
            }
        });

        if (post.imgUrl != null && !post.imgUrl.isEmpty()) {
            ((ConstraintLayout)row.findViewById(R.id.mediaContainer)).setVisibility(View.VISIBLE);
            ((ImageView)row.findViewById(R.id.postImg)).setVisibility(View.VISIBLE);
            ((VideoView)row.findViewById(R.id.videoView)).setVisibility(View.INVISIBLE);

            ((VideoView)row.findViewById(R.id.videoView)).getLayoutParams().height = 0;
            new DocumentHelper((ImageView) row.findViewById(R.id.postImg),null).execute(post.imgUrl);
        }

        else if (post.videoUrl != null && !post.videoUrl.isEmpty()) {
            ((ConstraintLayout)row.findViewById(R.id.mediaContainer)).setVisibility(View.VISIBLE);
            ((ImageView)row.findViewById(R.id.postImg)).setVisibility(View.INVISIBLE);
            ((VideoView)row.findViewById(R.id.videoView)).setVisibility(View.VISIBLE);

            VideoView video = (VideoView) row.findViewById(R.id.videoView);
            video.setVideoURI(Uri.parse(post.videoUrl));


            //TODO
            //video.start();
        }
        else{
            ((ConstraintLayout)row.findViewById(R.id.mediaContainer)).setVisibility(View.INVISIBLE);
            ((ConstraintLayout)row.findViewById(R.id.mediaContainer)).getLayoutParams().height = 0;

            ((ProgressBar)row.findViewById(R.id.mediaProgressBar)).getLayoutParams().height = 0;
            ((ImageView)row.findViewById(R.id.postImg)).getLayoutParams().height = 0;
            ((VideoView)row.findViewById(R.id.videoView)).getLayoutParams().height = 0;
        }

    }

    @Override
    public void onSuccess(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        rowUser.setText(user.userName.toString());

        new DocumentHelper(userProfileImg, null).execute(user.profileImgUrl);
    }

    @Override
    public void onFailure() {

    }

}
