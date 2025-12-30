package com.mycompany.hanoutimanagementsystem.dao;

import com.mycompany.hanoutimanagementsystem.model.Item;
import java.util.List;

public interface InterfaceItemDAO {

    // ===== CRUD Operations =====
    void create(Item item);                  // إضافة عنصر
    void update(Item item);                  // تعديل عنصر
    void delete(Long sku);                   // حذف عنصر بواسطة SKU
    Item findBySku(Long sku);                // البحث عن عنصر بواسطة SKU
    List<Item> findAll();                    // عرض كل العناصر

    // ===== Operational Scenarios =====
    List<Item> findBySection(String sectionCode);       // عرض العناصر حسب القسم
    List<Item> findByVendor(String licenseNumber);      // عرض العناصر التي يوفرها مورد معين
    List<Item> findItemsByVendor(String licenseNumber); // بديل أكثر وضوحًا للسيناريو
}