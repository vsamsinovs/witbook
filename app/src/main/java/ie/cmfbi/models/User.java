package ie.cmfbi.models;

import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    public String userId;
    public String userName;
    public String userEmail;
    public String userNameLower;

    public String profileImgUrl;

    public User(){
        userId = "";
        userName = "";
        userEmail = "";
        profileImgUrl = "";
        userNameLower = "";
    }

    public User(String userId, String userName, String userEmail, @Nullable String profileImgUrl){
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;

        if(userName != null)
            this.userNameLower = userName.toLowerCase();

        if(profileImgUrl == null){
            this.profileImgUrl = "https://firebasestorage.googleapis.com/v0/b/coffeemate6-6e504.appspot.com/o/user-images%2Fdefault_img.jpg?alt=media&token=8a4b83cc-c822-412b-87e7-aab4b7e21983";
        }
        else{
            this.profileImgUrl = profileImgUrl;
        }
    }



    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("userName", userName);
        result.put("userEmail", userEmail);
        result.put("profileImgUrl", profileImgUrl);
        result.put("userNameLower", userNameLower);

        return result;
    }
}
