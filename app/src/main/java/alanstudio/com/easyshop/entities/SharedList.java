package alanstudio.com.easyshop.entities;


import android.support.annotation.Nullable;

import java.util.HashMap;

public class SharedList {

    @Nullable
    private HashMap<String, User> sharedWith;

    public SharedList() {
    }

    public SharedList(HashMap<String, User> sharedWith) {
        this.sharedWith = sharedWith;
    }

    @Nullable
    public HashMap<String, User> getSharedWith() {
        return sharedWith;
    }
}



