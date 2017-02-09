package alanstudio.com.easyshop.activities;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import alanstudio.com.easyshop.R;
import alanstudio.com.easyshop.R2;
import alanstudio.com.easyshop.dialog.AddListDialogFragment;
import alanstudio.com.easyshop.dialog.DeleteListDialogFragment;
import alanstudio.com.easyshop.entities.ShoppingList;
import alanstudio.com.easyshop.infastructure.Utils;
import alanstudio.com.easyshop.views.ShoppingListViews.ShoppingListViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.activity_main_FAB)
    FloatingActionButton floatingActionButton;

    RecyclerView recyclerView;

    FirebaseRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        recyclerView = (RecyclerView)findViewById(R.id.activity_main_listRecyclerView);

        String toolBarName;

        if (userName.contains(" ")){
            toolBarName = userName.substring(0, userName.indexOf(" ")) + "'s shopping list";
        } else {
            toolBarName = userName + "'s shopping list";
        }

        getSupportActionBar().setTitle(toolBarName);

    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseReference shoppingListReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHOPPING_LIST_REFERENCE + userEmail);

        //set the list preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
        String sortOrder = sharedPreferences.getString(Utils.LIST_ORDER_PREFERENCE, Utils.ORDER_BY_KEY);
        Query sortQuery;

        if (sortOrder.equals(Utils.ORDER_BY_KEY)) {
            sortQuery = shoppingListReference.orderByKey();
        } else {
            sortQuery = shoppingListReference.orderByChild(sortOrder);
        }

        //set the list
        mAdapter = new FirebaseRecyclerAdapter<ShoppingList,ShoppingListViewHolder>(ShoppingList.class, R.layout.list_shopping_list,
                ShoppingListViewHolder.class, sortQuery) {

            @Override
            protected void populateViewHolder(ShoppingListViewHolder viewHolder, final ShoppingList shoppingList, int position) {
                viewHolder.populate(shoppingList);
                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ArrayList<String> shoppingListInfo = new ArrayList<String>();
                        shoppingListInfo.add(shoppingList.getId());//0
                        shoppingListInfo.add(shoppingList.getListName());//1
                        shoppingListInfo.add(shoppingList.getOwnerEmail());//2
                        startActivity(ListDetailsActivity.newInstance(getApplicationContext(),shoppingListInfo));
                    }
                });

                viewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (userEmail.equals(Utils.encodeEmail(shoppingList.getOwnerEmail()))) {
                            DialogFragment dialogFragment = DeleteListDialogFragment.newInstance(shoppingList.getId(),true);
                            dialogFragment.show(getFragmentManager(), DeleteListDialogFragment.class.getSimpleName());
                            return true;
                        } else {
                            Toast.makeText(getApplicationContext(),"Only the owner can delete a list", Toast.LENGTH_LONG).show();
                            return true;
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
        mAdapter.cleanup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case (R.id.action_logout):
                SharedPreferences sharedPreferences2 = getSharedPreferences(Utils.MY_PREFERENCE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences2.edit();
                editor.putString(Utils.EMAIL,null).apply();
                editor.putString(Utils.USERNAME,null).apply();
                auth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();

                return true;

            case (R.id.action_sort):
                startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.activity_main_FAB)
    public void setFAB(){
        DialogFragment dialogFragment = AddListDialogFragment.newInstance();
        dialogFragment.show(getFragmentManager(),AddListDialogFragment.class.getSimpleName());
    }
}
