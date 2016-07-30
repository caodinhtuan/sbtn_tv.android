package com.sbtn.androidtv.request.datarequest;

import com.sbtn.androidtv.utils.StringUtils;

public class FacebookRegisterResource {

    private transient String firstName;
    private transient String lastName;
    private String fullName;
    private String emailAddress;
    private String avatar;
    private String facebookToken;
    private String facebookID;

    public boolean isValid() {
        return StringUtils.isNotEmpty(firstName) && StringUtils.isNotEmpty(lastName) &&
                StringUtils.isNotEmpty(facebookID) && StringUtils.isNotEmpty(facebookToken);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        if (fullName != null) return fullName;

        if (StringUtils.isNotEmpty(firstName)) {
            fullName = firstName;
        }

        if (StringUtils.isNotEmpty(lastName)) {
            if (StringUtils.isNotEmpty(fullName)) {
                fullName += " ";
            }

            fullName += lastName;
        }
        return fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFacebookToken() {
        return facebookToken;
    }

    public void setFacebookToken(String facebookToken) {
        this.facebookToken = facebookToken;
    }

    public String getFacebookID() {
        return facebookID;
    }

    public void setFacebookID(String facebookID) {
        this.facebookID = facebookID;
    }

    @Override
    public String toString() {
        return "@firstName=" + firstName + " @lastName=" + lastName + " @facebookID" + facebookID +
                " @facebookToken" + facebookToken;
    }
}
