package pecia.socialmap;

import java.util.Date;

/**
 * Created by Francesco on 15/06/17.
 */

public class ChatMess {

    private String messText;
    private String messUser;
    private long messTime;
    private String topic;
    private String UserId;

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public ChatMess(String messText, String messUser, String userId, String topic) {
        this.messText = messText;
        this.messUser = messUser;
        this.topic = topic;
        this.UserId = userId;

        messTime = new Date().getTime();
    }

    public ChatMess() {
    }

    public String getMessText() {
        return messText;
    }

    public void setMessText(String messText) {
        this.messText = messText;
    }

    public String getMessUser() {
        return messUser;
    }

    public void setMessUser(String messUser) {
        this.messUser = messUser;
    }

    public long getMessTime() {
        return messTime;
    }

    public void setMessTime(long messTime) {
        this.messTime = messTime;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
