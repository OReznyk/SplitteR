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

import java.util.HashMap;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyHolder> {
    Context context;
    List<User> chatsList;
    private final HashMap<String, String> lastMessages;

    public ChatListAdapter(Context context, List<User> chatsList) {
        this.context = context;
        this.chatsList = chatsList;
        lastMessages = new HashMap<>();
    }
    public void setLastMessage(String userID, String msg){
        lastMessages.put(userID, msg);
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView imgTv;
        TextView titleTv, textTv;

        public MyHolder(@NonNull View view) {
            super(view);
            imgTv = view.findViewById(R.id.row_imgTv);
            textTv = view.findViewById(R.id.wallet_user_name);
            titleTv = view.findViewById(R.id.wallet_user_spentTv);
        }
    }

    @NonNull
    @Override
    public ChatListAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_row_user_or_chat, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        User user = chatsList.get(position);
        String lastMessage = lastMessages.get(user.getId());
        //set data
        holder.titleTv.setText(user.getName());
        if(lastMessage == null || lastMessage.equals("default")){
            holder.textTv.setVisibility(View.GONE);
        }
        else{
            holder.textTv.setVisibility(View.VISIBLE);
            holder.textTv.setText(lastMessage);
        }
        try {
            Picasso.get().load(user.getAvatar())
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(holder.imgTv);
        } catch (Exception e) {
        }


        //handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("chatID", user.getId());
                intent.putExtra("isGroup", false);
                context.startActivity(intent);
            }
        });

    }

    @Override public int getItemCount() {
        return chatsList.size();
    }

}
