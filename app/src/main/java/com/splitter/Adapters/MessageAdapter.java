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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.splitter.Model.Message;
import com.splitter.R;

import java.util.HashMap;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyHolder>{

    private  static final int MSG_TYPE_FROM_OTHER_USER = 0;
    private  static final int MSG_TYPE_FROM_THIS_USER = 1;

    Context context;
    List<Message> chatList;
    Boolean isGroup = false;
    FirebaseUser fUser;
    String sender;

    public MessageAdapter(Context context, List<Message> chatList, Boolean isGroup) {
        this.context = context;
        this.chatList = chatList;
        this.isGroup = isGroup;
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
        Message msg = chatList.get(position);
        if(isGroup) {
            sender = "hidden";
            setSenderName(msg.getReceiver());
            holder.senderTv.setText(sender);
        }
        holder.msgTv.setText(msg.getMsg());
        holder.timeTv.setText(msg.getTimeStamp());
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
                    deleteMsg(msgID);
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

    private void setSenderName(String senderID){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = firebaseDatabase.getReference("Users");
        Query chatQuery = dbRef.orderByChild("id").equalTo(senderID);
        chatQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    // To get & set image & name
                    sender = ""+dataSnapshot.child("name").getValue();
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void deleteMsg(String key) {
        DatabaseReference dbRefToMSG = FirebaseDatabase.getInstance().getReference("Chats").child(key);
        //dbRefToMSG.removeValue();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("msg", "This message was deleted...");
        dbRefToMSG.updateChildren(hashMap);
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
        TextView senderTv, msgTv, timeTv, isSeenTv;
        LinearLayout msgLayout;

        public MyHolder(@NonNull View itemView) {
        super(itemView);
        senderTv = itemView.findViewById(R.id.senderTv);
        // if not group don't show sender`s name
        if(!isGroup) senderTv.setVisibility(View.GONE);
        msgTv = itemView.findViewById(R.id.msgTv);
        timeTv = itemView.findViewById(R.id.msgTimeTv);
        isSeenTv = itemView.findViewById(R.id.msgIsSeenTv);
        msgLayout = itemView.findViewById(R.id.msgLayout);
        }
    }

}
