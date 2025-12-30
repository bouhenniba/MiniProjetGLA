package com.mycompany.hanoutimanagementsystem.controller;

import com.mycompany.hanoutimanagementsystem.dao.InterfaceItemDAO;
import com.mycompany.hanoutimanagementsystem.dao.InterfaceSectionDAO;
import com.mycompany.hanoutimanagementsystem.dao.InterfaceVendorDAO;
import com.mycompany.hanoutimanagementsystem.model.Item;
import com.mycompany.hanoutimanagementsystem.model.Section;
import com.mycompany.hanoutimanagementsystem.model.Vendor;

import java.math.BigDecimal;
import java.util.List;

/**
 * متحكم الأصناف - يدير عمليات CRUD والعلاقات
 */
public class ItemController {

    private final InterfaceItemDAO itemDAO;
    private final InterfaceSectionDAO sectionDAO;
    private final InterfaceVendorDAO vendorDAO;

    public ItemController(InterfaceItemDAO itemDAO,
                          InterfaceSectionDAO sectionDAO,
                          InterfaceVendorDAO vendorDAO) {
        this.itemDAO = itemDAO;
        this.sectionDAO = sectionDAO;
        this.vendorDAO = vendorDAO;
    }

    // ================= CRUD Operations =================

    public void createItem(Long sku, String name, int stock, 
                          BigDecimal price, String sectionCode) {
        Section section = sectionDAO.findByCode(sectionCode);
        if (section == null) {
            throw new IllegalArgumentException(
                "Section not found: " + sectionCode
            );
        }

        Item item = new Item(sku, name, stock, price);
        item.setSection(section);
        
        itemDAO.create(item);
    }

    public void updateItem(Item item) {
        if (item.getSection() == null) {
            throw new IllegalArgumentException("Item must have a section");
        }
        itemDAO.update(item);
    }

    public void deleteItem(Long sku) {
        itemDAO.delete(sku);
    }

    public Item findItem(Long sku) {
        return itemDAO.findBySku(sku);
    }

    public List<Item> getAllItems() {
        return itemDAO.findAll();
    }

    // ================= Vendor Management =================

    /**
     * ✅ ربط صنف بمورد (إضافة مورد لصنف) - محسّن
     */
    public void addVendorToItem(Long itemSku, String vendorLicense) {
        // ✅ استخدام findBySku المحسّن الذي يحمّل الموردين
        Item item = itemDAO.findBySku(itemSku);
        Vendor vendor = vendorDAO.findByLicense(vendorLicense);
        
        if (item == null) {
            throw new IllegalArgumentException("Item not found: " + itemSku);
        }
        if (vendor == null) {
            throw new IllegalArgumentException("Vendor not found: " + vendorLicense);
        }
        
        // ✅ التحقق من عدم وجود علاقة مسبقة
        if (item.getVendors().contains(vendor)) {
            throw new IllegalArgumentException(
                "Vendor already linked to this item"
            );
        }
        
        // ✅ إضافة العلاقة في كلا الاتجاهين
        item.addVendor(vendor);
        
        // ✅ حفظ التغييرات
        itemDAO.update(item);
        
        System.out.println("✅ تم ربط المورد " + vendorLicense + 
                         " بالصنف " + itemSku + " بنجاح");
    }

    /**
     * ✅ إزالة مورد من صنف - محسّن
     */
    public void removeVendorFromItem(Long itemSku, String vendorLicense) {
        Item item = itemDAO.findBySku(itemSku);
        Vendor vendor = vendorDAO.findByLicense(vendorLicense);
        
        if (item == null || vendor == null) {
            throw new IllegalArgumentException("Item or Vendor not found");
        }
        
        // ✅ التحقق من وجود علاقة
        if (!item.getVendors().contains(vendor)) {
            throw new IllegalArgumentException(
                "Vendor is not linked to this item"
            );
        }
        
        item.removeVendor(vendor);
        itemDAO.update(item);
        
        System.out.println("✅ تم إزالة المورد " + vendorLicense + 
                         " من الصنف " + itemSku);
    }

    // ================= Operational Scenarios =================

    /**
     * عرض جميع العناصر التابعة لقسم معين
     */
    public List<Item> getItemsBySection(String sectionCode) {
        return itemDAO.findBySection(sectionCode);
    }

    /**
     * عرض جميع العناصر التي يوفرها مورد معين
     */
    public List<Item> getItemsByVendor(String licenseNumber) {
        return itemDAO.findByVendor(licenseNumber);
    }
    
    /**
     * ✅ التحقق من العلاقة بين صنف ومورد
     */
    public boolean isVendorLinkedToItem(Long itemSku, String vendorLicense) {
        Item item = itemDAO.findBySku(itemSku);
        if (item == null) return false;
        
        return item.getVendors().stream()
            .anyMatch(v -> v.getLicenseNumber().equals(vendorLicense));
    }
}