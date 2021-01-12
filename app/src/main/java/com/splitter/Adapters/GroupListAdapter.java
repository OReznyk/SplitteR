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
import com.splitter.Model.Group;
import com.splitter.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.MyHolder> {
    Context context;
    List<Group> groupsList;
    private final HashMap<String, String> lastMessages;

    public GroupListAdapter(Context context, List<Group> groupsList) {
        this.context = context;
        this.groupsList = groupsList;
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
    public GroupListAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_row_user_or_chat, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Group group = groupsList.get(position);
        String lastMessage = lastMessages.get(group.getId());
        //set data
        holder.titleTv.setText(group.getTitle());
        if(lastMessage == null || lastMessage.equals("default")){
            holder.textTv.setVisibility(View.GONE);
        }
        else{
            holder.textTv.setVisibility(View.VISIBLE);
            holder.textTv.setText(lastMessage);
        }
        try {
            Picasso.get().load(group.getGroupImg())
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(holder.imgTv);
        } catch (Exception e) {
        }


        //handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("chatID", group.getId());
                intent.putExtra("isGroup", true);
                context.startActivity(intent);
            }
        });

    }

    @Override public int getItemCount() {
        return groupsList.size();
    }

}
