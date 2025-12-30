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
        try {
            em.getTransaction().begin();
            em.persist(vendor);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Vendor vendor) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(vendor);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(String licenseNumber) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Vendor vendor = em.find(Vendor.class, licenseNumber);
            if (vendor != null) em.remove(vendor);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public Vendor findByLicense(String licenseNumber) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // ✅ تحميل المورد مع الأصناف المرتبطة
            TypedQuery<Vendor> query = em.createQuery(
                "SELECT DISTINCT v FROM Vendor v " +
                "LEFT JOIN FETCH v.items " +
                "WHERE v.licenseNumber = :license",
                Vendor.class
            );
            query.setParameter("license", licenseNumber);
            List<Vendor> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Vendor> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // ✅ تحميل الأصناف مع الموردين
            return em.createQuery(
                "SELECT DISTINCT v FROM Vendor v LEFT JOIN FETCH v.items", 
                Vendor.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Vendor> findByItemSku(Long sku) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // ✅ تحميل الموردين مع أصنافهم
            TypedQuery<Vendor> query = em.createQuery(
                "SELECT DISTINCT v FROM Vendor v " +
                "LEFT JOIN FETCH v.items i " +
                "WHERE i.sku = :sku",
                Vendor.class
            );
            query.setParameter("sku", sku);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}