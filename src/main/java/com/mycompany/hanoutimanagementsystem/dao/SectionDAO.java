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
    try {
        // âœ… ØªØ­Ù…ÙŠÙ„ Ø§Ù„Ø£ØµÙ†Ø§Ù Ù…Ø¹ Ø§Ù„Ø£Ù‚Ø³Ø§Ù… (JOIN FETCH)
        return em.createQuery(
            "SELECT DISTINCT s FROM Section s LEFT JOIN FETCH s.items", 
            Section.class
        ).getResultList();
    } finally {
        em.close();
    }
}

    // ÙŠÙ…ÙƒÙ† Ø¥Ø¶Ø§ÙØ© Ù…ÙŠØ«ÙˆØ¯Ø² Ø®Ø§ØµØ© Ø¨Ø§Ù„Ø³ÙŠÙ†Ø§Ø±ÙŠÙˆÙ‡Ø§Øª Ø§Ù„ØªØ´ØºÙŠÙ„ÙŠØ© Ù„Ø§Ø­Ù‚Ù‹Ø§ Ø¥Ø°Ø§ Ø§Ø­ØªØ§Ø¬ Ø§Ù„Ù…Ø´Ø±ÙˆØ¹
}