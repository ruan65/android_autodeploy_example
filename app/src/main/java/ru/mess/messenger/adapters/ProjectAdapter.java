package ru.mess.messenger.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.mess.messenger.R;
import ru.mess.messenger.models.Project;

/**
 * Created by Shugig on 11/10/2017.
 */

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {


    ArrayList<Project> projects = new ArrayList<>();

    public ProjectAdapter(ArrayList<Project> projects)
    {
        this.projects = projects;
    }
    @Override
    public ProjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item,parent,false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView restoImage;
        TextView name, participants, message;



        public ViewHolder(View itemView) {
            super(itemView);

            restoImage = (ImageView) itemView.findViewById(R.id.avatar_image);
            name = (TextView) itemView.findViewById(R.id.name);
            participants  = (TextView) itemView.findViewById(R.id.project_author);
            message  = (TextView) itemView.findViewById(R.id.message);
        }
    }

    @Override
    public void onBindViewHolder(ProjectAdapter.ViewHolder holder, int position) {

        //bind values here

    }

    @Override
    public int getItemCount() {
        return projects.size();
    }


    public void addProject(Project project)
    {
        projects.add(project);
    }


    public ArrayList<Project> getProjects() {
        return projects;
    }
}
