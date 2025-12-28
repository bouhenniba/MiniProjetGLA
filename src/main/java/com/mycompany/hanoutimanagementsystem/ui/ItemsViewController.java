package com.mycompany.hanoutimanagementsystem.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.mycompany.hanoutimanagementsystem.model.*;
import com.mycompany.hanoutimanagementsystem.controller.*;

/**
 * متحكم واجهة إدارة الأصناف
 * يتعامل مع العمليات: إضافة، تحديث، حذف، بحث
 */
public class ItemsViewController {
    
    // حقول الإدخال
    @FXML private TextField skuField;
    @FXML private TextField nameField;
    @FXML private TextField stockField;
    @FXML private ComboBox<Section> sectionComboBox;
    
    // البحث والفلترة
    @FXML private TextField searchField;
    @FXML private ComboBox<Section> filterSectionComboBox;
    
    // الجدول والأعمدة
    @FXML private TableView<Item> itemsTable;
    @FXML private TableColumn<Item, Integer> skuColumn;
    @FXML private TableColumn<Item, String> nameColumn;
    @FXML private TableColumn<Item, Integer> stockColumn;
    @FXML private TableColumn<Item, String> sectionColumn;
    
    // الأزرار
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    
    // المتحكمات الخلفية
    private ItemController itemController;
    private SectionController sectionController;
    
    // القوائم المرصودة
    private ObservableList<Item> itemsList;
    private ObservableList<Section> sectionsList;
    
    /**
     * تعيين المتحكمات الخلفية
     * ✅ لا تستدعي initialize() هنا!
     */
    public void setControllers(ItemController itemController, SectionController sectionController) {
        this.itemController = itemController;
        this.sectionController = sectionController;
    }
    
    /**
     * ✅ JavaFX تستدعي هذه الدالة تلقائياً بعد حقن @FXML fields
     */
    @FXML
    public void initialize() {
        // انتظر حتى يتم حقن المتحكمات
        if (itemController == null || sectionController == null) {
            return;
        }
        
        // إعداد الأعمدة
        skuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        sectionColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getSection().getLabel()
            )
        );
        
        // تهيئة القوائم
        itemsList = FXCollections.observableArrayList();
        sectionsList = FXCollections.observableArrayList();
        
        itemsTable.setItems(itemsList);
        sectionComboBox.setItems(sectionsList);
        filterSectionComboBox.setItems(sectionsList);
        
        // تحميل البيانات الأولية
        loadSections();
        loadItems();
        
        // التحقق من الحقول الرقمية فقط
        addNumericValidation(skuField);
        addNumericValidation(stockField);
        
        // تحديد صنف عند النقر على الجدول
        itemsTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateFields(newSelection);
                }
            }
        );
    }
    
    /**
     * إضافة صنف جديد
     */
    @FXML
    private void handleAdd() {
        try {
            // التحقق من الحقول
            if (!validateFields()) {
                showError("خطأ في البيانات", "يرجى ملء جميع الحقول بشكل صحيح");
                return;
            }
            
            Section selectedSection = sectionComboBox.getValue();
            if (selectedSection == null) {
                showError("خطأ", "يجب اختيار قسم (علاقة إجبارية)");
                return;
            }
            
            // استخدام Factory Method من القسم لإنشاء الصنف
            Long sku = Long.parseLong(skuField.getText());
            String name = nameField.getText();
            int stock = Integer.parseInt(stockField.getText());
            
            Item newItem = selectedSection.addItem(sku, name, stock);
            
            // حفظ في قاعدة البيانات عبر المتحكم
            itemController.createItem(sku, name, stock, selectedSection.getCode());
            
            // تحديث القائمة المرصودة
            itemsList.add(newItem);
            
            showSuccess("تم الإضافة بنجاح");
            handleClear();
            
        } catch (NumberFormatException e) {
            showError("خطأ", "SKU والكمية يجب أن تكون أرقاماً");
        } catch (Exception e) {
            showError("خطأ", "فشلت عملية الإضافة: " + e.getMessage());
        }
    }
    
    /**
     * تحديث صنف موجود
     */
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
            
            // تحديث البيانات
            selectedItem.setName(nameField.getText());
            selectedItem.setStock(Integer.parseInt(stockField.getText()));
            
            Section newSection = sectionComboBox.getValue();
            if (newSection != null && !newSection.equals(selectedItem.getSection())) {
                selectedItem.setSection(newSection);
            }
            
            // حفظ التحديث في قاعدة البيانات
            itemController.updateItem(selectedItem);
            
            // تحديث الجدول
            itemsTable.refresh();
            
            showSuccess("تم التحديث بنجاح");
            handleClear();
            
        } catch (Exception e) {
            showError("خطأ", "فشلت عملية التحديث: " + e.getMessage());
        }
    }
    
    /**
     * حذف صنف
     */
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
                itemsList.remove(selectedItem);
                showSuccess("تم الحذف بنجاح");
                handleClear();
            } catch (Exception e) {
                showError("خطأ", "فشلت عملية الحذف: " + e.getMessage());
            }
        }
    }
    
    /**
     * البحث النصي
     */
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadItems();
            return;
        }
        
        ObservableList<Item> filteredList = FXCollections.observableArrayList();
        for (Item item : itemController.getAllItems()) {
            if (item.getName().toLowerCase().contains(searchTerm) ||
                String.valueOf(item.getSku()).contains(searchTerm)) {
                filteredList.add(item);
            }
        }
        itemsTable.setItems(filteredList);
    }
    
    /**
     * فلترة حسب القسم
     */
    @FXML
    private void handleFilterBySection() {
        Section selectedSection = filterSectionComboBox.getValue();
        if (selectedSection == null) {
            loadItems();
            return;
        }
        
        ObservableList<Item> filteredList = FXCollections.observableArrayList(
            itemController.getItemsBySection(selectedSection.getCode())
        );
        itemsTable.setItems(filteredList);
    }
    
    /**
     * إعادة تحميل البيانات
     */
    @FXML
    private void handleRefresh() {
        loadItems();
        loadSections();
        searchField.clear();
        filterSectionComboBox.setValue(null);
    }
    
    /**
     * مسح الحقول
     */
    @FXML
    private void handleClear() {
        skuField.clear();
        nameField.clear();
        stockField.clear();
        sectionComboBox.setValue(null);
        itemsTable.getSelectionModel().clearSelection();
    }
    
    // ===== دوال مساعدة =====
    
    private void loadItems() {
        itemsList.clear();
        itemsList.addAll(itemController.getAllItems());
    }
    
    private void loadSections() {
        sectionsList.clear();
        sectionsList.addAll(sectionController.getAllSections());
    }
    
    private void populateFields(Item item) {
        skuField.setText(String.valueOf(item.getSku()));
        nameField.setText(item.getName());
        stockField.setText(String.valueOf(item.getStock()));
        sectionComboBox.setValue(item.getSection());
    }
    
    private boolean validateFields() {
        return !skuField.getText().isEmpty() &&
               !nameField.getText().isEmpty() &&
               !stockField.getText().isEmpty();
    }
    
    private void addNumericValidation(TextField field) {
        field.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
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