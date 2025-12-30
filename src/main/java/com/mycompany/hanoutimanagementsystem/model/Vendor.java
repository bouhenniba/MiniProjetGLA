package com.mycompany.hanoutimanagementsystem.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Vendor {

    @Id
    @Column(name = "LICENSE_NUMBER")
    private String licenseNumber;

    @Column(name = "VENDOR_NAME") // اسم المورد
    private String vendorName;

    @Column(name = "CONTACT_NAME")
    private String contactName;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<SupplyContract> providedItems = new HashSet<>();

    // Constructors
    public Vendor() {}
    public Vendor(String licenseNumber, String vendorName, String contactName) {
        this.licenseNumber = licenseNumber;
        this.vendorName = vendorName;
        this.contactName = contactName;
    }

    // Getters and Setters
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public Set<SupplyContract> getProvidedItems() { return providedItems; }
    public void setProvidedItems(Set<SupplyContract> providedItems) { this.providedItems = providedItems; }
    
    @Override
    public String toString() {
        return vendorName + " (" + licenseNumber + ")";
    }
}