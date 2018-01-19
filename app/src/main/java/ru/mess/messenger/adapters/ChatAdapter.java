package ru.mess.messenger.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

import ru.mess.messenger.R;
import ru.mess.messenger.models.Message;

/**
 * Created by Shugig on 11/10/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {


    ArrayList<Message> messages;

    public ChatAdapter(ArrayList<Message> messages)
    {
        this.messages = messages;
    }
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item,parent,false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView  message;



        public ViewHolder(View itemView) {
            super(itemView);

            message  = (TextView) itemView.findViewById(R.id.message);
        }
    }

    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder holder, int position) {

        Message message = messages.get(position);
        holder.message.setText(message.getContent());
        //bind values here

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }




}
