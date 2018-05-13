package ie.cmfbi.models;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Comment {

    public String uid;
    public Date dateCreated;
    public long dateCreatedMills;
    public String comment;
    public String postId;

    public Comment() {
        this.uid = "";
        this.comment = "";
        this.postId = "";
        this.dateCreatedMills = 0;
        this.dateCreated = new Date();
    }

    public Comment(String comment, String uid, String postId)
    {
        this.uid = uid;
        this.comment = comment;
        this.postId = postId;
        this.dateCreatedMills = 0;
        this.dateCreated = new Date();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        long _dateCreated;
        if(dateCreatedMills == 0)
            _dateCreated = dateCreated.getTime();
        else
            _dateCreated = dateCreatedMills;

        if(_dateCreated > 0) _dateCreated *= -1;

        result.put("uid", uid);
        result.put("comment", comment);
        result.put("postId", postId);
        result.put("dateCreatedMills", _dateCreated);

        return result;
    }
}
