package com.mycompany.hanoutimanagementsystem.controller;

import com.mycompany.hanoutimanagementsystem.dao.InterfaceVendorDAO;
import com.mycompany.hanoutimanagementsystem.dao.InterfaceItemDAO;
import com.mycompany.hanoutimanagementsystem.model.Vendor;
import com.mycompany.hanoutimanagementsystem.model.Item;

import java.util.List;

public class VendorController {

    private final InterfaceVendorDAO vendorDAO;
    private final InterfaceItemDAO itemDAO;

    // ✅ Constructor Injection (تطبيق كامل لـ DIP)
    public VendorController(InterfaceVendorDAO vendorDAO,
                            InterfaceItemDAO itemDAO) {
        this.vendorDAO = vendorDAO;
        this.itemDAO = itemDAO;
    }

    // ===== CRUD Operations =====

    public void createVendor(String licenseNumber, String contactName) {
        Vendor vendor = new Vendor();
        vendor.setLicenseNumber(licenseNumber);
        vendor.setContactName(contactName);

        vendorDAO.create(vendor);
    }

    public void updateVendor(Vendor vendor) {
        vendorDAO.update(vendor);
    }

    public void deleteVendor(String licenseNumber) {
        vendorDAO.delete(licenseNumber);
    }

    public Vendor findVendor(String licenseNumber) {
        return vendorDAO.findByLicense(licenseNumber);
    }

    public List<Vendor> getAllVendors() {
        return vendorDAO.findAll();
    }

    // ===== Operational Scenarios =====

    /**
     * سيناريو:
     * مقارنة الموردين الذين يوفرون نفس الصنف
     */
    public List<Vendor> getVendorsByItem(Long sku) {
        return vendorDAO.findByItemSku(sku);
    }

    /**
     * سيناريو:
     * عرض كتالوج مورد معين
     */
    public List<Item> getItemsByVendor(String licenseNumber) {
        return itemDAO.findByVendor(licenseNumber);
    }
}