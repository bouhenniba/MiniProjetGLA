package com.mycompany.hanoutimanagementsystem.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.mycompany.hanoutimanagementsystem.model.*;
import com.mycompany.hanoutimanagementsystem.controller.*;
import java.math.BigDecimal;

public class ItemsViewController {
    
    @FXML private TextField skuField;
    @FXML private TextField nameField;
    @FXML private TextField stockField;
    @FXML private TextField priceField; // ✅ حقل السعر الجديد
    @FXML private ComboBox<Section> sectionComboBox;
    
    @FXML private TextField searchField;
    @FXML private ComboBox<Section> filterSectionComboBox;
    
    @FXML private TableView<Item> itemsTable;
    @FXML private TableColumn<Item, Long> skuColumn;
    @FXML private TableColumn<Item, String> nameColumn;
    @FXML private TableColumn<Item, Integer> stockColumn;
    @FXML private TableColumn<Item, BigDecimal> priceColumn; // ✅ عمود السعر
    @FXML private TableColumn<Item, String> sectionColumn;
    
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    
    private ItemController itemController;
    private SectionController sectionController;
    
    private ObservableList<Item> itemsList;
    private ObservableList<Item> allItems;
    private ObservableList<Section> sectionsList;
    
    public void setControllers(ItemController itemController, SectionController sectionController) {
        this.itemController = itemController;
        this.sectionController = sectionController;
    }
    
    @FXML
    public void initialize() {
        if (itemController == null || sectionController == null) {
            return;
        }
        
        skuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price")); // ✅
        sectionColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getSection().getLabel()
            )
        );
        
        itemsList = FXCollections.observableArrayList();
        sectionsList = FXCollections.observableArrayList();
        
        itemsTable.setItems(itemsList);
        sectionComboBox.setItems(sectionsList);
        filterSectionComboBox.setItems(sectionsList);
        
        loadSections();
        loadItems();
        
        addNumericValidation(skuField);
        addNumericValidation(stockField);
        addDecimalValidation(priceField); // ✅
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterItems(newValue);
        });
        
        itemsTable.getSelectionModel().selectedItemProperty().addListener(
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
                showError("خطأ في البيانات", "يرجى ملء جميع الحقول بشكل صحيح");
                return;
            }
            
            Section selectedSection = sectionComboBox.getValue();
            if (selectedSection == null) {
                showError("خطأ", "يجب اختيار قسم");
                return;
            }
            
            Long sku = Long.parseLong(skuField.getText());
            String name = nameField.getText();
            int stock = Integer.parseInt(stockField.getText());
            BigDecimal price = new BigDecimal(priceField.getText()); // ✅
            
            itemController.createItem(sku, name, stock, price, selectedSection.getCode());
            
            loadItems();
            showSuccess("تم الإضافة بنجاح");
            handleClear();
            
        } catch (NumberFormatException e) {
            showError("خطأ", "SKU والكمية والسعر يجب أن تكون أرقام صحيحة");
        } catch (Exception e) {
            showError("خطأ", "فشلت عملية الإضافة: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleUpdate() {
        Item selectedItem = itemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showError("خطأ", "يرجى اختيار صنف للتحديث");
            return;
        }
        
        try {
            if (!validateFields()) {
                showError("خطأ في البيانات", "يرجى ملء جميع الحقول بشكل صحيح");
                return;
            }
            
            selectedItem.setName(nameField.getText());
            selectedItem.setStock(Integer.parseInt(stockField.getText()));
            selectedItem.setPrice(new BigDecimal(priceField.getText())); // ✅
            
            Section newSection = sectionComboBox.getValue();
            if (newSection != null && !newSection.equals(selectedItem.getSection())) {
                selectedItem.setSection(newSection);
            }
            
            itemController.updateItem(selectedItem);
            
            itemsTable.refresh();
            
            showSuccess("تم التحديث بنجاح");
            handleClear();
            
        } catch (Exception e) {
            showError("خطأ", "فشلت عملية التحديث: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleDelete() {
        Item selectedItem = itemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showError("خطأ", "يرجى اختيار صنف للحذف");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("تأكيد الحذف");
        confirmAlert.setHeaderText("هل أنت متأكد من حذف هذا الصنف؟");
        confirmAlert.setContentText(selectedItem.getName());
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                itemController.deleteItem(selectedItem.getSku());
                allItems.remove(selectedItem);
                itemsList.remove(selectedItem);
                showSuccess("تم الحذف بنجاح");
                handleClear();
            } catch (Exception e) {
                showError("خطأ", "فشلت عملية الحذف: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleSearch() {
        filterItems(searchField.getText());
    }
    
    @FXML
    private void handleFilterBySection() {
        Section selectedSection = filterSectionComboBox.getValue();
        if (selectedSection == null) {
            itemsList.setAll(allItems);
            return;
        }
        
        ObservableList<Item> filteredList = FXCollections.observableArrayList();
        for (Item item : allItems) {
            if (item.getSection().getCode().equals(selectedSection.getCode())) {
                filteredList.add(item);
            }
        }
        itemsList.setAll(filteredList);
    }
    
    @FXML
    private void handleRefresh() {
        loadItems();
        loadSections();
        searchField.clear();
        filterSectionComboBox.setValue(null);
    }
    
    @FXML
    private void handleClear() {
        skuField.clear();
        nameField.clear();
        stockField.clear();
        priceField.clear(); // ✅
        sectionComboBox.setValue(null);
        itemsTable.getSelectionModel().clearSelection();
        searchField.clear();
        filterSectionComboBox.setValue(null);
        filterItems("");
    }
    
    private void filterItems(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            itemsList.setAll(allItems);
            return;
        }
        
        String lowerCaseFilter = searchTerm.toLowerCase().trim();
        ObservableList<Item> filteredList = FXCollections.observableArrayList();
        
        for (Item item : allItems) {
            if (item.getName().toLowerCase().contains(lowerCaseFilter) ||
                String.valueOf(item.getSku()).contains(lowerCaseFilter)) {
                filteredList.add(item);
            }
        }
        
        itemsList.setAll(filteredList);
    }
    
    private void loadItems() {
        allItems = FXCollections.observableArrayList(itemController.getAllItems());
        itemsList.clear();
        itemsList.addAll(allItems);
    }
    
    private void loadSections() {
        sectionsList.clear();
        sectionsList.addAll(sectionController.getAllSections());
    }
    
    private void populateFields(Item item) {
        skuField.setText(String.valueOf(item.getSku()));
        nameField.setText(item.getName());
        stockField.setText(String.valueOf(item.getStock()));
        priceField.setText(item.getPrice() != null ? item.getPrice().toString() : "0"); // ✅
        sectionComboBox.setValue(item.getSection());
    }
    
    private boolean validateFields() {
        return !skuField.getText().isEmpty() &&
               !nameField.getText().isEmpty() &&
               !stockField.getText().isEmpty() &&
               !priceField.getText().isEmpty(); // ✅
    }
    
    private void addNumericValidation(TextField field) {
        field.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                field.setText(oldValue);
            }
        });
    }
    
    // ✅ Validation للأرقام العشرية (السعر)
    private void addDecimalValidation(TextField field) {
        field.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                field.setText(oldValue);
            }
        });
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
}