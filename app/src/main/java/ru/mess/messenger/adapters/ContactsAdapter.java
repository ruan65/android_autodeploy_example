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
import ru.mess.messenger.activities.chat.P2PActivity;
import ru.mess.messenger.models.User;
import ru.mess.messenger.tools.Utils;

/**
 * Created by Shugig on 11/10/2017.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {


    Context context;
    ArrayList<User> contacts = new ArrayList<>();
    User currentUser;

    public ContactsAdapter(ArrayList<User> people, Context context, User currentUser)
    {
        this.contacts = people;
        this.context = context;
        this.currentUser = currentUser;
    }


    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item,parent,false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView restoImage;
        TextView name, role, message;



        public ViewHolder(View itemView) {
            super(itemView);

            restoImage = (ImageView) itemView.findViewById(R.id.profile_image);
            name = (TextView) itemView.findViewById(R.id.name);
            role  = (TextView) itemView.findViewById(R.id.role);
            message  = (TextView) itemView.findViewById(R.id.message);
        }
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.ViewHolder holder, int position) {


        final User user = contacts.get(position);

        holder.name.setText(Utils.capitalizeFirst(user.getLogin()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, P2PActivity.class);
                intent.putExtra("currentUser",currentUser);
                intent.putExtra("selectedUser",user);
                context.startActivity(intent);

            }
        });





    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void addContact(User user)
    {
        if (!user.getId().equals(currentUser.getId())) {
            contacts.add(user);
        }
    }


    public ArrayList<User> getContacts() {
        return contacts;
    }
}
