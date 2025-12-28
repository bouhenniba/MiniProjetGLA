package com.mycompany.hanoutimanagementsystem.dao;

import com.mycompany.hanoutimanagementsystem.model.Section;
import com.mycompany.hanoutimanagementsystem.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class SectionDAO implements InterfaceSectionDAO {

    @Override
    public void create(Section section) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(section);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void update(Section section) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(section);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void delete(String code) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        Section section = em.find(Section.class, code);
        if (section != null) em.remove(section);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Section findByCode(String code) {
        EntityManager em = JPAUtil.getEntityManager();
        Section section = em.find(Section.class, code);
        em.close();
        return section;
    }

    @Override
    public List<Section> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        TypedQuery<Section> query = em.createQuery("SELECT s FROM Section s", Section.class);
        List<Section> sections = query.getResultList();
        em.close();
        return sections;
    }

    // يمكن إضافة ميثودز خاصة بالسيناريوهات التشغيلية لاحقًا إذا احتاج المشروع
}
