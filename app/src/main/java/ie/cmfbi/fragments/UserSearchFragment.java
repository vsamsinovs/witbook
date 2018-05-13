package ie.cmfbi.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import ie.cmfbi.R;
import ie.cmfbi.adapters.UserFilter;

public class UserSearchFragment extends UserFragment
        implements AdapterView.OnItemSelectedListener, TextWatcher {

    public String selected;

    public UserSearchFragment() {
        // Required empty public constructor
    }

    public static UserSearchFragment newInstance() {
        UserSearchFragment fragment = new UserSearchFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context c) {
        super.onAttach(c);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater,container,savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_user_search, container, false);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
                .createFromResource(getActivity(), R.array.friendTypes,
                        android.R.layout.simple_spinner_item);

        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = ((Spinner) v.findViewById(R.id.searchUserTypeSpinner));
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        EditText nameText = (EditText)v.findViewById(R.id.searchUserNameEditText);
        nameText.addTextChangedListener(this);

        listView = (ListView) v.findViewById(R.id.userList); //Bind to the list on our Search layout

        setListView(listView);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        userFilter = new UserFilter(query,selected,listAdapter,this);
        ((TextView)getActivity().findViewById(R.id.recentAddedBarTextView)).setText("User Search");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        selected = parent.getItemAtPosition(position).toString();
        checkSelected(selected);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        userFilter.filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {}

    private void checkSelected(String selected)
    {
        if(userFilter == null)
            userFilter = new UserFilter(query,selected,listAdapter,this);

        if (selected != null) {
            if (selected.equals("All")) {
                userFilter.setFilter("all");
            } else if (selected.equals("Friends Only")) {
                userFilter.setFilter("friends");
            }

            String filterText = ((EditText)getActivity()
                    .findViewById(R.id.searchUserNameEditText))
                    .getText().toString();

            if(filterText.length() > 0)
                userFilter.filter(filterText);
            else
                userFilter.filter("");
        }

    }
}
