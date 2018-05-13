package ie.cmfbi.adapters;

import android.widget.Filter;

import com.google.firebase.database.Query;

import ie.cmfbi.fragments.UserFragment;

public class UserFilter extends Filter {
    private String filterText;
    private UserListAdapter adapter;
    private Query query;
    private UserFragment fragment;

    public UserFilter(Query originalQuery, String filterText
            , UserListAdapter adapter, UserFragment fragment) {
        super();
        this.query = originalQuery;
        this.filterText = filterText;
        this.adapter = adapter;
        this.fragment = fragment;
    }

    public void setFilter(String filterText) {
        this.filterText = filterText;
    }

    @Override
    protected Filter.FilterResults performFiltering(CharSequence prefix) {
        Filter.FilterResults results = new Filter.FilterResults();

        //TODO
        if (filterText.equals("friends")) {
            query = fragment.app.mFBDBManager.getAllFriends(prefix.toString());
        }
        else{
            query = fragment.app.mFBDBManager.getAllUsers(prefix.toString());
        }

        results.values = query;

        return results;
    }

    @Override
    protected void publishResults(CharSequence prefix, Filter.FilterResults results) {

        fragment.updateUI((Query) results.values);
    }
}
