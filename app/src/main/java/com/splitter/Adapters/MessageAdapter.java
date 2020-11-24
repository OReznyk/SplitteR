package com.splitter.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.splitter.Model.Message;
import com.splitter.Model.Uploader;
import com.splitter.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyHolder>{

    private  static final int MSG_TYPE_FROM_OTHER_USER = 0;
    private  static final int MSG_TYPE_FROM_THIS_USER = 1;

    Context context;
    List<Message> chatList;
    Uploader uploader;
    FirebaseUser fUser;

    public MessageAdapter(Context context, List<Message> chatList) {
        this.context = context;
        this.chatList = chatList;
    }


    @NonNull
    @Override
    // This function sets message view by checking who send it
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == MSG_TYPE_FROM_OTHER_USER){
            view = LayoutInflater.from(context).inflate(R.layout.view_row_msg_other, parent, false);
        }
        else{
            view = LayoutInflater.from(context).inflate(R.layout.view_row_msg_user, parent, false);
        }
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        String msg = chatList.get(position).getMsg();
        String timeStamp = chatList.get(position).getTimeStamp();
        holder.msgTv.setText(msg);
        holder.timeTv.setText(timeStamp);
        // ToDo: set seen status. Problem with seen setter
        if (position == chatList.size()-1){
            if(!chatList.get(chatList.size()-1).isSeen()) holder.isSeenTv.setText("delivered");
            else holder.isSeenTv.setText("seen");
        }
        else{
            holder.isSeenTv.setVisibility(View.GONE);
        }
        // show delete dialog on click
        holder.msgLayout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete message");
            builder.setMessage("Do you want to delete this message?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                // delete message
                public void onClick(DialogInterface dialog, int which) {
                    String msgID = chatList.get(position).getId();
                    uploader = new Uploader();
                    uploader.deleteMsg(msgID);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        });
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
        LinearLayout msgLayout;

        public MyHolder(@NonNull View itemView) {
        super(itemView);
        msgTv = itemView.findViewById(R.id.msgTv);
        timeTv = itemView.findViewById(R.id.msgTimeTv);
        isSeenTv = itemView.findViewById(R.id.msgIsSeenTv);
        msgLayout = itemView.findViewById(R.id.msgLayout);
        }
    }

}
