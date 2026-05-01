package com.vis.model;

/**
 * BaseEntity - Abstract base class for all models.
 * Demonstrates INHERITANCE used by Vehicle, Customer, etc.
 */
public abstract class BaseEntity {
    protected int id;

    public BaseEntity() {}

    public BaseEntity(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    /**
     * Polymorphic method - each subclass provides its own display string.
     */
    public abstract String getDisplayInfo();

    @Override
    public String toString() {
        return getDisplayInfo();
    }
}
