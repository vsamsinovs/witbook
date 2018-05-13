package ie.cmfbi.api;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ie.cmfbi.models.Comment;
import ie.cmfbi.models.Post;
import ie.cmfbi.models.User;

public class FBDBManager {

    private static final String TAG = "witbook";
    public DatabaseReference mFirebaseDatabase;

    //storage will be used to create a FirebaseStorage instance
    public FirebaseStorage mFirebaseStorage;
    //storageReference will point to the uploaded file
    public StorageReference mFirebaseStorageReference;

    public static String mFBUserId;
    public FBDBListener mFBDBListener;


    public void open() {
        //Set up local caching
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Bind to remote Firebase Database
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();

        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseStorageReference = mFirebaseStorage.getReference();

        Log.v(TAG, "Database Connected :" + mFirebaseDatabase);
    }

    public void attachListener(FBDBListener listener) {
        mFBDBListener = listener;
    }

    //Check to see if the Firebase User exists in the Database
    //if not, create a new User
    public void checkUser(final String userid, final String username, final String email) {
        Log.v(TAG, "checkUser ID == " + userid);
        mFirebaseDatabase.child("users").child(userid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mFBDBListener.onSuccess(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mFBDBListener.onFailure();
                    }
                }
        );
    }

    public void getCurrentUser(){
        getUser(mFBUserId);
    }


    public void getUser(final String userKey) {
        mFirebaseDatabase.child("users").child(userKey).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.v(TAG, "The read Succeeded: " + dataSnapshot.toString());
                        mFBDBListener.onSuccess(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.v(TAG, "The read failed: " + firebaseError.getMessage());
                        mFBDBListener.onFailure();
                    }
                });
    }



    public void updateImageUser(final User user, Bitmap bitmap) {

        StorageReference ref = mFirebaseStorageReference.child("user-images/" + UUID.randomUUID().toString());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = ref.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception exception) {

            }

        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                updateUser(user, downloadUrl);
            }
        });

    }

    public void updateUser(final User user, @Nullable Uri uri) {

        if(uri != null){
            user.profileImgUrl = uri.toString();
        }

        mFirebaseDatabase.child("users").child(mFBUserId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.v(TAG, "The update Succeeded: " + dataSnapshot.toString());
                        dataSnapshot.getRef().setValue(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.v(TAG, "The update failed: " + firebaseError.getMessage());
                        mFBDBListener.onFailure();
                    }
                });
    }

    public void isFirstTimeLikingPost(final String postKey){
        Query query = mFirebaseDatabase.child("post-likes").child(postKey).child(mFBUserId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0){
                    mFBDBListener.onFailure();
                }
                else {
                    mFBDBListener.onSuccess(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mFBDBListener.onFailure();
            }
        });
    }

    public void likePost(final String postKey) {

        mFirebaseDatabase.child("posts").child(postKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.v(TAG, "The update Succeeded: " + dataSnapshot.toString());

                        Post post = dataSnapshot.getValue(Post.class);
                        post.numLikes++;

                        Map<String, Object> postValues = post.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();

                        childUpdates.put("/likes/" + mFBUserId, postValues);

                        childUpdates.put("/post-likes/" + postKey + "/" + mFBUserId, postValues);

                        mFirebaseDatabase.updateChildren(childUpdates);

                        dataSnapshot.getRef().setValue(post);
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.v(TAG, "The update failed: " + firebaseError.getMessage());
                        mFBDBListener.onFailure();
                    }
                });
    }

    public void addVideoPost(final Post post, final Uri uri){

        StorageReference riversRef = mFirebaseStorageReference.child("post-videos/" + UUID.randomUUID().toString());
        UploadTask uploadTask = riversRef.putFile(uri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                addPost(post, null, downloadUrl);
            }
        });
    }

    public void addImagePost(final Post post, Bitmap bitmap) {

        StorageReference ref = mFirebaseStorageReference.child("post-images/" + UUID.randomUUID().toString());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = ref.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception exception) {

            }

        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                addPost(post, downloadUrl, null);
            }
        });

    }

    public void addComment(final String postKey, final String comment){
        mFirebaseDatabase.child("posts").child(postKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.v(TAG, "The update Succeeded: " + dataSnapshot.toString());

                        Post post = dataSnapshot.getValue(Post.class);
                        post.numComments++;

                        Comment com = new Comment(comment, mFBUserId, postKey);

                        String key = mFirebaseDatabase.child("comments").push().getKey();
                        Map<String, Object> postValues = com.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();

                        childUpdates.put("/comments/" + key, postValues);

                        childUpdates.put("/post-comments/" + postKey + "/" + key, postValues);

                        mFirebaseDatabase.updateChildren(childUpdates);

                        dataSnapshot.getRef().setValue(post);

                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.v(TAG, "The update failed: " + firebaseError.getMessage());
                        mFBDBListener.onFailure();
                    }
                });
    }

    public void addPost(final Post p, @Nullable Uri imgUri, @Nullable Uri videoUri) {

        if (imgUri != null) {
            p.imgUrl = imgUri.toString();
        }

        if(videoUri != null){
            p.videoUrl = videoUri.toString();
        }

        mFirebaseDatabase.child("users").child(mFBUserId).addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);
                        Log.v(TAG, "ADDING POST");

                        if (user == null) {
                            Log.v(TAG, "User " + mFBUserId + " is unexpectedly null");
                        } else {
                            writeNewPost(p);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.v(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });

    }


    private void writeNewPost(Post p) {

        String key = mFirebaseDatabase.child("posts").push().getKey();
        Map<String, Object> postValues = p.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/posts/" + key, postValues);

        childUpdates.put("/user-posts/" + mFBUserId + "/" + key, postValues);

        mFirebaseDatabase.updateChildren(childUpdates);
    }


    public Query getComments(String postKey) {
        Query query = mFirebaseDatabase.child("post-comments").child(postKey);

        return query;
    }

    public Query getAllPosts() {
        Query query = mFirebaseDatabase.child("posts").orderByChild("dateCreatedMills");

        return query;
    }

    public Query getAllUserPosts(String key) {
        Query query = mFirebaseDatabase.child("posts").orderByChild("uid").equalTo(key);

        return query;
    }

    public Query getAllUsers() {
        Query query = mFirebaseDatabase.child("users").orderByChild("userName");

        return query;
    }

    public Query getAllFriends(String s) {
        String l = s.toLowerCase();
        Query query = mFirebaseDatabase.child("user-friends").child(mFBUserId)
                .orderByChild("userNameLower").startAt(l).endAt(l + "\uf8ff");

        return query;
    }

    public Query getAllFriends() {

        Query query = mFirebaseDatabase.child("user-friends").child(mFBUserId)
                .orderByChild("userNameLower");

        return query;
    }

    public Query getAllUsers(String s) {
        //TODO: Ignore case
        String l = s.toLowerCase();
        Query query = mFirebaseDatabase.child("users")
                .orderByChild("userNameLower").startAt(l).endAt(l + "\uf8ff");

        return query;
    }

}
