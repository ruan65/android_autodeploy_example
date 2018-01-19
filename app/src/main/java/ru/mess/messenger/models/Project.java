package ru.mess.messenger.models;

import org.joda.time.DateTime;

/**
 * Created by Shugig on 11/9/2017.
 */

public class Project {

    private String id;
    private String name;
    private String description;
    private DateTime cdate;
    private String author;
    private int autherId;


    public Project() {
    }

    public Project(String id, String name, String description, DateTime cdate, String author, int autherId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.cdate = cdate;
        this.author = author;
        this.autherId = autherId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public DateTime getCdate() {
        return cdate;
    }

    public void setCdate(DateTime cdate) {
        this.cdate = cdate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getAutherId() {
        return autherId;
    }

    public void setAutherId(int autherId) {
        this.autherId = autherId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        if (autherId != project.autherId) return false;
        if (id != null ? !id.equals(project.id) : project.id != null) return false;
        if (name != null ? !name.equals(project.name) : project.name != null) return false;
        if (description != null ? !description.equals(project.description) : project.description != null)
            return false;
        if (cdate != null ? !cdate.equals(project.cdate) : project.cdate != null) return false;
        return author != null ? author.equals(project.author) : project.author == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (cdate != null ? cdate.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + autherId;
        return result;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", cdate=" + cdate +
                ", author='" + author + '\'' +
                ", autherId=" + autherId +
                '}';
    }
}
