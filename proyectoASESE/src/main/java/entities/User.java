package entities;

import java.util.Objects;

public class User {
    private int id;
    private String name;
    private String surname;
    private String email;
    private String emailToken;
    private String password;
    private UserRol rol;

    public enum UserRol {
        ADMIN,
        USER,
    }

    public User(int id, String name, String surname, String email, String emailToken, String password, UserRol rol) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.emailToken = emailToken;
        this.password = password;
        this.rol = rol;
    }

    // TODO: Add tests for this
    public String getFtpUsername() {
        var index = email.lastIndexOf("@");
        if (index < 1) {
            throw new RuntimeException("Could not convert email to ftp username");
        }
        return email.substring(0, index);
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public UserRol getRol() {
        return rol;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRol(UserRol rol) {
        this.rol = rol;
    }

    public String getEmailToken() {
        return emailToken;
    }

    public void setEmailToken(String emailToken) {
        this.emailToken = emailToken;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", emailToken='" + emailToken + '\'' +
                ", password='" + password + '\'' +
                ", rol=" + rol +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(name, user.name) && Objects.equals(surname, user.surname) && Objects.equals(email, user.email) && Objects.equals(emailToken, user.emailToken) && Objects.equals(password, user.password) && rol == user.rol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, email, emailToken, password, rol);
    }
}