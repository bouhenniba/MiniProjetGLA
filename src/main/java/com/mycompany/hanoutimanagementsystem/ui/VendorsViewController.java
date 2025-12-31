package com.mycompany.hanoutimanagementsystem.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import com.mycompany.hanoutimanagementsystem.model.*;
import com.mycompany.hanoutimanagementsystem.controller.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class VendorsViewController {

    @FXML private TextField vendorNameField;
    @FXML private TextField licenseField;
    @FXML private TextField contactField;
    @FXML private TextField searchField;

    @FXML private ListView<ItemSupplyEntry> itemsListView;
    @FXML private ComboBox<Item> itemComboBox;
    @FXML private TextField supplyPriceField;


    @FXML private TableView<Vendor> vendorsTable;
    @FXML private TableColumn<Vendor, String> vendorNameColumn;
    @FXML private TableColumn<Vendor, String> licenseColumn;
    @FXML private TableColumn<Vendor, String> contactColumn;
    @FXML private TableColumn<Vendor, Integer> itemsSuppliedColumn;

    private VendorController vendorController;
    private ItemController itemController;
    private ObservableList<Vendor> vendorsList;
    private ObservableList<Vendor> allVendors;
    private ObservableList<ItemSupplyEntry> currentItems;

    public void setController(VendorController vendorController) {
        this.vendorController = vendorController;
    }

    public void setControllers(VendorController vendorController, ItemController itemController) {
        this.vendorController = vendorController;
        this.itemController = itemController;
    }

    @FXML
    public void initialize() {
        // Set placeholders
        vendorNameField.setPromptText("اسم المورد");
        licenseField.setPromptText("رقم الرخصة");
        contactField.setPromptText("جهة الاتصال: 0XXXXXXXXX");
        searchField.setPromptText("بحث...");
        supplyPriceField.setPromptText("سعر التوريد");
        itemComboBox.setPromptText("اختر صنف...");

        if (vendorController == null) return;

        vendorNameColumn.setCellValueFactory(new PropertyValueFactory<>("vendorName"));
        licenseColumn.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        itemsSuppliedColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleIntegerProperty(
                cellData.getValue().getProvidedItems() != null ? cellData.getValue().getProvidedItems().size() : 0
            ).asObject()
        );

        currentItems = FXCollections.observableArrayList();
        itemsListView.setItems(currentItems);
        
        itemsListView.setCellFactory(param -> new ListCell<ItemSupplyEntry>() {
            private final HBox hBox = new HBox(10); // حاوية النص والأيقونة
            private final Label itemLabel = new Label(); // لعرض اسم الصنف وسعره
            private final Button editIconBtn = new Button("✎"); // أيقونة التعديل

            {
                // تنسيق أيقونة التعديل (تصميم عصري) [2]
                editIconBtn.setStyle("-fx-text-fill: #3182ce; -fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");
                
                // دفع الأيقونة لليمين تماماً
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                
                hBox.getChildren().addAll(itemLabel, spacer, editIconBtn);
                hBox.setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(ItemSupplyEntry entry, boolean empty) {
                super.updateItem(entry, empty);
                if (empty || entry == null) {
                    setGraphic(null);
                } else {
                    // عرض بيانات الصنف [1]
                    itemLabel.setText(entry.getItem().getName() + " - " + entry.getSupplyPrice() + " دج");
                    
                    // منطق أيقونة التعديل
                    editIconBtn.setOnAction(e -> {
                        // إعادة البيانات إلى حقول الإدخال لتعديلها [10، 26]
                        itemComboBox.setValue(entry.getItem());
                        supplyPriceField.setText(entry.getSupplyPrice().toString());
                        
                        // تمييز السطر المختار للتسهيل على المستخدم
                        itemsListView.getSelectionModel().select(entry);
                    });
                    
                    setGraphic(hBox);
                }
            }
        });

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
            itemComboBox.setCellFactory(param -> new ListCell<Item>() {
                @Override
                protected void updateItem(Item item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getSku() + " - " + item.getName());
                }
            });
            itemComboBox.setButtonCell(new ListCell<Item>() {
                @Override
                protected void updateItem(Item item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getSku() + " - " + item.getName());
                }
            });
        }
    }

    @FXML
    private void handleAddItemToList() {
        Item selectedItem = itemComboBox.getValue();
        String priceText = supplyPriceField.getText().trim();

        if (selectedItem == null) {
            showError("خطأ", "يرجى اختيار صنف");
            return;
        }

        if (priceText.isEmpty()) {
            showError("خطأ", "يرجى إدخال سعر التوريد");
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceText);
            
            // البحث عن الصنف في القائمة الحالية
            ItemSupplyEntry existingEntry = null;
            for (ItemSupplyEntry entry : currentItems) {
                if (entry.getItem().getSku().equals(selectedItem.getSku())) {
                    existingEntry = entry;
                    break;
                }
            }

            if (existingEntry != null) {
                // تحديث الصنف الموجود بالسعر الجديد
                int index = currentItems.indexOf(existingEntry);
                currentItems.set(index, new ItemSupplyEntry(selectedItem, price));
                itemsListView.getSelectionModel().clearSelection();
            } else {
                // إضافة صنف جديد
                currentItems.add(new ItemSupplyEntry(selectedItem, price));
            }

            // تنظيف الحقول
            itemComboBox.setValue(null);
            supplyPriceField.clear();

        } catch (NumberFormatException e) {
            showError("خطأ", "صيغة السعر غير صحيحة");
        }
    }

    @FXML
    private void handleRemoveItemFromList() {
        ItemSupplyEntry selected = itemsListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("خطأ", "يرجى اختيار صنف من القائمة لإزالته");
            return;
        }
        currentItems.remove(selected);
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

            Vendor newVendor = new Vendor(license, vendorName, contact);

            if (!currentItems.isEmpty()) {
                Set<SupplyContract> contracts = new HashSet<>();
                for (ItemSupplyEntry entry : currentItems) {
                    SupplyContract contract = new SupplyContract(entry.getItem(), newVendor, entry.getSupplyPrice());
                    contracts.add(contract);
                }
                newVendor.setProvidedItems(contracts);
            }

            vendorController.createVendor(newVendor);

            allVendors.add(newVendor);
            vendorsList.add(newVendor);
            vendorsTable.refresh();

            showSuccess("✅ تم إضافة المورد بنجاح مع " + currentItems.size() + " صنف");
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

            // Update items logic
            if (itemController != null) {
                // Get existing contracts
                Set<SupplyContract> existingContracts = selectedVendor.getProvidedItems();
                
                // Create a set of SKUs from currentItems for easy lookup
                Set<Long> currentItemSkus = currentItems.stream()
                    .map(entry -> entry.getItem().getSku())
                    .collect(Collectors.toSet());

                // Remove contracts that are no longer in the list
                existingContracts.removeIf(contract -> !currentItemSkus.contains(contract.getItem().getSku()));

                // Add or update contracts
                for (ItemSupplyEntry entry : currentItems) {
                    boolean found = false;
                    for (SupplyContract contract : existingContracts) {
                        if (contract.getItem().getSku().equals(entry.getItem().getSku())) {
                            contract.setSupplyPrice(entry.getSupplyPrice());
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        // Add new contract
                         try {
                            itemController.addVendorToItem(
                                entry.getItem().getSku(), 
                                selectedVendor.getLicenseNumber(), 
                                entry.getSupplyPrice()
                            );
                        } catch (Exception e) {
                            System.err.println("تحذير: فشل ربط الصنف: " + e.getMessage());
                        }
                    }
                }
            }

            vendorController.updateVendor(selectedVendor);
            
            // Refresh table view
            int index = vendorsList.indexOf(selectedVendor);
            if (index >= 0) {
                vendorsList.set(index, selectedVendor);
            }
            vendorsTable.refresh();

            showSuccess("✅ تم تحديث المورد بنجاح");
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
                showSuccess("✅ تم الحذف بنجاح");
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
        currentItems.clear();
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

        currentItems.clear();
        if (vendor.getProvidedItems() != null) {
            for (SupplyContract contract : vendor.getProvidedItems()) {
                currentItems.add(new ItemSupplyEntry(contract.getItem(), contract.getSupplyPrice()));
            }
        }

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
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class ItemSupplyEntry {
        private final Item item;
        private final BigDecimal supplyPrice;

        public ItemSupplyEntry(Item item, BigDecimal supplyPrice) {
            this.item = item;
            this.supplyPrice = supplyPrice;
        }

        public Item getItem() { return item; }
        public BigDecimal getSupplyPrice() { return supplyPrice; }
    }
}