package alanstudio.com.easyshop.views.ShareListViews;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import alanstudio.com.easyshop.R;
import alanstudio.com.easyshop.entities.User;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.list_users_friends_name)
    public TextView friendsName;

    @BindView(R.id.list_users_friends_share_list)
    public ImageView shareListImageView;

    public ShareListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void populate(User user){
        itemView.setTag(user);
        friendsName.setText(user.getName());
    }
}
