package pecia.socialmap;

import java.util.ArrayList;

/**
 * Created by Pierluca on 7/12/2017.
 */

public class UserKey {
    public String userName;
    public ArrayList<String> key;



    public UserKey(){

    }

    public UserKey(String userId, String chiave){
        key = new ArrayList<String>();
        this.userName = userId;
        this.key.add(chiave);
    }
}
