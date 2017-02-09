package alanstudio.com.easyshop.views.ItemListViews;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import alanstudio.com.easyshop.R;
import alanstudio.com.easyshop.entities.ShoppingListItem;
import alanstudio.com.easyshop.infastructure.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.list_shopping_item_layout)
    public View layout;

    @BindView(R.id.list_shopping_item_listName)
    public TextView listName;

    @BindView(R.id.list_shopping_item_boughtBy)
    public TextView boughtBy;

    @BindView(R.id.list_shopping_item_boughtByName)
    public TextView boughtByName;

    @BindView(R.id.list_shopping_item_deleteButton)
    public ImageButton deleteButton;

    public ItemListViewHolder(View itemView){
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void populate(ShoppingListItem shoppingListItem, String currentUserEmail){
        listName.setText(shoppingListItem.getItemName());

        if (shoppingListItem.isBought()) {

            listName.setPaintFlags(listName.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            deleteButton.setImageResource(R.mipmap.ic_tick);
            deleteButton.setEnabled(false);

            boughtBy.setVisibility(View.VISIBLE);

            if (currentUserEmail.equals(shoppingListItem.getBoughtBy())) {
                boughtByName.setText(R.string.current_user_item_bought_by);
            } else {
                boughtByName.setText(Utils.decodeEmail(shoppingListItem.getBoughtBy()));
            }

        } else {

            listName.setPaintFlags(listName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            deleteButton.setImageResource(R.mipmap.ic_dustbin);
            boughtBy.setVisibility(View.INVISIBLE);
            boughtByName.setVisibility(View.INVISIBLE);
        }

    }


}
