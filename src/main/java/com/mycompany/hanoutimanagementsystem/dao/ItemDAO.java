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
        em.getTransaction().begin();
        em.persist(item);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void update(Item item) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(item);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void delete(Long sku) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        Item item = em.find(Item.class, sku);
        if (item != null) em.remove(item);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Item findBySku(Long sku) {
        EntityManager em = JPAUtil.getEntityManager();
        Item item = em.find(Item.class, sku);
        em.close();
        return item;
    }

    @Override
    public List<Item> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i", Item.class);
        List<Item> items = query.getResultList();
        em.close();
        return items;
    }

    @Override
    public List<Item> findBySection(String sectionCode) {
        EntityManager em = JPAUtil.getEntityManager();
        TypedQuery<Item> query = em.createQuery(
            "SELECT i FROM Item i WHERE i.section.code = :code", Item.class);
        query.setParameter("code", sectionCode);
        List<Item> items = query.getResultList();
        em.close();
        return items;
    }

    @Override
    public List<Item> findByVendor(String licenseNumber) {
        EntityManager em = JPAUtil.getEntityManager();
        TypedQuery<Item> query = em.createQuery(
            "SELECT i FROM Item i JOIN i.vendors v WHERE v.licenseNumber = :license", Item.class);
        query.setParameter("license", licenseNumber);
        List<Item> items = query.getResultList();
        em.close();
        return items;
    }

    @Override
    public List<Item> findItemsByVendor(String licenseNumber) {
        return findByVendor(licenseNumber);
    }
}
