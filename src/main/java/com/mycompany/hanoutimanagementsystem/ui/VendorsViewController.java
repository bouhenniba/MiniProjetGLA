package com.mycompany.hanoutimanagementsystem.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.mycompany.hanoutimanagementsystem.model.*;
import com.mycompany.hanoutimanagementsystem.controller.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class VendorsViewController {

    @FXML private TextField vendorNameField;
    @FXML private TextField licenseField;
    @FXML private TextField contactField;
    @FXML private TextField searchField;

    @FXML private ComboBox<Item> itemComboBox;
    @FXML private TextField supplyPriceField;

    @FXML private TableView<Vendor> vendorsTable;
    @FXML private TableColumn<Vendor, String> vendorNameColumn;
    @FXML private TableColumn<Vendor, String> licenseColumn;
    @FXML private TableColumn<Vendor, String> contactColumn;
    @FXML private TableColumn<Vendor, Integer> itemsSuppliedColumn;

    private VendorController vendorController;
    private ObservableList<Vendor> vendorsList;
    private ObservableList<Vendor> allVendors;

    public void setController(VendorController vendorController) {
        this.vendorController = vendorController;
    }

    @FXML
    public void initialize() {
        if (vendorController == null) return;

        vendorNameColumn.setCellValueFactory(new PropertyValueFactory<>("vendorName"));
        licenseColumn.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        itemsSuppliedColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleIntegerProperty(
                cellData.getValue().getProvidedItems() != null ? cellData.getValue().getProvidedItems().size() : 0
            ).asObject()
        );

        loadItems();
        addDecimalValidation(supplyPriceField);

        vendorsList = FXCollections.observableArrayList();
        vendorsTable.setItems(vendorsList);

        loadVendors();

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

    private void loadItems() {
        if (vendorController != null) {
            itemComboBox.setItems(FXCollections.observableArrayList(vendorController.getAllItems()));
        }
    }

    private void addDecimalValidation(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                textField.setText(oldValue);
            }
        });
    }

    @FXML
    private void handleAdd() {
        try {
            if (!validateFields()) {
                showError("خطأ", "يرجى ملء جميع الحقول الأساسية (اسم المورد، الرخصة، جهة الاتصال)");
                return;
            }

            String vendorName = vendorNameField.getText();
            String license = licenseField.getText();
            String contact = contactField.getText();

            Item selectedItem = itemComboBox.getValue();
            String priceText = supplyPriceField.getText();

            Vendor newVendor = new Vendor(license, vendorName, contact);

            if (selectedItem != null && !priceText.isEmpty()) {
                try {
                    BigDecimal price = new BigDecimal(priceText);
                    SupplyContract contract = new SupplyContract(selectedItem, newVendor, price);
                    Set<SupplyContract> contracts = new HashSet<>();
                    contracts.add(contract);
                    newVendor.setProvidedItems(contracts);
                } catch (NumberFormatException e) {
                    showError("خطأ", "صيغة السعر غير صحيحة. سيتم حفظ المورد بدون معلومات التوريد.");
                }
            }

            vendorController.createVendor(newVendor);

            allVendors.add(newVendor);
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
                showError("خطأ", "يرجى ملء جميع الحقول الأساسية");
                return;
            }

            selectedVendor.setVendorName(vendorNameField.getText());
            selectedVendor.setContactName(contactField.getText());
            // License number is the ID, so it should not be updated.

            // Logic for updating supply contract would be more complex.
            // For now, we focus on updating vendor's own fields.
            // If an item and price are selected, should it add a new contract or update an existing one?
            // This part is left for more detailed requirements.

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
        confirmAlert.setContentText(selectedVendor.getVendorName());

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                vendorController.deleteVendor(selectedVendor.getLicenseNumber());
                allVendors.remove(selectedVendor);
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
        filterVendors(searchField.getText());
    }

    @FXML
    private void handleRefresh() {
        loadVendors();
        loadItems();
        searchField.clear();
    }

    @FXML
    private void handleClear() {
        vendorNameField.clear();
        licenseField.clear();
        contactField.clear();
        itemComboBox.getSelectionModel().clearSelection();
        supplyPriceField.clear();
        vendorsTable.getSelectionModel().clearSelection();
        searchField.clear();
        filterVendors("");
    }

    private void filterVendors(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            vendorsList.setAll(allVendors);
            return;
        }

        String lowerCaseFilter = searchTerm.toLowerCase().trim();
        ObservableList<Vendor> filteredList = FXCollections.observableArrayList();

        for (Vendor vendor : allVendors) {
            if ((vendor.getVendorName() != null && vendor.getVendorName().toLowerCase().contains(lowerCaseFilter)) ||
                (vendor.getLicenseNumber() != null && vendor.getLicenseNumber().toLowerCase().contains(lowerCaseFilter)) ||
                (vendor.getContactName() != null && vendor.getContactName().toLowerCase().contains(lowerCaseFilter))) {
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
        vendorNameField.setText(vendor.getVendorName());
        licenseField.setText(vendor.getLicenseNumber());
        contactField.setText(vendor.getContactName());

        // Clear supply fields as we are not editing contracts directly in this simplified view
        itemComboBox.getSelectionModel().clearSelection();
        supplyPriceField.clear();
    }

    private boolean validateFields() {
        return !vendorNameField.getText().isEmpty() &&
               !licenseField.getText().isEmpty() &&
               !contactField.getText().isEmpty();
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