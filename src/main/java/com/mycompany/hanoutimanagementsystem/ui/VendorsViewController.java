package com.mycompany.hanoutimanagementsystem.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.mycompany.hanoutimanagementsystem.model.*;
import com.mycompany.hanoutimanagementsystem.controller.*;

public class VendorsViewController {
    
    @FXML private TextField licenseField;
    @FXML private TextField contactField;
    @FXML private TextField searchField;
    
    @FXML private TableView<Vendor> vendorsTable;
    @FXML private TableColumn<Vendor, String> licenseColumn;
    @FXML private TableColumn<Vendor, String> contactColumn;
    @FXML private TableColumn<Vendor, Integer> itemsSuppliedColumn;
    
    private VendorController vendorController;
    private ObservableList<Vendor> vendorsList;
    
    /**
     * ✅ لا تستدعي initialize() هنا
     */
    public void setController(VendorController vendorController) {
        this.vendorController = vendorController;
    }
    
    @FXML
    public void initialize() {
        if (vendorController == null) return;
        
        licenseColumn.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        itemsSuppliedColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(
                cellData.getValue().getItems().size()
            ).asObject()
        );
        
        vendorsList = FXCollections.observableArrayList();
        vendorsTable.setItems(vendorsList);
        
        loadVendors();
        
        vendorsTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateFields(newSelection);
                }
            }
        );
    }
    
    @FXML
    private void handleAdd() {
        try {
            if (!validateFields()) {
                showError("خطأ", "يرجى ملء جميع الحقول");
                return;
            }
            
            String license = licenseField.getText();
            String contact = contactField.getText();
            
            vendorController.createVendor(license, contact);
            
            Vendor newVendor = new Vendor(license, contact);
            vendorsList.add(newVendor);
            
            showSuccess("تم إضافة المورد بنجاح");
            handleClear();
            
        } catch (Exception e) {
            showError("خطأ", "فشلت عملية الإضافة: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleUpdate() {
        Vendor selectedVendor = vendorsTable.getSelectionModel().getSelectedItem();
        if (selectedVendor == null) {
            showError("خطأ", "يرجى اختيار مورد للتحديث");
            return;
        }
        
        try {
            if (!validateFields()) {
                showError("خطأ", "يرجى ملء جميع الحقول");
                return;
            }
            
            selectedVendor.setContactName(contactField.getText());
            vendorController.updateVendor(selectedVendor);
            vendorsTable.refresh();
            
            showSuccess("تم تحديث المورد بنجاح");
            handleClear();
            
        } catch (Exception e) {
            showError("خطأ", "فشلت عملية التحديث: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleDelete() {
        Vendor selectedVendor = vendorsTable.getSelectionModel().getSelectedItem();
        if (selectedVendor == null) {
            showError("خطأ", "يرجى اختيار مورد للحذف");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("تأكيد الحذف");
        confirmAlert.setHeaderText("هل أنت متأكد من حذف هذا المورد؟");
        confirmAlert.setContentText(selectedVendor.getContactName());
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                vendorController.deleteVendor(selectedVendor.getLicenseNumber());
                vendorsList.remove(selectedVendor);
                showSuccess("تم الحذف بنجاح");
                handleClear();
            } catch (Exception e) {
                showError("خطأ", "فشلت عملية الحذف: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadVendors();
            return;
        }
        
        ObservableList<Vendor> filteredList = FXCollections.observableArrayList();
        for (Vendor vendor : vendorController.getAllVendors()) {
            if (vendor.getLicenseNumber().toLowerCase().contains(searchTerm) ||
                vendor.getContactName().toLowerCase().contains(searchTerm)) {
                filteredList.add(vendor);
            }
        }
        vendorsTable.setItems(filteredList);
    }
    
    @FXML
    private void handleRefresh() {
        loadVendors();
        searchField.clear();
    }
    
    @FXML
    private void handleClear() {
        licenseField.clear();
        contactField.clear();
        vendorsTable.getSelectionModel().clearSelection();
    }
    
    private void loadVendors() {
        vendorsList.clear();
        vendorsList.addAll(vendorController.getAllVendors());
    }
    
    private void populateFields(Vendor vendor) {
        licenseField.setText(vendor.getLicenseNumber());
        contactField.setText(vendor.getContactName());
    }
    
    private boolean validateFields() {
        return !licenseField.getText().isEmpty() && !contactField.getText().isEmpty();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("نجاح");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}