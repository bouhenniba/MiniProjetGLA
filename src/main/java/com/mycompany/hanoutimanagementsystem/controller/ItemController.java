package com.mycompany.hanoutimanagementsystem.controller;

import com.mycompany.hanoutimanagementsystem.dao.InterfaceItemDAO;
import com.mycompany.hanoutimanagementsystem.dao.InterfaceSectionDAO;
import com.mycompany.hanoutimanagementsystem.dao.InterfaceVendorDAO;
import com.mycompany.hanoutimanagementsystem.model.Item;
import com.mycompany.hanoutimanagementsystem.model.Section;
import com.mycompany.hanoutimanagementsystem.model.Vendor;
import com.mycompany.hanoutimanagementsystem.model.SupplyContract;

import java.math.BigDecimal;
import java.util.List;

/**
 * متحكم الأصناف - يدير عمليات CRUD والعلاقات
 */
public class ItemController {

    private final InterfaceItemDAO itemDAO;
    private final InterfaceSectionDAO sectionDAO;
    private final InterfaceVendorDAO vendorDAO;

    public ItemController(InterfaceItemDAO itemDAO,
                          InterfaceSectionDAO sectionDAO,
                          InterfaceVendorDAO vendorDAO) {
        this.itemDAO = itemDAO;
        this.sectionDAO = sectionDAO;
        this.vendorDAO = vendorDAO;
    }

    // ================= CRUD Operations =================

    public void createItem(Long sku, String name, int stock, 
                          BigDecimal price, String sectionCode) {
        Section section = sectionDAO.findByCode(sectionCode);
        if (section == null) {
            throw new IllegalArgumentException(
                "Section not found: " + sectionCode
            );
        }

        Item item = new Item(sku, name, stock, price);
        item.setSection(section);
        
        itemDAO.create(item);
    }

    public void updateItem(Item item) {
        if (item.getSection() == null) {
            throw new IllegalArgumentException("Item must have a section");
        }
        itemDAO.update(item);
    }

    public void deleteItem(Long sku) {
        itemDAO.delete(sku);
    }

    public Item findItem(Long sku) {
        return itemDAO.findBySku(sku);
    }

    public List<Item> getAllItems() {
        return itemDAO.findAll();
    }

    // ================= Vendor Management =================

    /**
     * ✅ ربط صنف بمورد (إضافة مورد لصنف) - محسّن مع السعر
     */
    public void addVendorToItem(Long itemSku, String vendorLicense, BigDecimal supplyPrice) {
        // ✅ استخدام findBySku المحسّن الذي يحمّل الموردين
        Item item = itemDAO.findBySku(itemSku);
        Vendor vendor = vendorDAO.findByLicense(vendorLicense);
        
        if (item == null) {
            throw new IllegalArgumentException("Item not found: " + itemSku);
        }
        if (vendor == null) {
            throw new IllegalArgumentException("Vendor not found: " + vendorLicense);
        }
        
        // ✅ التحقق من عدم وجود علاقة مسبقة
        boolean exists = item.getVendorSupplies().stream()
                .anyMatch(sc -> sc.getVendor().getLicenseNumber().equals(vendorLicense));
        
        if (exists) {
            throw new IllegalArgumentException(
                "Vendor already linked to this item"
            );
        }
        
        // ✅ إنشاء عقد توريد جديد
        SupplyContract contract = new SupplyContract(item, vendor, supplyPrice);
        item.getVendorSupplies().add(contract);
        vendor.getProvidedItems().add(contract);
        
        // ✅ حفظ التغييرات
        itemDAO.update(item);

        System.out.println("✅ تم ربط المورد " + vendorLicense +
                         " بالصنف " + itemSku + " بسعر " + supplyPrice + " بنجاح");
    }

    /**
     * ✅ إزالة مورد من صنف - محسّن
     */
    public void removeVendorFromItem(Long itemSku, String vendorLicense) {
        Item item = itemDAO.findBySku(itemSku);

        if (item == null) {
            throw new IllegalArgumentException("Item not found");
        }
        
        // ✅ البحث عن العقد وحذفه
        SupplyContract contractToRemove = item.getVendorSupplies().stream()
                .filter(sc -> sc.getVendor().getLicenseNumber().equals(vendorLicense))
                .findFirst()
                .orElse(null);

        if (contractToRemove == null) {
            throw new IllegalArgumentException(
                "Vendor is not linked to this item"
            );
        }
        
        // إزالة العقد من قائمة الصنف
        item.getVendorSupplies().remove(contractToRemove);
        
        // إزالة العقد من قائمة المورد (هذا هو الجزء الذي كان يسبب المشكلة)
        // بدلاً من الوصول إلى providedItems مباشرة (التي قد تكون Lazy)، نعتمد على Cascade من جانب Item
        // أو نتأكد من تحميل المورد بالكامل إذا لزم الأمر.
        // ولكن بما أننا في جلسة جديدة، فإن الوصول إلى contractToRemove.getVendor().getProvidedItems() قد يسبب LazyInitializationException
        // إذا لم يتم تحميل providedItems مسبقاً.
        
        // الحل: بما أن العلاقة ثنائية الاتجاه، يكفي إزالتها من جانب واحد وحفظ الكيان المالك للعلاقة (أو كلاهما إذا كنا في نفس الجلسة).
        // ولكن الأفضل هو الاعتماد على orphanRemoval في Item.
        
        // ومع ذلك، لتجنب الخطأ، يجب ألا نحاول الوصول إلى providedItems للمورد إذا لم نكن بحاجة إليها
        // أو إذا لم نكن متأكدين من أنها محملة.
        
        // في حالتنا، نحن نقوم بتحديث Item، و Item يملك العلاقة (mappedBy في Vendor، ولكن هنا SupplyContract هو الكيان الوسيط).
        // SupplyContract لديه @ManyToOne لـ Item و Vendor.
        // Item لديه @OneToMany(mappedBy="item", cascade=ALL, orphanRemoval=true)
        
        // لذا، إزالة contractToRemove من item.getVendorSupplies() وحفظ item يجب أن يكون كافياً لحذف SupplyContract
        // بفضل orphanRemoval=true.
        
        // السطر المسبب للمشكلة هو:
        // contractToRemove.getVendor().getProvidedItems().remove(contractToRemove);
        
        // سنقوم بإزالته لأننا نعتمد على تحديث Item لحذف العقد.
        
        itemDAO.update(item);

        System.out.println("✅ تم إزالة المورد " + vendorLicense +
                         " من الصنف " + itemSku);
    }

    // ================= Operational Scenarios =================

    /**
     * عرض جميع العناصر التابعة لقسم معين
     */
    public List<Item> getItemsBySection(String sectionCode) {
        return itemDAO.findBySection(sectionCode);
    }

    /**
     * عرض جميع العناصر التي يوفرها مورد معين
     */
    public List<Item> getItemsByVendor(String licenseNumber) {
        return itemDAO.findByVendor(licenseNumber);
    }

    /**
     * ✅ التحقق من العلاقة بين صنف ومورد
     */
    public boolean isVendorLinkedToItem(Long itemSku, String vendorLicense) {
        Item item = itemDAO.findBySku(itemSku);
        if (item == null) return false;

        return item.getVendorSupplies().stream()
            .anyMatch(sc -> sc.getVendor().getLicenseNumber().equals(vendorLicense));
    }
}