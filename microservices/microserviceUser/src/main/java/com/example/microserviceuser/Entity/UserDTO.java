package com.example.microserviceuser.Entity;

public class UserDTO {
private  Long id;
    private String token;

    private String role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String username;
    private String email;
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }





        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }


}