package com.mycompany.hanoutimanagementsystem.controller;

import com.mycompany.hanoutimanagementsystem.dao.InterfaceSectionDAO;
import com.mycompany.hanoutimanagementsystem.model.Section;
import java.util.List;

public class SectionController {

    private final InterfaceSectionDAO sectionDAO;

    // Constructor Injection (SOLID - DIP)
    public SectionController(InterfaceSectionDAO sectionDAO) {
        this.sectionDAO = sectionDAO;
    }

    // ===== CRUD Operations =====

    public void createSection(String code, String label) {
        Section section = new Section();
        section.setCode(code);
        section.setLabel(label); // ✅ التصحيح هنا

        sectionDAO.create(section);
    }

    public void updateSection(Section section) {
        sectionDAO.update(section);
    }

    public void deleteSection(String code) {
        sectionDAO.delete(code);
    }

    public Section findSection(String code) {
        return sectionDAO.findByCode(code);
    }

    public List<Section> getAllSections() {
        return sectionDAO.findAll();
    }
}

