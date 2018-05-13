package ie.cmfbi.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import ie.cmfbi.R;
import ie.cmfbi.adapters.PostListAdapter;
import ie.cmfbi.api.FBDBListener;
import ie.cmfbi.main.WitBookApp;

public class PostFragment extends Fragment implements AdapterView.OnItemClickListener, FBDBListener {

    protected static PostListAdapter listAdapter;
    protected ListView listView;
    public Query query;
    public WitBookApp app = WitBookApp.getInstance();
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    private String userKey;
    private String postKey;

    public View.OnClickListener likeListener;
    public View.OnClickListener commentListener;


    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment newInstance(Bundle userBundle) {
        PostFragment fragment = new PostFragment();
        fragment.setArguments(userBundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = null;
        v = inflater.inflate(R.layout.fragment_post, container, false);
        listView = (ListView) v.findViewById(R.id.postList);


        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.post_swipe_refresh_layout);

        likeListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postKey = view.getTag().toString();
                onPostLike(view.getTag().toString());
            }
        };

        commentListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //postCommentsFrame

                Bundle activityInfo = new Bundle();
                activityInfo.putString("postKey", view.getTag().toString());

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                CommentFragment postFragment = CommentFragment.newInstance(activityInfo);
                ft.replace(R.id.postCommentsFrame, postFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        };

        setSwipeRefreshLayout();

        return v;
    }

    public void onPostLike(final String postKey) {
        app.mFBDBManager.isFirstTimeLikingPost(postKey);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        app.mFBDBManager.attachListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSuccess(DataSnapshot dataSnapshot) {
        if(postKey != null && !postKey.isEmpty())
            app.mFBDBManager.likePost(postKey);
    }

    @Override
    public void onFailure() {
        Toast.makeText(getActivity(),"You already liked the post.",Toast.LENGTH_LONG).show();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle activityInfo = new Bundle();
        activityInfo.putString("postKey", (String) view.getTag());
    }

    protected void setSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query = app.mFBDBManager.getAllPosts();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        app.mFBDBManager.attachListener(this);

        ((TextView) getActivity().findViewById(R.id.recentAddedBarTextView)).setText("Recent Posts");

        if (getArguments() != null && getArguments().getString("userKey") != null) {
            userKey = getArguments().getString("userKey");
            query = app.mFBDBManager.getAllUserPosts(userKey);
        } else {
            query = app.mFBDBManager.getAllPosts();
        }

        updateUI(query);
    }

    public void updateUI(Query query) {

        listAdapter = new PostListAdapter(getActivity(), query, likeListener, commentListener);

        setListView(listView);
        listAdapter.notifyDataSetChanged();
    }

    public void setListView(ListView listview) {

        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listview.setAdapter(listAdapter);
        listview.setOnItemClickListener(this);
        listview.setEmptyView(getActivity().findViewById(R.id.empty_list_view));
    }

}
