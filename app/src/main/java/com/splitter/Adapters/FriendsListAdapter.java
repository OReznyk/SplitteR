package com.splitter.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.splitter.Activities.ChatActivity;
import com.splitter.Model.User;
import com.splitter.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.MyHolder> {
    Context context;
    List<User> friendsList;

    public FriendsListAdapter(Context context, List<User> friendsList) {
        this.context = context;
        this.friendsList = friendsList;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView mAvatarIv;
        TextView mNameTv, mEmailTv;

        public MyHolder(@NonNull View view) {
            super(view);
            mAvatarIv = view.findViewById(R.id.friend_avatarIv);
            mEmailTv = view.findViewById(R.id.friend_email);
            mNameTv = view.findViewById(R.id.friend_name);
        }
    }

    @NonNull
    @Override
    public FriendsListAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_row_user, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        final String friendsId = friendsList.get(position).getId();
        String userImage = friendsList.get(position).getAvatar();
        String userName = friendsList.get(position).getName();
        String userEmail = friendsList.get(position).getEmail();
        //set data
        holder.mNameTv.setText(userName);
        holder.mEmailTv.setText(userEmail);
        try {
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(holder.mAvatarIv);
        } catch (Exception e) {
        }

        //handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("friendsID", friendsId);
                context.startActivity(intent);
            }
        });

    }

    @Override public int getItemCount() {
        return friendsList.size();
    }

}
