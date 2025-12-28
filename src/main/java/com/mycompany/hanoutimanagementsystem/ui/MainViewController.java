package com.mycompany.hanoutimanagementsystem.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.mycompany.hanoutimanagementsystem.controller.*;

/**
 * متحكم الواجهة الرئيسية
 * يدير التنقل بين التبويبات وتمرير المتحكمات للواجهات الفرعية
 */
public class MainViewController {
    
    @FXML private TabPane mainTabPane;
    @FXML private Label statusLabel;
    
    // المتحكمات الخلفية - حقن عبر المشيد
    private final ItemController itemController;
    private final SectionController sectionController;
    private final VendorController vendorController;
    
    /**
     * Constructor Injection - حقن المتحكمات عبر المشيد
     */
    public MainViewController(ItemController itemController, 
                             SectionController sectionController,
                             VendorController vendorController) {
        this.itemController = itemController;
        this.sectionController = sectionController;
        this.vendorController = vendorController;
    }
    
    @FXML
    public void initialize() {
        // ✅ الآن Controllers الفرعية ستُحقن تلقائياً عبر ControllerFactory
        // لذا لا نحتاج لتمريرها يدوياً هنا
        
        updateStatus("النظام جاهز للاستخدام - System Ready");
    }
    
    /**
     * تحديث شريط الحالة
     */
    public void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
}