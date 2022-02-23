package ch.uzh.ifi.hase.soprafs22.rest.dto;

import java.util.Date;

public class UserPutDTO {

    private String username;

    private Date birthday;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getBirthday() {
        return birthday;
    }
}
