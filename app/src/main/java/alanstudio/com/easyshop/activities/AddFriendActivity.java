package alanstudio.com.easyshop.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

import alanstudio.com.easyshop.R;
import alanstudio.com.easyshop.entities.Friend;
import alanstudio.com.easyshop.entities.User;
import alanstudio.com.easyshop.infastructure.Utils;
import alanstudio.com.easyshop.services.ShareListServices;
import alanstudio.com.easyshop.views.ShareListViews.AddFriendListViewHolder;

/**
 * Activity which displays all users registered with the app and allows the user to add people as friends
 */
public class AddFriendActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter mAdapter;

    private DatabaseReference friendsReference;
    private DatabaseReference sharedListReference;

    private ValueEventListener listener;

    private Friend usersFriends;
    private String mShoppingListId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("");
        setContentView(R.layout.activity_add_friend);
        recyclerView = (RecyclerView)findViewById(R.id.activity_add_friend_listRecyclerView);
        mShoppingListId = getIntent().getStringExtra("LIST_ID");

        friendsReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIREBASE_BASE_URL + Utils.FIREBASE_FRIEND_REF + userEmail
        );
        bus.post(new ShareListServices.GetUsersFriendsRequest(friendsReference));
    }

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseReference usersListReference = FirebaseDatabase.getInstance().getReferenceFromUrl(Utils.FIREBASE_USER_REF);

        mAdapter = new FirebaseRecyclerAdapter<User,AddFriendListViewHolder>(User.class, R.layout.list_app_users,
                AddFriendListViewHolder.class, usersListReference) {
            @Override
            protected void populateViewHolder(final AddFriendListViewHolder viewHolder, final User user, int position) {
                viewHolder.populate(user);

                if (isFriend(usersFriends.getUsersFriends(), user)) {
                    viewHolder.addFriend.setImageResource(R.mipmap.ic_tick);
                } else {
                    viewHolder.addFriend.setImageResource(R.mipmap.ic_plus);
                }

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!Utils.decodeEmail(userEmail).equals(user.getEmail())) {

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                                    Utils.FIREBASE_BASE_URL + Utils.FIREBASE_FRIEND_REF + userEmail + "/" + Utils.FIREBASE_FRIEND_REF +
                                            "/" + Utils.encodeEmail(user.getEmail())
                            );

                            //call to check if list has been shared with a friend
                            sharedListReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                                    Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHARE_LIST_REF + mShoppingListId + "/"
                                    + Utils.FIREBASE_SHARE_LIST_REF + Utils.encodeEmail(user.getEmail())
                            );

                            if (isFriend(usersFriends.getUsersFriends(), user)) {
                                reference.removeValue();
                                sharedListReference.removeValue();
                                viewHolder.addFriend.setImageResource(R.mipmap.ic_plus);
                            } else {
                                reference.setValue(user);
                                viewHolder.addFriend.setImageResource(R.mipmap.ic_tick);
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "You cannot add yourself as a friend", Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
    }

    @Subscribe
    public void getUsersFriends(ShareListServices.GetUsersFriendsResponse response) {
        listener = response.listener;

        if (response.usersFriends != null) {
            usersFriends = response.usersFriends;
        } else {
            usersFriends = new Friend();
        }
    }

    private boolean isFriend(HashMap<String,User> userFriends, User user){
        return userFriends!=null && userFriends.size()!=0 &&
                userFriends.containsKey(Utils.encodeEmail(user.getEmail()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
        friendsReference.removeEventListener(listener);
    }
}
