package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;

import java.time.LocalDate;
import java.util.Date;

public class UserGetDTO {

    private Long id;
    private String password;
    private String username;
    private Boolean status;
    private LocalDate creationDate;
    private LocalDate birthday;
    private String token;


    public LocalDate getBirthday() { return birthday; }


    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    // shows as not used but if its not here it will not set the date
    public LocalDate getCreationDate() { return creationDate; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /*
        public String getPassword() { return password; }
    **/
        public void setPassword(String password) {
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }
    /*
        public String getToken() {
            return token;
        }
    **/
    public void setToken(String token) {
        this.token = token;
    }
}