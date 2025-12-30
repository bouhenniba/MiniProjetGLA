package com.mycompany.hanoutimanagementsystem.dao;

import com.mycompany.hanoutimanagementsystem.model.Section;
import java.util.List;

public interface InterfaceSectionDAO {

    // ===== CRUD Operations =====
    void create(Section section);          // إضافة قسم
    void update(Section section);          // تعديل قسم
    void delete(String code);              // حذف قسم بواسطة Code
    Section findByCode(String code);       // البحث عن قسم بواسطة Code
    List<Section> findAll();               // عرض كل الأقسام

    // ===== Operational Scenarios =====
    // يمكن إضافة ميثودز إضافية إذا احتاجت الواجهة إلى تقارير معينة خاصة بالأقسام
}