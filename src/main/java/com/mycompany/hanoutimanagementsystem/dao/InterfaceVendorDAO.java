package com.mycompany.hanoutimanagementsystem.dao;

import com.mycompany.hanoutimanagementsystem.model.Vendor;
import com.mycompany.hanoutimanagementsystem.model.SupplyContract;
import java.util.List;

public interface InterfaceVendorDAO {

    // ===== CRUD Operations =====
    void create(Vendor vendor);                  // إضافة مورد

    void update(Vendor vendor);                  // تعديل مورد

    void delete(String licenseNumber);           // حذف مورد بواسطة License Number

    Vendor findByLicense(String licenseNumber);  // البحث عن مورد بواسطة License Number

    List<Vendor> findAll();                      // عرض كل الموردين

    // ===== Operational Scenarios =====
    List<SupplyContract> findByItemSku(Long sku);        // جميع عقود التوريد لصنف معين (لمقارنة التوريد)
}