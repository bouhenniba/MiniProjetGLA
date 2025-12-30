package com.mycompany.hanoutimanagementsystem.dao;

import com.mycompany.hanoutimanagementsystem.model.Vendor;
import java.util.List;

public interface InterfaceVendorDAO {

    // ===== CRUD Operations =====
    void create(Vendor vendor);                  // إضافة مورد

    void update(Vendor vendor);                  // تعديل مورد

    void delete(String licenseNumber);           // حذف مورد بواسطة License Number

    Vendor findByLicense(String licenseNumber);  // البحث عن مورد بواسطة License Number

    List<Vendor> findAll();                      // عرض كل الموردين

    // ===== Operational Scenarios =====
    List<Vendor> findByItemSku(Long sku);        // جميع الموردين الذين يوفرون صنفًا معينًا (لمقارنة التوريد)
}