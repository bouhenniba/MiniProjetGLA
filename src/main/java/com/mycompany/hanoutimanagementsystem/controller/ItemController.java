package com.mycompany.hanoutimanagementsystem.controller;

import com.mycompany.hanoutimanagementsystem.dao.InterfaceItemDAO;
import com.mycompany.hanoutimanagementsystem.dao.InterfaceSectionDAO;
import com.mycompany.hanoutimanagementsystem.model.Item;
import com.mycompany.hanoutimanagementsystem.model.Section;

import java.util.List;

public class ItemController {

    private final InterfaceItemDAO itemDAO;
    private final InterfaceSectionDAO sectionDAO;

    // ✅ Dependency Injection عبر المشيد (Constructor Injection)
    public ItemController(InterfaceItemDAO itemDAO,
                          InterfaceSectionDAO sectionDAO) {
        this.itemDAO = itemDAO;
        this.sectionDAO = sectionDAO;
    }

    // ================= CRUD Operations =================

    /**
     * إنشاء عنصر جديد وربطه بقسمه
     * يعتمد على Factory Method داخل Section
     */
    public void createItem(Long sku, String name, int stock, String sectionCode) {

        Section section = sectionDAO.findByCode(sectionCode);
        if (section == null) {
            throw new IllegalArgumentException(
                "Section not found: " + sectionCode
            );
        }

        // ✅ القسم هو من يصنع العنصر (Factory Method)
        Item item = section.addItem(sku, name, stock);

        // حفظ العنصر في قاعدة البيانات
        itemDAO.create(item);
    }

    public void updateItem(Item item) {
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
}
