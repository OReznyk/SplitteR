package com.splitter.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.splitter.Model.Chat;
import com.splitter.R;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyHolder> {
    Context context;
    List<Chat> chatsList;

    public ChatListAdapter(Context context, List<Chat> chatsList) {
        this.context = context;
        this.chatsList = chatsList;
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
    public ChatListAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_row_user, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        /*final String friendsId = chatsList.get(position).getId();
        String userImage = chatsList.get(position).getAvatar();
        String userName = chatsList.get(position).getName();
        //String userEmail = chatsList.get(position).getEmail();
        //set data
        holder.mNameTv.setText(userName);
        //holder.mEmailTv.setText(userEmail);
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
        });*/

    }

    @Override public int getItemCount() {
        return chatsList.size();
    }

}
