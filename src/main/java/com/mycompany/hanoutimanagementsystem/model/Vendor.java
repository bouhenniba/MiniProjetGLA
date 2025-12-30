package com.mycompany.hanoutimanagementsystem.model;


import com.mycompany.hanoutimanagementsystem.model.Item;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Vendor {

    @Id
    @Column(name = "LICENSE_NUMBER") // الربط مع عمود قاعدة البيانات
    private String licenseNumber; // رقم الرخصة الفريد

    @Column(name = "CONTACT_NAME")
    private String contactName;   // اسم جهة الاتصال

    @ManyToMany(mappedBy = "vendors")
    private Set<Item> items = new HashSet<>();

    // Constructors
    public Vendor() {}
    public Vendor(String licenseNumber, String contactName) {
        this.licenseNumber = licenseNumber;
        this.contactName = contactName;
    }

    // Getters and Setters
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public Set<Item> getItems() { return items; }
    public void setItems(Set<Item> items) { this.items = items; }
    @Override
public String toString() {
    return licenseNumber + " - " + contactName;
}
}