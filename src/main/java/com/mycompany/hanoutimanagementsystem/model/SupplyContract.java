package com.mycompany.hanoutimanagementsystem.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "supply_contract")
public class SupplyContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_sku")
    private Item item;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vendor_license")
    private Vendor vendor;

    @Column(name = "supply_price", precision = 10, scale = 2)
    private BigDecimal supplyPrice;

    public SupplyContract() {}

    public SupplyContract(Item item, Vendor vendor, BigDecimal supplyPrice) {
        this.item = item;
        this.vendor = vendor;
        this.supplyPrice = supplyPrice;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public Vendor getVendor() { return vendor; }
    public void setVendor(Vendor vendor) { this.vendor = vendor; }

    public BigDecimal getSupplyPrice() { return supplyPrice; }
    public void setSupplyPrice(BigDecimal supplyPrice) { this.supplyPrice = supplyPrice; }
    
    @Override
    public String toString() {
        return "Contract: " + vendor.getContactName() + " supplies " + item.getName() + " at " + supplyPrice;
    }
}