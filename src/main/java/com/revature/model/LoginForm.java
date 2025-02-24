package com.revature.model;

import java.util.Objects;

public class LoginForm {

    private String username;
    private String password;

    public LoginForm() {
    }

    public LoginForm(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        return Objects.hash(password, username);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof LoginForm)) {
            return false;
        }
        LoginForm other = (LoginForm) obj;
        return Objects.equals(password, other.password) && Objects.equals(username, other.username);
    }

}
