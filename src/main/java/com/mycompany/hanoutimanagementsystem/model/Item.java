package com.mycompany.hanoutimanagementsystem.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Item {

    @Id
    private Long sku; 
    private String name;
    private int stock;

    @ManyToOne(optional = false) // ❌ لا يمكن أن يكون null
    @JoinColumn(name = "section_code")
    private Section section;

    @ManyToMany
    @JoinTable(
        name = "item_vendor",
        joinColumns = @JoinColumn(name = "item_sku"),
        inverseJoinColumns = @JoinColumn(name = "vendor_license")
    )
    private Set<Vendor> vendors = new HashSet<>();

    public Item() {}
    public Item(Long sku, String name, int stock) {
        this.sku = sku;
        this.name = name;
        this.stock = stock;
    }

    // Getters & Setters
    public Long getSku() { return sku; }
    public void setSku(Long sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public Section getSection() { return section; }
    public void setSection(Section section) { this.section = section; }

    public Set<Vendor> getVendors() { return vendors; }
    public void setVendors(Set<Vendor> vendors) { this.vendors = vendors; }
}
