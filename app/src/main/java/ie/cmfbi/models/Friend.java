package ie.cmfbi.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Friend {

    public String _id;
    public String uid;
    public String friendUid;
    public String usertoken;
    public boolean isTrueFriend;


    public Friend() {
        this.uid = "";
        this.friendUid = "";
        this.isTrueFriend = false;
        this.usertoken = "";
    }

    public Friend(String uid, String friendUid, Boolean isTrueFriend, String usertoken)
    {
        this.uid = uid;
        this.isTrueFriend = isTrueFriend;
        this.usertoken = usertoken;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("friendUid", friendUid);
        result.put("isTrueFriend", isTrueFriend);
        result.put("usertoken", usertoken);

        return result;
    }
}
