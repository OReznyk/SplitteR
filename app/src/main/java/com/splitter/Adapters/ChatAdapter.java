package com.splitter.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.splitter.Model.ChatModel;
import com.splitter.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyHolder>{

    private  static final int MSG_TYPE_FROM_OTHER_USER = 0;
    private  static final int MSG_TYPE_FROM_THIS_USER = 1;

    Context context;
    List<ChatModel> chatList;

    FirebaseUser fUser;

    public ChatAdapter(Context context, List<ChatModel> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == MSG_TYPE_FROM_OTHER_USER){
            view = LayoutInflater.from(context).inflate(R.layout.activity_row_msg_other, parent, false);
        }
        else{
            view = LayoutInflater.from(context).inflate(R.layout.activity_row_msg_user, parent, false);
        }
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String msg = chatList.get(position).getMsg();
        String timeStamp = chatList.get(position).getTimeStamp();
        holder.msgTv.setText(msg);
        holder.timeTv.setText(timeStamp);
        if (position == chatList.size()-1){
            if(chatList.get(position).isSeen()){
                holder.isSeenTv.setText("seen");
            }
            else{
                holder.isSeenTv.setText("delivered");
            }
        }
        else{
            holder.isSeenTv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position){
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_FROM_THIS_USER;
        }
        else{
            return MSG_TYPE_FROM_OTHER_USER;
        }
    }


    class MyHolder extends RecyclerView.ViewHolder {
        TextView msgTv, timeTv, isSeenTv;
        public MyHolder(@NonNull View itemView) {
        super(itemView);
        msgTv = itemView.findViewById(R.id.msgTv);
        timeTv = itemView.findViewById(R.id.msgTimeTv);
        isSeenTv = itemView.findViewById(R.id.msgIsSeenTv);
        }
    }
}
