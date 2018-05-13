package ie.cmfbi.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.Query;

import ie.cmfbi.R;
import ie.cmfbi.adapters.UserFilter;
import ie.cmfbi.adapters.UserListAdapter;
import ie.cmfbi.main.WitBookApp;

public class UserFragment extends Fragment implements AdapterView.OnItemClickListener {

    protected ListView listView;
    public Query query;
    protected static UserListAdapter listAdapter;
    protected UserFilter userFilter;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private ImageView imageView;
    private Bitmap imgBitmap;

    public WitBookApp app = WitBookApp.getInstance();

    public UserFragment() {
    }

    public static UserFragment newInstance() {
        UserFragment userFragment = new UserFragment();
        return userFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = null;
        v = inflater.inflate(R.layout.fragment_home, container, false);
        listView = (ListView) v.findViewById(R.id.homeList);
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle activityInfo = new Bundle();
        activityInfo.putString("userKey", (String) view.getTag());

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = UserDetailsFragment.newInstance(activityInfo);
        ft.replace(R.id.homeFrame, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        query = app.mFBDBManager.getAllUsers();
        updateUI(query);

        ((TextView)getActivity().findViewById(R.id.recentAddedBarTextView)).setText("Users");
    }

    public void updateUI(Query query) {

        listAdapter = new UserListAdapter(getActivity(), query);

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
