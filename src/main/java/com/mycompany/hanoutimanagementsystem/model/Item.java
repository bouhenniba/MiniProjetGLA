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

    // ✅ تم استبدال ManyToMany بـ OneToMany للعقد الوسيط
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<SupplyContract> vendorSupplies = new HashSet<>();

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

    public Set<SupplyContract> getVendorSupplies() { return vendorSupplies; }
    public void setVendorSupplies(Set<SupplyContract> vendorSupplies) { this.vendorSupplies = vendorSupplies; }
    
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