package com.mike.sim;

public class Item {
    static private long nextId = 1;


    private long id = nextId++;
    private String supplierId;
    private String description;

    public Item(Supplier supplier, String description) {
        this.supplierId = supplier.getID();
        this.description = description;
    }

    public long getId() {
        return id;
    }
}
