package alanstudio.com.easyshop.services;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import alanstudio.com.easyshop.entities.Friend;
import alanstudio.com.easyshop.entities.SharedList;

public class ShareListServices {

    private ShareListServices() {
    }

    public static class GetUsersFriendsRequest {

        public DatabaseReference reference;

        public GetUsersFriendsRequest(DatabaseReference reference) {
            this.reference = reference;
        }
    }

    public static class GetUsersFriendsResponse {

        public ValueEventListener listener;
        public Friend usersFriends;
    }

    public static class GetShareListRequest {

        public DatabaseReference reference;

        public GetShareListRequest(DatabaseReference reference) {
            this.reference = reference;
        }
    }

    public static class GetShareListResponse {

        public ValueEventListener listener;
        public SharedList sharedList;
    }
}


