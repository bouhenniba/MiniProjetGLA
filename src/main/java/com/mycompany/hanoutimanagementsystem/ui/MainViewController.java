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
        if (statusLabel != null) {
            updateStatus("النظام جاهز للاستخدام - System Ready");
        }
        
        if (mainTabPane != null) {
            mainTabPane.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldTab, newTab) -> {
                    if (newTab != null && newTab.getText().contains("العمليات")) {
                        refreshOperationsData();
                        updateStatus("تم تحديث بيانات العمليات");
                    }
                }
            );
        }
    }
    
    // Methods to handle sidebar navigation
    @FXML
    private void selectItemsTab() {
        mainTabPane.getSelectionModel().select(0);
    }

    @FXML
    private void selectSectionsTab() {
        mainTabPane.getSelectionModel().select(1);
    }

    @FXML
    private void selectVendorsTab() {
        mainTabPane.getSelectionModel().select(2);
    }

    @FXML
    private void selectOperationsTab() {
        mainTabPane.getSelectionModel().select(3);
    }
    
    public void setOperationsViewController(OperationsViewController controller) {
        this.operationsViewController = controller;
    }
    
    private void refreshOperationsData() {
        if (operationsViewController != null) {
            try {
                operationsViewController.refreshAllData();
            } catch (Exception e) {
                System.err.println("خطأ في تحديث بيانات العمليات: " + e.getMessage());
            }
        }
    }
    
    public void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
}