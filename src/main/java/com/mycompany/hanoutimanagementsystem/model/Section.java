package com.mycompany.hanoutimanagementsystem.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import com.mycompany.hanoutimanagementsystem.model.Item;

@Entity
public class Section {

    @Id
    private String code;
    private String label;

    @OneToMany(
        mappedBy = "section",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<Item> items = new HashSet<>();

    public Section() {}
    public Section(String code, String label) {
        this.code = code;
        this.label = label;
    }

    // Getters & Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Set<Item> getItems() { return items; }

    // ✅ طريقة لإضافة Item مرتبطة بالـ Section مباشرة
    public Item addItem(Long sku, String name, int stock) {
        Item item = new Item(sku, name, stock);
        item.setSection(this);    // ربط مع Section
        items.add(item);          // إضافة لمجموعة الـ Section
        return item;
    }

    // إزالة Item إذا لزم
    public void removeItem(Item item) {
        items.remove(item);
        item.setSection(null);
    }
}
