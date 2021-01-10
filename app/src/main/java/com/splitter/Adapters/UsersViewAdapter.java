package com.splitter.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.splitter.Activities.ChatActivity;
import com.splitter.Model.User;
import com.splitter.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersViewAdapter extends RecyclerView.Adapter<UsersViewAdapter.MyHolder> {
    Context context;
    List<User> userList;
    String groupID;
    Boolean addParticipant, isAdmin;

    public UsersViewAdapter(Context context, List<User> userList, String groupID, Boolean addParticipant, Boolean isAdmin) {
        this.context = context;
        this.userList = userList;
        this.groupID = groupID;
        this.addParticipant = addParticipant;
        this.isAdmin = isAdmin;
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
    public UsersViewAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_row_user_or_chat, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewAdapter.MyHolder holder, int position) {
        //get data
        User user = userList.get(position);
        //set data
        holder.mNameTv.setText(user.getName());
        if(addParticipant){
            checkIfParticipantExists(user, holder);
        }
        else holder.mEmailTv.setText(user.getEmail());
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
                if(!addParticipant) {
                    //if regular userListActivity: open chat on click
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("chatID", user.getId());
                    intent.putExtra("isGroup", false);
                    context.startActivity(intent);
                }
                else{
                    //for group participants settings
                    DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
                    groupsRef.child(groupID).child("participants").child(user.getId())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    String [] dialogOptions;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    if(isAdmin){
                                        // create dialog for admins
                                        if(snapshot.exists()){
                                            // if participant added
                                            String participantPrevRole = "" + snapshot.getValue();
                                            builder.setTitle("Choose Option:");
                                            // if participant is admin
                                            if(participantPrevRole.equals("admin")){
                                                dialogOptions = new String[]{"Remove admin permissions", "Remove participant"};
                                                builder.setItems(dialogOptions, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if(which == 0){
                                                            //ToDo remove admin permissions
                                                            setParticipant(holder, user, "participant");
                                                        }
                                                        else{
                                                            //TODO remove participant
                                                            removeParticipant(holder, user);
                                                        }
                                                    }
                                                });
                                            }
                                            else{
                                                // if participant no admin
                                                dialogOptions = new String[]{"Give admin permissions", "Remove participant"};
                                                builder.setItems(dialogOptions, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if(which == 0){
                                                            //ToDo give admin permissions
                                                            setParticipant(holder, user, "admin");
                                                        }
                                                        else{
                                                            //TODO remove participant
                                                            removeParticipant(holder, user);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                        else {
                                            //user is not participant
                                            dialogOptions = new String[]{"Add as admin", "Add as participant"};
                                            builder.setItems(dialogOptions, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if(which == 0){
                                                        //ToDo give admin permissions
                                                        setParticipant(holder, user, "admin");
                                                    }
                                                    else{
                                                        //TODO add participant
                                                        setParticipant(holder, user, "participant");
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    else{
                                        // create dialog for no admins
                                        builder.setTitle("For group admins only");
                                        builder.setMessage("No options available");
                                    }
                                    builder.show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                }
            }
        });

    }

    private void checkIfParticipantExists(User user, MyHolder holder) {
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
        groupsRef.child(groupID).child("participants").child(user.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String role = "" + snapshot.getValue();
                    holder.mEmailTv.setText(role);
                }
                else holder.mEmailTv.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeParticipant(MyHolder holder, User user) {
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
        groupsRef.child(groupID).child("participants").child(user.getId()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                        holder.mEmailTv.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setParticipant(MyHolder holder, User user, String admin) {
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
        groupsRef.child(groupID).child("participants").child(user.getId()).setValue(admin)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                holder.mEmailTv.setText(admin);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}
