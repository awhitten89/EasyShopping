package alanstudio.com.easyshop.views.ShareListViews;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import alanstudio.com.easyshop.R;
import alanstudio.com.easyshop.entities.User;
import alanstudio.com.easyshop.infastructure.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddFriendListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.list_app_users_layout)
    public View layout;

    @BindView(R.id.list_app_users_users_email)
    TextView appUsersEmail;

    @BindView(R.id.list_app_users_add_friend)
    public ImageView addFriend;

    public AddFriendListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void populate(User appUser) {
        itemView.setTag(appUser);
        appUsersEmail.setText(Utils.decodeEmail(appUser.getEmail()));

    }
}
