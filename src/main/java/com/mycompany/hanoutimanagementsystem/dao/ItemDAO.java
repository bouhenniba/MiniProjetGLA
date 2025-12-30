package com.mycompany.hanoutimanagementsystem.dao;

import com.mycompany.hanoutimanagementsystem.model.Item;
import com.mycompany.hanoutimanagementsystem.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class ItemDAO implements InterfaceItemDAO {

    @Override
    public void create(Item item) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(item);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Item item) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(item);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Long sku) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Item item = em.find(Item.class, sku);
            if (item != null) em.remove(item);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public Item findBySku(Long sku) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // ✅ تحميل الصنف مع الموردين والقسم
            TypedQuery<Item> query = em.createQuery(
                "SELECT DISTINCT i FROM Item i " +
                "LEFT JOIN FETCH i.section " +
                "LEFT JOIN FETCH i.vendors " +
                "WHERE i.sku = :sku",
                Item.class
            );
            query.setParameter("sku", sku);
            List<Item> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Item> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // ✅ تحميل القسم والموردين مع الأصناف
            return em.createQuery(
                "SELECT DISTINCT i FROM Item i " +
                "LEFT JOIN FETCH i.section " +
                "LEFT JOIN FETCH i.vendors", 
                Item.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Item> findBySection(String sectionCode) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // ✅ تحميل الموردين أيضاً
            TypedQuery<Item> query = em.createQuery(
                "SELECT DISTINCT i FROM Item i " +
                "LEFT JOIN FETCH i.vendors " +
                "WHERE i.section.code = :code",
                Item.class
            );
            query.setParameter("code", sectionCode);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Item> findByVendor(String licenseNumber) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // ✅ تحميل القسم والموردين
            TypedQuery<Item> query = em.createQuery(
                "SELECT DISTINCT i FROM Item i " +
                "LEFT JOIN FETCH i.section " +
                "LEFT JOIN FETCH i.vendors v " +
                "WHERE v.licenseNumber = :license",
                Item.class
            );
            query.setParameter("license", licenseNumber);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Item> findItemsByVendor(String licenseNumber) {
        return findByVendor(licenseNumber);
    }
}