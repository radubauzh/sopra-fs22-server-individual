package ch.uzh.ifi.hase.soprafs22.rest.dto;

import java.time.LocalDate;
import java.util.Date;

public class UserPutDTO {

    private String username;

    private LocalDate birthday;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public LocalDate getBirthday() {
        return birthday;
    }
}
