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

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyHolder> {
    Context context;
    List<User> userList;

    public UsersAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView mAvatarIv;
        TextView mNameTv, mEmailTv;

        public MyHolder(@NonNull View view) {
            super(view);
            mAvatarIv = view.findViewById(R.id.row_imgTv);
            mEmailTv = view.findViewById(R.id.row_bottom_textField);
            mNameTv = view.findViewById(R.id.row_top_textField);
        }
    }

    @NonNull
    @Override
    public UsersAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_row_user_or_chat, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.MyHolder holder, int position) {
        //get data
        User user = userList.get(position);
        //set data
        holder.mNameTv.setText(user.getName());
        holder.mEmailTv.setText(user.getEmail());
        try {
            Picasso.get().load(user.getAvatar())
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(holder.mAvatarIv);
        } catch (Exception e) {
        }


        //handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("friendsID", user.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override public int getItemCount() {
        return userList.size();
    }

}
