package ie.cmfbi.api;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import ie.cmfbi.main.WitBookApp;
import ie.cmfbi.models.User;

public class FBDBManagerPromise {

    private DatabaseReference mFirebaseDatabase;

    //storage will be used to create a FirebaseStorage instance
    private FirebaseStorage mFirebaseStorage;
    //storageReference will point to the uploaded file
    private StorageReference mFirebaseStorageReference;

    private String mFBUserId;
    private FBDBListener mFBDBListener;

    public WitBookApp app = WitBookApp.getInstance();

    public FBDBManagerPromise() {
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Bind to remote Firebase Database
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();

        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseStorageReference = mFirebaseStorage.getReference();
    }

    public void getUserPromise(final String userKey, final FBDBListener promise) {

        mFirebaseDatabase.child("users").child(userKey).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        promise.onSuccess(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        promise.onFailure();
                    }
                });
    }

    public void addFriend(final User friend) {

        Map<String, Object> postValues = friend.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/user-friends/" + app.mFirebaseUser.getUid()+"/"+friend.userId, postValues);

        mFirebaseDatabase.updateChildren(childUpdates);
    }

    public void checkFriend(final String userid, final FBDBListener promise) {
        mFirebaseDatabase.child("user-friends").child(app.mFirebaseUser.getUid()).child(userid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            promise.onSuccess(dataSnapshot);
                        }
                        else{
                            promise.onFailure();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        promise.onFailure();
                    }
                }
        );
    }

    public void removeFriend(final String userKey, final FBDBListener promise) {
        mFirebaseDatabase.child("user-friends").child(app.mFirebaseUser.getUid()).child(userKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeValue();
                        promise.onSuccess(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        promise.onFailure();
                    }
                });
    }
}
