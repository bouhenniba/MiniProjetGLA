package com.mycompany.hanoutimanagementsystem.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Item {

    @Id
    private Long sku; 
    
    private String name;
    
    private int stock;
    
    // ✅ إضافة حقل السعر
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "section_code")
    private Section section;

    // ✅ تحديد Fetch Type بوضوح
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "item_vendor",
        joinColumns = @JoinColumn(name = "item_sku"),
        inverseJoinColumns = @JoinColumn(name = "vendor_license")
    )
    private Set<Vendor> vendors = new HashSet<>();

    // Constructors
    public Item() {}
    
    public Item(Long sku, String name, int stock) {
        this.sku = sku;
        this.name = name;
        this.stock = stock;
    }
    
    // ✅ Constructor مع السعر
    public Item(Long sku, String name, int stock, BigDecimal price) {
        this.sku = sku;
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    // Getters & Setters
    public Long getSku() { return sku; }
    public void setSku(Long sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    
    // ✅ Price Getter & Setter
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Section getSection() { return section; }
    public void setSection(Section section) { this.section = section; }

    public Set<Vendor> getVendors() { return vendors; }
    public void setVendors(Set<Vendor> vendors) { this.vendors = vendors; }
    
    // ✅ Helper methods لإدارة العلاقة Many-to-Many
    public void addVendor(Vendor vendor) {
        this.vendors.add(vendor);
        vendor.getItems().add(this);
    }
    
    public void removeVendor(Vendor vendor) {
        this.vendors.remove(vendor);
        vendor.getItems().remove(this);
    }
    
    @Override
    public String toString() {
        return sku + " - " + name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return sku != null && sku.equals(item.getSku());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}