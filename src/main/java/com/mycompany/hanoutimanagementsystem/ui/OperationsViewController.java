package com.mycompany.hanoutimanagementsystem.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.mycompany.hanoutimanagementsystem.model.*;
import com.mycompany.hanoutimanagementsystem.controller.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * متحكم واجهة السيناريوهات التشغيلية - النسخة الكاملة
 */
public class OperationsViewController {
    
    // ===== السيناريو الأول: مخزون القسم =====
    @FXML private ComboBox<Section> sectionInventoryComboBox;
    @FXML private TableView<Item> sectionInventoryTable;
    @FXML private TableColumn<Item, Long> invSkuColumn;
    @FXML private TableColumn<Item, String> invNameColumn;
    @FXML private TableColumn<Item, Integer> invStockColumn;
    @FXML private TableColumn<Item, BigDecimal> invPriceColumn;
    
    // ===== السيناريو الثاني: مقارنة الموردين =====
    @FXML private ComboBox<Item> itemComparisonComboBox;
    @FXML private TableView<Vendor> vendorComparisonTable;
    @FXML private TableColumn<Vendor, String> compLicenseColumn;
    @FXML private TableColumn<Vendor, String> compContactColumn;
    
    // ===== السيناريو الثالث: كتالوج المورد =====
    @FXML private ComboBox<Vendor> vendorCatalogComboBox;
    @FXML private TableView<Item> vendorCatalogTable;
    @FXML private TableColumn<Item, Long> catSkuColumn;
    @FXML private TableColumn<Item, String> catNameColumn;
    @FXML private TableColumn<Item, String> catSectionColumn;
    @FXML private TableColumn<Item, BigDecimal> catPriceColumn;
    
    // ===== إدارة علاقة Item-Vendor =====
    @FXML private ComboBox<Item> manageItemComboBox;
    @FXML private ComboBox<Vendor> manageVendorComboBox;
    @FXML private ListView<Vendor> currentVendorsListView;
    
    // المتحكمات الخلفية
    private ItemController itemController;
    private SectionController sectionController;
    private VendorController vendorController;
    
    // القوائم المرصودة
    private ObservableList<Section> sectionsList;
    private ObservableList<Item> itemsList;
    private ObservableList<Vendor> vendorsList;
    
    /**
     * ✅ تعيين المتحكمات
     */
    public void setControllers(ItemController itemController, 
                               SectionController sectionController,
                               VendorController vendorController) {
        this.itemController = itemController;
        this.sectionController = sectionController;
        this.vendorController = vendorController;
        
        if (sectionInventoryTable != null) {
            initializeAfterInjection();
        }
    }
    
    /**
     * ✅ FXML initialize
     */
    @FXML
    public void initialize() {
        System.out.println("🔍 OperationsViewController.initialize() called");
        
        setupTableColumns();
        
        if (itemController != null && sectionController != null && vendorController != null) {
            initializeAfterInjection();
        }
    }
    
    /**
     * ✅ تهيئة بعد حقن المتحكمات
     */
    private void initializeAfterInjection() {
        System.out.println("✅ Initializing with injected controllers");
        
        sectionsList = FXCollections.observableArrayList();
        itemsList = FXCollections.observableArrayList();
        vendorsList = FXCollections.observableArrayList();
        
        setupComboBoxes();
        loadAllData();
    }
    
    /**
     * إعداد أعمدة الجداول
     */
    private void setupTableColumns() {
        // مخزون القسم
        invSkuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));
        invNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        invStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        invPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        // مقارنة الموردين
        compLicenseColumn.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));
        compContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        
        // كتالوج المورد
        catSkuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));
        catNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        catSectionColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getSection() != null ? 
                cellData.getValue().getSection().getLabel() : "N/A"
            )
        );
        catPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    }
    
    /**
     * إعداد ComboBoxes
     */
    private void setupComboBoxes() {
        // Section ComboBox
        sectionInventoryComboBox.setItems(sectionsList);
        sectionInventoryComboBox.setCellFactory(param -> new ListCell<Section>() {
            @Override
            protected void updateItem(Section item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });
        sectionInventoryComboBox.setButtonCell(new ListCell<Section>() {
            @Override
            protected void updateItem(Section item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });
        
        // Item ComboBoxes
        itemComparisonComboBox.setItems(itemsList);
        manageItemComboBox.setItems(itemsList);
        
        ListCell<Item> itemCellFactory = new ListCell<Item>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        };
        
        itemComparisonComboBox.setCellFactory(param -> new ListCell<Item>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });
        itemComparisonComboBox.setButtonCell(new ListCell<Item>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });
        
        manageItemComboBox.setCellFactory(param -> new ListCell<Item>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });
        manageItemComboBox.setButtonCell(new ListCell<Item>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });
        
        // Vendor ComboBoxes
        vendorCatalogComboBox.setItems(vendorsList);
        manageVendorComboBox.setItems(vendorsList);
        
        vendorCatalogComboBox.setCellFactory(param -> new ListCell<Vendor>() {
            @Override
            protected void updateItem(Vendor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });
        vendorCatalogComboBox.setButtonCell(new ListCell<Vendor>() {
            @Override
            protected void updateItem(Vendor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });
        
        manageVendorComboBox.setCellFactory(param -> new ListCell<Vendor>() {
            @Override
            protected void updateItem(Vendor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });
        manageVendorComboBox.setButtonCell(new ListCell<Vendor>() {
            @Override
            protected void updateItem(Vendor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });
        
        // ✅ Listener لتحديث قائمة الموردين
        if (manageItemComboBox != null && currentVendorsListView != null) {
            manageItemComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    updateCurrentVendorsList(newVal);
                } else {
                    currentVendorsListView.setItems(FXCollections.observableArrayList());
                }
            });
        }
    }
    
    /**
     * ✅ تحميل جميع البيانات
     */
    private void loadAllData() {
        try {
            System.out.println("📊 جاري تحميل البيانات...");
            
            List<Section> sections = sectionController.getAllSections();
            List<Item> items = itemController.getAllItems();
            List<Vendor> vendors = vendorController.getAllVendors();
            
            sectionsList.setAll(sections);
            itemsList.setAll(items);
            vendorsList.setAll(vendors);
            
            System.out.println("✅ تم التحميل - Sections: " + sections.size() +
                             ", Items: " + items.size() +
                             ", Vendors: " + vendors.size());
            
            // ✅ طباعة تفاصيل العلاقات للتشخيص
            for (Item item : items) {
                System.out.println("  📦 " + item.getName() + 
                                 " - موردين: " + item.getVendors().size());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("خطأ في تحميل البيانات", e.getMessage());
        }
    }
    
    /**
     * ✅ تحديث جميع البيانات - دالة عامة
     */
    @FXML
    public void refreshAllData() {
        loadAllData();
        
        // إعادة تعيين الـ ComboBoxes
        sectionInventoryComboBox.setValue(null);
        itemComparisonComboBox.setValue(null);
        vendorCatalogComboBox.setValue(null);
        manageItemComboBox.setValue(null);
        manageVendorComboBox.setValue(null);
        
        // مسح الجداول
        sectionInventoryTable.setItems(FXCollections.observableArrayList());
        vendorComparisonTable.setItems(FXCollections.observableArrayList());
        vendorCatalogTable.setItems(FXCollections.observableArrayList());
        currentVendorsListView.setItems(FXCollections.observableArrayList());
        
        showSuccess("تم تحديث البيانات بنجاح");
        System.out.println("✅ تم تحديث جميع البيانات");
    }
    
    // ================= Event Handlers =================
    
    /**
     * السيناريو 1: عرض مخزون القسم
     */
    @FXML
    private void handleSectionInventoryQuery() {
        Section selected = sectionInventoryComboBox.getValue();
        if (selected == null) {
            sectionInventoryTable.setItems(FXCollections.observableArrayList());
            return;
        }
        
        try {
            List<Item> items = itemController.getItemsBySection(selected.getCode());
            sectionInventoryTable.setItems(FXCollections.observableArrayList(items));
            System.out.println("✅ عرض " + items.size() + " صنف للقسم: " + selected.getLabel());
        } catch (Exception e) {
            showError("خطأ", "فشل عرض المخزون: " + e.getMessage());
        }
    }
    
    /**
     * السيناريو 2: مقارنة الموردين لصنف معين
     */
    @FXML
    private void handleVendorComparisonQuery() {
        Item selected = itemComparisonComboBox.getValue();
        if (selected == null) {
            vendorComparisonTable.setItems(FXCollections.observableArrayList());
            return;
        }
        
        try {
            List<Vendor> vendors = vendorController.getVendorsByItem(selected.getSku());
            vendorComparisonTable.setItems(FXCollections.observableArrayList(vendors));
            System.out.println("✅ عرض " + vendors.size() + " مورد للصنف: " + selected.getName());
            
            if (vendors.isEmpty()) {
                showInfo("لا توجد موردين", 
                    "لم يتم ربط أي موردين بهذا الصنف بعد.\n" +
                    "استخدم قسم 'إدارة العلاقات' لربط موردين.");
            }
        } catch (Exception e) {
            showError("خطأ", "فشل عرض الموردين: " + e.getMessage());
        }
    }
    
    /**
     * السيناريو 3: عرض كتالوج المورد
     */
    @FXML
    private void handleVendorCatalogQuery() {
        Vendor selected = vendorCatalogComboBox.getValue();
        if (selected == null) {
            vendorCatalogTable.setItems(FXCollections.observableArrayList());
            return;
        }
        
        try {
            List<Item> items = vendorController.getItemsByVendor(selected.getLicenseNumber());
            vendorCatalogTable.setItems(FXCollections.observableArrayList(items));
            System.out.println("✅ عرض " + items.size() + " صنف للمورد: " + selected.getContactName());
            
            if (items.isEmpty()) {
                showInfo("لا توجد أصناف", 
                    "لم يتم ربط أي أصناف بهذا المورد بعد.\n" +
                    "استخدم قسم 'إدارة العلاقات' لربط أصناف.");
            }
        } catch (Exception e) {
            showError("خطأ", "فشل عرض الكتالوج: " + e.getMessage());
        }
    }
    
    /**
     * ✅ إضافة مورد لصنف
     */
    @FXML
    private void handleAddVendorToItem() {
        Item item = manageItemComboBox.getValue();
        Vendor vendor = manageVendorComboBox.getValue();
        
        if (item == null || vendor == null) {
            showError("خطأ", "يرجى اختيار صنف ومورد");
            return;
        }
        
        try {
            itemController.addVendorToItem(item.getSku(), vendor.getLicenseNumber());
            updateCurrentVendorsList(item);
            showSuccess("✅ تم ربط المورد بالصنف بنجاح");
            
            // ✅ تحديث القوائم
            loadAllData();
            
        } catch (IllegalArgumentException e) {
            showError("خطأ", e.getMessage());
        } catch (Exception e) {
            showError("خطأ", "فشلت عملية الربط: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * ✅ إزالة مورد من صنف
     */
    @FXML
    private void handleRemoveVendorFromItem() {
        Item item = manageItemComboBox.getValue();
        Vendor vendor = currentVendorsListView.getSelectionModel().getSelectedItem();
        
        if (item == null || vendor == null) {
            showError("خطأ", "يرجى اختيار مورد من القائمة");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("تأكيد الإزالة");
        confirmAlert.setHeaderText("هل أنت متأكد من إزالة هذا المورد؟");
        confirmAlert.setContentText(vendor.getContactName());
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                itemController.removeVendorFromItem(item.getSku(), vendor.getLicenseNumber());
                updateCurrentVendorsList(item);
                showSuccess("✅ تم إزالة المورد من الصنف");
                
                // ✅ تحديث القوائم
                loadAllData();
                
            } catch (Exception e) {
                showError("خطأ", "فشلت عملية الإزالة: " + e.getMessage());
            }
        }
    }
    
    /**
     * ✅ تحديث قائمة الموردين الحاليين للصنف
     */
    private void updateCurrentVendorsList(Item item) {
        if (currentVendorsListView == null || item == null) return;
        
        try {
            // ✅ إعادة جلب الصنف من قاعدة البيانات بموردينه
            Item refreshed = itemController.findItem(item.getSku());
            if (refreshed != null) {
                ObservableList<Vendor> vendors = FXCollections.observableArrayList(
                    refreshed.getVendors()
                );
                currentVendorsListView.setItems(vendors);
                System.out.println("✅ تم تحديث القائمة - موردين: " + vendors.size());
            }
        } catch (Exception e) {
            System.err.println("❌ خطأ في تحديث القائمة: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ================= Utility Methods =================
    
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
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}