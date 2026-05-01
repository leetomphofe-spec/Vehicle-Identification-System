package com.vis.model;

import javafx.beans.property.*;

/**
 * Customer model - extends BaseEntity (Inheritance).
 */
public class Customer extends BaseEntity {

    private final StringProperty name    = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();
    private final StringProperty phone   = new SimpleStringProperty();
    private final StringProperty email   = new SimpleStringProperty();

    public Customer() { super(); }

    public Customer(int id, String name, String address, String phone, String email) {
        super(id);
        setName(name);
        setAddress(address);
        setPhone(phone);
        setEmail(email);
    }

    public StringProperty nameProperty()    { return name; }
    public StringProperty addressProperty() { return address; }
    public StringProperty phoneProperty()   { return phone; }
    public StringProperty emailProperty()   { return email; }

    public String getName()    { return name.get(); }
    public String getAddress() { return address.get(); }
    public String getPhone()   { return phone.get(); }
    public String getEmail()   { return email.get(); }

    public void setName(String v)    { name.set(v); }
    public void setAddress(String v) { address.set(v); }
    public void setPhone(String v)   { phone.set(v); }
    public void setEmail(String v)   { email.set(v); }

    @Override
    public String getDisplayInfo() {
        return String.format("[Customer] %s | %s | %s", getName(), getPhone(), getEmail());
    }
}
