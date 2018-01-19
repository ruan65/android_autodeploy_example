package ru.mess.messenger.models;

import org.joda.time.DateTime;

import java.util.Calendar;

/**
 * Created by neliousness on 12/10/17.
 */

public class Message {

    long id;
    String senderName;
    String senderId;
    String content;
    boolean sent;
    boolean recieved;
    String sendtime;

    public Message(long id, String senderName, String senderId, boolean sent, boolean recieved, String content) {
        this.id = id;
        this.senderName = senderName;
        this.senderId = senderId;
        this.sent = sent;
        this.recieved = recieved;
        DateTime time = new DateTime(Calendar.getInstance());
        this.sendtime = "";
        this.content = content;
    }

    public Message() {
        //DateTime time = new DateTime(Calendar.getInstance());
        this.sendtime = null;// time.toDateTime();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public boolean isRecieved() {
        return recieved;
    }

    public void setRecieved(boolean recieved) {
        this.recieved = recieved;
    }


    public String getSendtime() {
        return sendtime;
    }

    public void setSendtime(String sendtime) {
        this.sendtime = sendtime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message that = (Message) o;

        if (id != that.id) return false;
        if (sent != that.sent) return false;
        if (recieved != that.recieved) return false;
        if (senderName != null ? !senderName.equals(that.senderName) : that.senderName != null)
            return false;
        if (senderId != null ? !senderId.equals(that.senderId) : that.senderId != null)
            return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        return sendtime != null ? sendtime.equals(that.sendtime) : that.sendtime == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (senderName != null ? senderName.hashCode() : 0);
        result = 31 * result + (senderId != null ? senderId.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (sent ? 1 : 0);
        result = 31 * result + (recieved ? 1 : 0);
        result = 31 * result + (sendtime != null ? sendtime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", senderName='" + senderName + '\'' +
                ", senderId='" + senderId + '\'' +
                ", content='" + content + '\'' +
                ", sent=" + sent +
                ", recieved=" + recieved +
                ", sendtime='" + sendtime + '\'' +
                '}';
    }
}
