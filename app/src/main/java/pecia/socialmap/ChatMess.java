package pecia.socialmap;

import java.util.Date;

/**
 * Created by Francesco on 15/06/17.
 */

public class ChatMess {

    private String messText;
    private String messUser;
    private long messTime;

    public ChatMess(String messText, String messUser) {
        this.messText = messText;
        this.messUser = messUser;
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
}
