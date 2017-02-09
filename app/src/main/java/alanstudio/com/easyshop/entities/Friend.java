package alanstudio.com.easyshop.entities;


import java.util.HashMap;

public class Friend {

    private HashMap<String, User> usersFriends;

    public Friend() {
    }

    public Friend(HashMap<String, User> usersFriends) {
        this.usersFriends = usersFriends;
    }

    public HashMap<String, User> getUsersFriends() {
        return usersFriends;
    }
}
