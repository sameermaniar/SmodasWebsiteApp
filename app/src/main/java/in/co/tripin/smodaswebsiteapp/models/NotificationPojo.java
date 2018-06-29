package in.co.tripin.smodaswebsiteapp.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class NotificationPojo {
    private String mTitle;
    private String mMessage;
    @ServerTimestamp
    private Date mTimeStamp;

    private String mUrl;

    public NotificationPojo() {
    }

    public NotificationPojo(String mTitle, String mMessage, String mUrl) {
        this.mTitle = mTitle;
        this.mMessage = mMessage;
        this.mUrl = mUrl;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public Date getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(Date mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }
}
