package com.mycompany.hanoutimanagementsystem.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.mycompany.hanoutimanagementsystem.controller.*;

public class MainViewController {
    
    @FXML private TabPane mainTabPane;
    @FXML private Label statusLabel;
    
    private final ItemController itemController;
    private final SectionController sectionController;
    private final VendorController vendorController;
    
    // ✅ مراجع للواجهات الفرعية
    private OperationsViewController operationsViewController;
    
    public MainViewController(ItemController itemController, 
                             SectionController sectionController,
                             VendorController vendorController) {
        this.itemController = itemController;
        this.sectionController = sectionController;
        this.vendorController = vendorController;
    }
    
    @FXML
    public void initialize() {
        updateStatus("النظام جاهز للاستخدام - System Ready");
        
        // ✅ إضافة Listener لتبويب العمليات
        if (mainTabPane != null) {
            mainTabPane.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldTab, newTab) -> {
                    if (newTab != null && newTab.getText().contains("العمليات")) {
                        // عند فتح تبويب العمليات، حدّث البيانات
                        refreshOperationsData();
                        updateStatus("تم تحديث بيانات العمليات");
                    }
                }
            );
        }
    }
    
    /**
     * ✅ تعيين مرجع واجهة العمليات (يُستدعى من HanoutiApplication)
     */
    public void setOperationsViewController(OperationsViewController controller) {
        this.operationsViewController = controller;
    }
    
    /**
     * ✅ تحديث بيانات واجهة العمليات
     */
    private void refreshOperationsData() {
        if (operationsViewController != null) {
            try {
                operationsViewController.refreshAllData();
            } catch (Exception e) {
                System.err.println("خطأ في تحديث بيانات العمليات: " + e.getMessage());
            }
        }
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