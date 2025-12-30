package com.mycompany.hanoutimanagementsystem.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Section {

    @Id
    private String code;
    
    private String label;

    // ✅ إصلاح Generic Type + تحديد Fetch Type
    @OneToMany(
        mappedBy = "section",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private Set<Item> items = new HashSet<>();

    // Constructors
    public Section() {}
    
    public Section(String code, String label) {
        this.code = code;
        this.label = label;
    }

    // Getters & Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Set<Item> getItems() { return items; }

    // ✅ Factory Method محدّث مع السعر
    public Item addItem(Long sku, String name, int stock, BigDecimal price) {
        Item item = new Item(sku, name, stock, price);
        item.setSection(this);
        items.add(item);
        return item;
    }

    public void removeItem(Item item) {
        items.remove(item);
        item.setSection(null);
    }
    
    @Override
    public String toString() {
        return code + " - " + label;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        return code != null && code.equals(section.getCode());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}