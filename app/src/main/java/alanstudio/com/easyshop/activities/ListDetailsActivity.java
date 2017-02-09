package alanstudio.com.easyshop.activities;


import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import alanstudio.com.easyshop.R;
import alanstudio.com.easyshop.dialog.AddItemDialogFragment;
import alanstudio.com.easyshop.dialog.ChangeItemNameDialogFragment;
import alanstudio.com.easyshop.dialog.ChangeListNameDialogFragment;
import alanstudio.com.easyshop.dialog.DeleteItemDialogFragment;
import alanstudio.com.easyshop.dialog.DeleteListDialogFragment;
import alanstudio.com.easyshop.entities.ShoppingList;
import alanstudio.com.easyshop.entities.ShoppingListItem;
import alanstudio.com.easyshop.infastructure.Utils;
import alanstudio.com.easyshop.services.ShoppingItemServices;
import alanstudio.com.easyshop.services.ShoppingListServices;
import alanstudio.com.easyshop.views.ItemListViews.ItemListViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListDetailsActivity extends BaseActivity {

    @BindView(R.id.activity_list_details_FAB)
    FloatingActionButton floatingActionButton;

    public static final String SHOPPING_LIST_DETAILS = "SHOPPING_LIST_DETAILS";

    private String mShoppingId;
    private String mShoppingName;
    private String mOwnerName;

    private DatabaseReference mShoppingListReference;
    private ValueEventListener mShoppingListListener;
    private ShoppingList mCurrentShoppingList;

    private MenuItem shareItem;
    private MenuItem changeNameItem;
    private MenuItem deleteListItem;

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter mAdapter;

    public static Intent newInstance(Context context, ArrayList<String> shoppingListInfo) {

        Intent intent = new Intent(context,ListDetailsActivity.class);
        intent.putStringArrayListExtra(SHOPPING_LIST_DETAILS,shoppingListInfo);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);
        ButterKnife.bind(this);
        recyclerView = (RecyclerView) findViewById(R.id.activity_list_details_listRecyclerView);

        mShoppingId = getIntent().getStringArrayListExtra(SHOPPING_LIST_DETAILS).get(0);
        mShoppingName = getIntent().getStringArrayListExtra(SHOPPING_LIST_DETAILS).get(1);
        mOwnerName = getIntent().getStringArrayListExtra(SHOPPING_LIST_DETAILS).get(2);
        getSupportActionBar().setTitle(mShoppingName);

        mShoppingListReference = FirebaseDatabase.getInstance().getReference(Utils.FIREBASE_SHOPPING_LIST_REFERENCE
        + userEmail + "/" + mShoppingId);

        bus.post(new ShoppingListServices.GetCurrentShoppingListRequest(mShoppingListReference));
    }

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseReference itemListReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHOPPING_ITEM_REFERENCE + mShoppingId
        );

        mAdapter = new FirebaseRecyclerAdapter<ShoppingListItem,ItemListViewHolder>(ShoppingListItem.class, R.layout.list_shopping_item,
                ItemListViewHolder.class, itemListReference) {

            @Override
            protected void populateViewHolder(final ItemListViewHolder viewHolder, final ShoppingListItem shoppingListItem, int position) {
                viewHolder.populate(shoppingListItem, userEmail);
                    viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                    public void onClick(View view) {

                            bus.post(new ShoppingItemServices.ItemBoughtStatusRequest(shoppingListItem,userEmail,mShoppingId));
                    }
                });

                viewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        ArrayList<String> shoppingItemInfo = new ArrayList<String>();
                        shoppingItemInfo.add(mShoppingId);//0
                        shoppingItemInfo.add(shoppingListItem.getId());//1
                        shoppingItemInfo.add(shoppingListItem.getItemName());//2
                        shoppingItemInfo.add(shoppingListItem.getOwnerEmail());//3

                        if (userEmail.equals(Utils.encodeEmail(shoppingListItem.getOwnerEmail()))) {

                            DialogFragment dialogFragment = ChangeItemNameDialogFragment.newInstance(shoppingItemInfo);
                            dialogFragment.show(getFragmentManager(),ChangeItemNameDialogFragment.class.getSimpleName());
                            return true;

                        } else {

                            Toast.makeText(getApplicationContext(), "Only the list owner can change list items", Toast.LENGTH_LONG).show();
                            return true;
                        }
                    }
                });

                viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (userEmail.equals(Utils.encodeEmail(shoppingListItem.getOwnerEmail()))) {

                            DialogFragment dialogFragment = DeleteItemDialogFragment.newInstance(
                                    mShoppingId, shoppingListItem.getId(), true);
                            dialogFragment.show(getFragmentManager(), DeleteListDialogFragment.class.getSimpleName());

                        } else {

                            Toast.makeText(getApplicationContext(), "Only the list owner can change list items", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_details, menu);
        shareItem = menu.findItem(R.id.action_share_list);
        changeNameItem = menu.findItem(R.id.action_change_list_name);
        deleteListItem = menu.findItem(R.id.action_delete_list);

        //set menu items to invisible if user does not own list
        if (!userEmail.equals(Utils.encodeEmail(mOwnerName))) {
            shareItem.setVisible(false).setEnabled(false);
            changeNameItem.setVisible(false).setEnabled(false);
            deleteListItem.setVisible(false).setEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_change_list_name:
                if (userEmail.equals(Utils.encodeEmail(mOwnerName))){
                    ArrayList<String> shoppingListInfo = new ArrayList<>();
                    shoppingListInfo.add(mShoppingId);
                    shoppingListInfo.add(mShoppingName);

                    DialogFragment dialogFragment = ChangeListNameDialogFragment.newInstance(shoppingListInfo);
                    dialogFragment.show(getFragmentManager(),ChangeListNameDialogFragment.class.getSimpleName());
                    return true;
                } else {
                    return true;
                }

            case R.id.action_delete_list:
                if (userEmail.equals(Utils.encodeEmail(mOwnerName))) {
                    DialogFragment dialog = DeleteListDialogFragment.newInstance(mShoppingId,false);
                    dialog.show(getFragmentManager(), DeleteListDialogFragment.class.getSimpleName());
                    return true;
                } else {
                    return true;
                }

            case R.id.action_share_list:
                if (userEmail.equals(Utils.encodeEmail(mOwnerName))) {
                    Intent intent = new Intent(getApplicationContext(), ShareListActivity.class);
                    intent.putExtra("LIST_ID", mCurrentShoppingList.getId());
                    startActivity(intent);
                    return true;
                } else {
                    return true;
                }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mAdapter.cleanup();
        mShoppingListReference.removeEventListener(mShoppingListListener);
    }

    @Subscribe
    public void getCurrentShoppingList(ShoppingListServices.GetCurrentShoppingListResponse response) {

        mShoppingListListener = response.valueEventListener;
        mCurrentShoppingList = response.shoppingList;
        getSupportActionBar().setTitle(mCurrentShoppingList.getListName());
    }

    @OnClick(R.id.activity_list_details_FAB)
    public void setFAB(){
        if (userEmail.equals(Utils.encodeEmail(mOwnerName))){
            DialogFragment dialogFragment = AddItemDialogFragment.newInstance(mShoppingId);
            dialogFragment.show(getFragmentManager(),AddItemDialogFragment.class.getSimpleName());
        } else {
            Toast.makeText(getApplicationContext(), "Only the list owner can add list items", Toast.LENGTH_LONG).show();
        }

    }
}
