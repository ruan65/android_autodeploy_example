package ru.mess.messenger.models;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by Shugig on 11/10/2017.
 */

public class Group implements Serializable{

    private Long id;

    private String name;
    private String description;


    private Instance instance;


    private Project project;


    private Set<Message> messages;

    public Group(Long id, String name, String description, Instance instance, Project project, Set<Message> messages) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.instance = instance;
        this.project = project;
        this.messages = messages;
    }

    public Group() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        if (id != null ? !id.equals(group.id) : group.id != null) return false;
        if (name != null ? !name.equals(group.name) : group.name != null) return false;
        if (description != null ? !description.equals(group.description) : group.description != null)
            return false;
        if (instance != null ? !instance.equals(group.instance) : group.instance != null)
            return false;
        if (project != null ? !project.equals(group.project) : group.project != null) return false;
        return messages != null ? messages.equals(group.messages) : group.messages == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (instance != null ? instance.hashCode() : 0);
        result = 31 * result + (project != null ? project.hashCode() : 0);
        result = 31 * result + (messages != null ? messages.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProjectGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", instance=" + instance +
                ", project=" + project +
                ", messages=" + messages +
                '}';
    }
}
