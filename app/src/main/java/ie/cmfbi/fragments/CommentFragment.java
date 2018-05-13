package ie.cmfbi.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.Query;

import ie.cmfbi.R;
import ie.cmfbi.adapters.CommentListAdapter;
import ie.cmfbi.main.WitBookApp;

public class CommentFragment extends Fragment implements View.OnClickListener {

    protected static CommentListAdapter listAdapter;
    protected ListView listView;
    public Query query;
    public WitBookApp app = WitBookApp.getInstance();
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    private String postKey;
    private EditText commentTxt;

    public View.OnClickListener saveListener;

    public CommentFragment() {
        // Required empty public constructor
    }

    public static CommentFragment newInstance(Bundle userBundle) {
        CommentFragment fragment = new CommentFragment();
        fragment.setArguments(userBundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = null;
        v = inflater.inflate(R.layout.fragment_comments, container, false);

        listView = (ListView) v.findViewById(R.id.commentList);
        commentTxt = (EditText)v.findViewById(R.id.commentEditText);

        Button btn = (Button) v.findViewById(R.id.saveComment);
        //btn.setTag(getRef(position).getKey());
        btn.setOnClickListener(this);



/*
        saveListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(commentTxt.getText() != null && !commentTxt.getText().toString().isEmpty()){
                    app.mFBDBManager.addComment(postKey, commentTxt.getText().toString());
                    Toast.makeText(getActivity(), "Comment saved successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), "Comment input field cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        };

  */      return v;
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.saveComment:
                saveComment();
                break;
            case R.id.takeASnap:
                //takeASnap();
                break;
            case R.id.takeAVid:
                //takeAVid();
                break;
        }
    }

    public void saveComment(){
        if(commentTxt.getText() != null && !commentTxt.getText().toString().isEmpty()){
            app.mFBDBManager.addComment(postKey, commentTxt.getText().toString());
            commentTxt.setText("");
            Toast.makeText(getActivity(), "Comment saved successfully", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getActivity(), "Comment input field cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }
/*
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle activityInfo = new Bundle();
        activityInfo.putString("postKey", (String) view.getTag());
    }
*/
    @Override
    public void onResume() {
        super.onResume();

        if (getArguments() != null) {
            postKey = getArguments().getString("postKey");
            query = app.mFBDBManager.getComments(postKey);
        }
        updateUI(query);
    }


    public void updateUI(Query query) {
        listAdapter = new CommentListAdapter(getActivity(), query);
        setListView(listView);
        listAdapter.notifyDataSetChanged();
    }

    public void setListView(ListView listview) {
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listview.setAdapter(listAdapter);
        //listview.setOnItemClickListener(this);
        listview.setEmptyView(getActivity().findViewById(R.id.empty_list_view));
    }

}
