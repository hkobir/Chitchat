package com.example.chitchat.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.example.chitchat.R;
import com.example.chitchat.models.ChatModel;
import com.google.firebase.auth.FirebaseAuth;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private List<ChatModel> list_chat_models;
    private Context context;
    private int MESSAGE_LEFT = 0;
    private int MESSAGE_RIGHT = 1;
    PrettyTime prettyTime = new PrettyTime();

    public CustomAdapter(List<ChatModel> list_chat_models, Context context) {
        this.list_chat_models = list_chat_models;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == MESSAGE_RIGHT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_send,parent,false);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_tem_recieve,parent,false);
        }
        return new ViewHolder((view));
    }

    @Override
    public int getItemViewType(int position) {

        if(isSender(list_chat_models.get(position).getMessageUser()))
            return MESSAGE_RIGHT;
        else
            return MESSAGE_LEFT;


    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        ChatModel chat = list_chat_models.get(position);
        holder.msg.setText(chat.getMessageText());
        holder.time.setText(prettyTime.format(new Date(chat.getMessageTime())));
        holder.userName.setText(chat.getUserName());
    }

    @Override
    public int getItemCount() {
        return list_chat_models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView msg,time,userName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.text_message);
            time = itemView.findViewById(R.id.timeStmp);
            userName = itemView.findViewById(R.id.userName);
        }
    }

    public boolean isSender(String userEmail){
        boolean flag;
        if(FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(userEmail)){
            flag = true;
        }
        else {
            flag =false;
        }
        return flag;
    }
}
