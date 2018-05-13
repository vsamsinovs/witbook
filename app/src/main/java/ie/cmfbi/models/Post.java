package ie.cmfbi.models;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Post {

    public String _id;
    public Integer numLikes;
    public Integer numComments;
    public String uid;
    public String post;
    public String imgUrl;
    public String videoUrl;
    public Date dateCreated;
    public long dateCreatedMills;

    public Post() {
        this.uid = "";
        this.numLikes = 0;
        this.numComments = 0;
        this.post = "";
        this.imgUrl = "";
        this.videoUrl = "";
        this.dateCreatedMills = 0;
        this.dateCreated = new Date();
    }

    public Post(String uid, String post)
    {
        this.uid = uid;
        this.post = post;

        this.imgUrl = imgUrl;
        this.videoUrl = videoUrl;

        this.numLikes = 0;
        this.numComments = 0;
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
        result.put("post", post);
        result.put("numLikes", numLikes);
        result.put("numComments", numComments);
        result.put("imgUrl", imgUrl);
        result.put("videoUrl", videoUrl);
        result.put("dateCreatedMills", _dateCreated);

        return result;
    }
}
