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
    private ObservableList<Vendor> allVendors; // âœ… Ù‚Ø§Ø¦Ù…Ø© Ù„Ø­ÙØ¸ Ø¬Ù…ÙŠØ¹ Ø§Ù„Ù…ÙˆØ±Ø¯ÙŠÙ†
    
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
        
        // âœ… Ø¥Ø¶Ø§ÙØ© Listener Ù„Ø­Ù‚Ù„ Ø§Ù„Ø¨Ø­Ø«
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterVendors(newValue);
        });
        
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
                showError("Ø®Ø·Ø£", "ÙŠØ±Ø¬Ù‰ Ù…Ù„Ø¡ Ø¬Ù…ÙŠØ¹ Ø§Ù„Ø­Ù‚ÙˆÙ„");
                return;
            }
            
            String license = licenseField.getText();
            String contact = contactField.getText();
            
            vendorController.createVendor(license, contact);
            
            Vendor newVendor = new Vendor(license, contact);
            allVendors.add(newVendor); // âœ… Ø¥Ø¶Ø§ÙØ© Ù„Ù„Ù‚Ø§Ø¦Ù…Ø© Ø§Ù„ÙƒØ§Ù…Ù„Ø©
            vendorsList.add(newVendor);
            
            showSuccess("ØªÙ… Ø¥Ø¶Ø§ÙØ© Ø§Ù„Ù…ÙˆØ±Ø¯ Ø¨Ù†Ø¬Ø§Ø­");
            handleClear();
            
        } catch (Exception e) {
            showError("Ø®Ø·Ø£", "ÙØ´Ù„Øª Ø¹Ù…Ù„ÙŠØ© Ø§Ù„Ø¥Ø¶Ø§ÙØ©: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleUpdate() {
        Vendor selectedVendor = vendorsTable.getSelectionModel().getSelectedItem();
        if (selectedVendor == null) {
            showError("Ø®Ø·Ø£", "ÙŠØ±Ø¬Ù‰ Ø§Ø®ØªÙŠØ§Ø± Ù…ÙˆØ±Ø¯ Ù„Ù„ØªØ­Ø¯ÙŠØ«");
            return;
        }
        
        try {
            if (!validateFields()) {
                showError("Ø®Ø·Ø£", "ÙŠØ±Ø¬Ù‰ Ù…Ù„Ø¡ Ø¬Ù…ÙŠØ¹ Ø§Ù„Ø­Ù‚ÙˆÙ„");
                return;
            }
            
            selectedVendor.setContactName(contactField.getText());
            vendorController.updateVendor(selectedVendor);
            vendorsTable.refresh();
            
            showSuccess("ØªÙ… ØªØ­Ø¯ÙŠØ« Ø§Ù„Ù…ÙˆØ±Ø¯ Ø¨Ù†Ø¬Ø§Ø­");
            handleClear();
            
        } catch (Exception e) {
            showError("Ø®Ø·Ø£", "ÙØ´Ù„Øª Ø¹Ù…Ù„ÙŠØ© Ø§Ù„ØªØ­Ø¯ÙŠØ«: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleDelete() {
        Vendor selectedVendor = vendorsTable.getSelectionModel().getSelectedItem();
        if (selectedVendor == null) {
            showError("Ø®Ø·Ø£", "ÙŠØ±Ø¬Ù‰ Ø§Ø®ØªÙŠØ§Ø± Ù…ÙˆØ±Ø¯ Ù„Ù„Ø­Ø°Ù");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("ØªØ£ÙƒÙŠØ¯ Ø§Ù„Ø­Ø°Ù");
        confirmAlert.setHeaderText("Ù‡Ù„ Ø£Ù†Øª Ù…ØªØ£ÙƒØ¯ Ù…Ù† Ø­Ø°Ù Ù‡Ø°Ø§ Ø§Ù„Ù…ÙˆØ±Ø¯ØŸ");
        confirmAlert.setContentText(selectedVendor.getContactName());
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                vendorController.deleteVendor(selectedVendor.getLicenseNumber());
                allVendors.remove(selectedVendor); // âœ… Ø­Ø°Ù Ù…Ù† Ø§Ù„Ù‚Ø§Ø¦Ù…Ø© Ø§Ù„ÙƒØ§Ù…Ù„Ø©
                vendorsList.remove(selectedVendor);
                showSuccess("ØªÙ… Ø§Ù„Ø­Ø°Ù Ø¨Ù†Ø¬Ø§Ø­");
                handleClear();
            } catch (Exception e) {
                showError("Ø®Ø·Ø£", "ÙØ´Ù„Øª Ø¹Ù…Ù„ÙŠØ© Ø§Ù„Ø­Ø°Ù: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleSearch() {
        filterVendors(searchField.getText());
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
        searchField.clear();
        filterVendors(""); // âœ… Ø¹Ø±Ø¶ Ø§Ù„ÙƒÙ„
    }
    
    // âœ… Ø¯Ø§Ù„Ø© Ø¬Ø¯ÙŠØ¯Ø© Ù„Ù„ÙÙ„ØªØ±Ø©
    private void filterVendors(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            vendorsList.setAll(allVendors);
            return;
        }
        
        String lowerCaseFilter = searchTerm.toLowerCase().trim();
        ObservableList<Vendor> filteredList = FXCollections.observableArrayList();
        
        for (Vendor vendor : allVendors) {
            if (vendor.getLicenseNumber().toLowerCase().contains(lowerCaseFilter) ||
                vendor.getContactName().toLowerCase().contains(lowerCaseFilter)) {
                filteredList.add(vendor);
            }
        }
        
        vendorsList.setAll(filteredList);
    }
    
    private void loadVendors() {
        allVendors = FXCollections.observableArrayList(vendorController.getAllVendors());
        vendorsList.clear();
        vendorsList.addAll(allVendors);
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
        alert.setTitle("Ù†Ø¬Ø§Ø­");
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