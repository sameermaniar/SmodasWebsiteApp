package in.co.tripin.smodaswebsiteapp.models;

public class UserPojo {

    private String mUserFullName;
    private String mUserEmail;
    private String mUserMobile;
    private String mUserPassword;

    public UserPojo() {
    }

    public UserPojo(String mUserFullName, String mUserEmail, String mUserMobile, String mUserPassword) {
        this.mUserFullName = mUserFullName;
        this.mUserEmail = mUserEmail;
        this.mUserMobile = mUserMobile;
        this.mUserPassword = mUserPassword;
    }

    public String getmUserFullName() {
        return mUserFullName;
    }

    public void setmUserFullName(String mUserFullName) {
        this.mUserFullName = mUserFullName;
    }

    public String getmUserEmail() {
        return mUserEmail;
    }

    public void setmUserEmail(String mUserEmail) {
        this.mUserEmail = mUserEmail;
    }

    public String getmUserMobile() {
        return mUserMobile;
    }

    public void setmUserMobile(String mUserMobile) {
        this.mUserMobile = mUserMobile;
    }

    public String getmUserPassword() {
        return mUserPassword;
    }

    public void setmUserPassword(String mUserPassword) {
        this.mUserPassword = mUserPassword;
    }
}
