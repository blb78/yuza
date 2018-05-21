package com.skillogs.yuza.domain.account;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class Account {
    @Id
    private String id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String city;
    private String country;
    private Role role;
    private long birthday;
    private String picture;
    private boolean enabled = true;
    private boolean locked = false;
    private long createdAt  = System.currentTimeMillis();

    public Account(String email) {
        this.email = email;
    }

    public Account() {
        // NOTE : for bean convention
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {this.id = id; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", email=" + email +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", birthday=" + lastName +
                ", city=" + city +
                ", country=" + country +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        return email.equals(account.email);
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
