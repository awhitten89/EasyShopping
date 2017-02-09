package alanstudio.com.easyshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

import alanstudio.com.easyshop.R;
import alanstudio.com.easyshop.entities.Friend;
import alanstudio.com.easyshop.entities.SharedList;
import alanstudio.com.easyshop.entities.ShoppingList;
import alanstudio.com.easyshop.entities.User;
import alanstudio.com.easyshop.infastructure.Utils;
import alanstudio.com.easyshop.services.ShareListServices;
import alanstudio.com.easyshop.services.ShoppingListServices;
import alanstudio.com.easyshop.views.ShareListViews.ShareListViewHolder;

/**
 * Activity which displays the users friends and allows them to share their list with them.
 */
public class ShareListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter mAdapter;

    private DatabaseReference sharedListReference;
    private DatabaseReference shoppingListReference;

    private ValueEventListener listener;
    private ValueEventListener shoppingListListener;

    private SharedList mSharedList;
    private String mShoppingListId;
    private ShoppingList mCurrentShoppingList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_list);
        recyclerView = (RecyclerView)findViewById(R.id.activity_share_list_listRecyclerView);
        mShoppingListId = getIntent().getStringExtra("LIST_ID");

        //call to check if list has been shared with a friend
        sharedListReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHARE_LIST_REF + mShoppingListId
        );
        bus.post(new ShareListServices.GetShareListRequest(sharedListReference));

        //call to get current shopping list
        shoppingListReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHOPPING_LIST_REFERENCE + userEmail + "/" + mShoppingListId
        );
        bus.post(new ShoppingListServices.GetCurrentShoppingListRequest(shoppingListReference));

        //call to populate list with friends
        DatabaseReference friendsReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIREBASE_BASE_URL + Utils.FIREBASE_FRIEND_REF + userEmail + "/" + Utils.FIREBASE_FRIEND_REF
        );

        mAdapter = new FirebaseRecyclerAdapter<User,ShareListViewHolder>(User.class, R.layout.list_users_friends,
                ShareListViewHolder.class, friendsReference) {
            @Override
            protected void populateViewHolder(final ShareListViewHolder viewHolder, final User user, int position) {
                viewHolder.populate(user);

                if (isShared(mSharedList.getSharedWith(), user)){
                    viewHolder.shareListImageView.setImageResource(R.mipmap.ic_tick);
                } else {
                    viewHolder.shareListImageView.setImageResource(R.mipmap.ic_plus);
                }

                viewHolder.shareListImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DatabaseReference shareListReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                                Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHARE_LIST_REF + mShoppingListId +
                                        "/" + Utils.FIREBASE_SHARE_LIST_REF + Utils.encodeEmail(user.getEmail())
                        );

                        DatabaseReference shareListShoppingListReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                                Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHOPPING_LIST_REFERENCE + Utils.encodeEmail(user.getEmail())
                                        + "/" + mShoppingListId
                        );

                        if (isShared(mSharedList.getSharedWith(), user)){
                            shareListReference.removeValue();
                            shareListShoppingListReference.removeValue();
                            updateListTimeLastChanged(mSharedList.getSharedWith(),mCurrentShoppingList,bus,true);
                            viewHolder.shareListImageView.setImageResource(R.mipmap.ic_plus);
                        } else {
                            shareListReference.setValue(user);
                            shareListShoppingListReference.setValue(mCurrentShoppingList);
                            updateListTimeLastChanged(mSharedList.getSharedWith(),mCurrentShoppingList,bus,false);
                            viewHolder.shareListImageView.setImageResource(R.mipmap.ic_tick);
                        }
                    }
                });
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Subscribe
    public void getSharedList(ShareListServices.GetShareListResponse response) {
        listener = response.listener;

        if (response.sharedList != null) {
            mSharedList = response.sharedList;
        } else {
            mSharedList = new SharedList();
        }
    }

    private boolean isShared(HashMap<String,User>sharedList, User user) {
        return sharedList != null && sharedList.size() != 0 &&
                sharedList.containsKey(Utils.encodeEmail(user.getEmail()));

    }

    @Subscribe
    public void getCurrentShoppingList(ShoppingListServices.GetCurrentShoppingListResponse response){
        shoppingListListener = response.valueEventListener;
        mCurrentShoppingList = response.shoppingList;
    }

    public static void updateListTimeLastChanged(HashMap<String, User>sharedList, ShoppingList list, Bus bus,
                                                 boolean deletingList){

        if (sharedList != null && !sharedList.isEmpty()) {
            for (User user: sharedList.values()) {
                if (sharedList.containsKey(Utils.encodeEmail(user.getEmail()))) {
                    DatabaseReference friendListRef = FirebaseDatabase.getInstance().getReferenceFromUrl(
                            Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHOPPING_LIST_REFERENCE +
                                    Utils.encodeEmail(user.getEmail()) + "/" + list.getId()
                    );

                    if (!deletingList){
                        bus.post(new ShoppingListServices.UpdateListTimeLastChangedRequest(friendListRef));
                    }
                }
            }
        }

        DatabaseReference ownerReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHOPPING_LIST_REFERENCE +
                        Utils.encodeEmail(list.getOwnerEmail()) + "/" + list.getId()
        );
        bus.post(new ShoppingListServices.UpdateListTimeLastChangedRequest(ownerReference));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
        sharedListReference.removeEventListener(listener);
        shoppingListReference.removeEventListener(shoppingListListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share_list, menu);
        getSupportActionBar().setTitle("Your Friends");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_add_friend) {
            Intent intent = new Intent(getApplicationContext(), AddFriendActivity.class);
            intent.putExtra("LIST_ID", mShoppingListId);
            startActivity(intent);
            return true;
        }
        return true;
    }

}
