package com.mycompany.hanoutimanagementsystem.dao;

import com.mycompany.hanoutimanagementsystem.model.Vendor;
import com.mycompany.hanoutimanagementsystem.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class VendorDAO implements InterfaceVendorDAO {

    @Override
    public void create(Vendor vendor) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(vendor);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void update(Vendor vendor) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(vendor);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void delete(String licenseNumber) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        Vendor vendor = em.find(Vendor.class, licenseNumber);
        if (vendor != null) em.remove(vendor);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Vendor findByLicense(String licenseNumber) {
        EntityManager em = JPAUtil.getEntityManager();
        Vendor vendor = em.find(Vendor.class, licenseNumber);
        em.close();
        return vendor;
    }

    @Override
    public List<Vendor> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        TypedQuery<Vendor> query = em.createQuery("SELECT v FROM Vendor v", Vendor.class);
        List<Vendor> vendors = query.getResultList();
        em.close();
        return vendors;
    }

    @Override
    public List<Vendor> findByItemSku(Long sku) {
        EntityManager em = JPAUtil.getEntityManager();
        TypedQuery<Vendor> query = em.createQuery(
            "SELECT v FROM Vendor v JOIN v.items i WHERE i.sku = :sku", Vendor.class);
        query.setParameter("sku", sku);
        List<Vendor> vendors = query.getResultList();
        em.close();
        return vendors;
    }
}
