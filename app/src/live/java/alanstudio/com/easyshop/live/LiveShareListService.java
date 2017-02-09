package alanstudio.com.easyshop.live;


import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;

import alanstudio.com.easyshop.entities.Friend;
import alanstudio.com.easyshop.entities.SharedList;
import alanstudio.com.easyshop.infastructure.EasyShoppingApplication;
import alanstudio.com.easyshop.services.ShareListServices;

public class LiveShareListService extends BaseLiveServices {

    public LiveShareListService(EasyShoppingApplication application) {
        super(application);
    }

    @Subscribe
    public void getUsersFriends(ShareListServices.GetUsersFriendsRequest request) {

        final ShareListServices.GetUsersFriendsResponse response = new ShareListServices.GetUsersFriendsResponse();

        response.listener = request.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                response.usersFriends = dataSnapshot.getValue(Friend.class);
                bus.post(response);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(application.getApplicationContext(), "Database Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Subscribe
    public void getSharedList(ShareListServices.GetShareListRequest request) {
        final ShareListServices.GetShareListResponse response = new ShareListServices.GetShareListResponse();

        response.listener = request.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                response.sharedList = dataSnapshot.getValue(SharedList.class);
                bus.post(response);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(application.getApplicationContext(), "Database Error", Toast.LENGTH_LONG).show();
            }
        });
    }
}
