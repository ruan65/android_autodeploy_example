package ru.mess.messenger.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.mess.messenger.R;
import ru.mess.messenger.activities.chat.GroupChatActivity;
import ru.mess.messenger.activities.chat.P2PActivity;
import ru.mess.messenger.models.Group;
import ru.mess.messenger.models.User;

/**
 * Created by Shugig on 11/10/2017.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {


    ArrayList<Group> groups = new ArrayList<>();
    Context context;
    User current;

    public GroupAdapter(ArrayList<Group> people , Context context, User current)
    {
        this.groups = people;
        this.context = context;
        this.current = current;

    }
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item,parent,false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView restoImage;
        TextView name, participants, description;



        public ViewHolder(View itemView) {
            super(itemView);

            restoImage = (ImageView) itemView.findViewById(R.id.profile_image);
            name = (TextView) itemView.findViewById(R.id.name);
            participants  = (TextView) itemView.findViewById(R.id.role);
            description  = (TextView) itemView.findViewById(R.id.message);
        }
    }

    @Override
    public void onBindViewHolder(GroupAdapter.ViewHolder holder, int position) {

        //bind values here

        Group group = groups.get(position);


        holder.name.setText(group.getName());
        holder.description.setText(group.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, GroupChatActivity.class);
                intent.putExtra("currentUser",current);
                intent.putExtra("group",group);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }


    public void addGroup(Group group)
    {
        groups.add(group);
    }

    public void addAllGroups(ArrayList<Group> groups)
    {
        groups.addAll(groups);
    }


    public ArrayList<Group> getGroups() {
        return groups;
    }
}
