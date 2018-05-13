package ie.cmfbi.api;

import com.google.firebase.database.DataSnapshot;

public interface FBDBListener {
    void onSuccess(DataSnapshot dataSnapshot);
    void onFailure();
}
