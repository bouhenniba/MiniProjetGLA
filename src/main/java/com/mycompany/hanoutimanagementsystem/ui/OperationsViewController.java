package com.mycompany.hanoutimanagementsystem.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.mycompany.hanoutimanagementsystem.model.*;
import com.mycompany.hanoutimanagementsystem.controller.*;
import java.util.List;
import java.util.Set;

/**
 * متحكم واجهة السيناريوهات التشغيلية
 * يدير ثلاث سيناريوهات: مخزون القسم، مقارنة الموردين، كتالوج المورد
 */
public class OperationsViewController {
    
    // ===== السيناريو الأول: مخزون القسم =====
    @FXML private ComboBox<Section> sectionInventoryComboBox;
    @FXML private TableView<Item> sectionInventoryTable;
    @FXML private TableColumn<Item, Integer> invSkuColumn;
    @FXML private TableColumn<Item, String> invNameColumn;
    @FXML private TableColumn<Item, Integer> invStockColumn;
    
    // ===== السيناريو الثاني: مقارنة الموردين =====
    @FXML private ComboBox<Item> itemComparisonComboBox;
    @FXML private TableView<Vendor> vendorComparisonTable;
    @FXML private TableColumn<Vendor, String> compLicenseColumn;
    @FXML private TableColumn<Vendor, String> compContactColumn;
    
    // ===== السيناريو الثالث: كتالوج المورد =====
    @FXML private ComboBox<Vendor> vendorCatalogComboBox;
    @FXML private TableView<Item> vendorCatalogTable;
    @FXML private TableColumn<Item, Integer> catSkuColumn;
    @FXML private TableColumn<Item, String> catNameColumn;
    @FXML private TableColumn<Item, String> catSectionColumn;
    
    // المتحكمات الخلفية
    private ItemController itemController;
    private SectionController sectionController;
    private VendorController vendorController;
    
    // القوائم المرصودة
    private ObservableList<Section> sectionsList;
    private ObservableList<Item> itemsList;
    private ObservableList<Vendor> vendorsList;
    
    /**
     * تعيين المتحكمات الخلفية
     * ✅ لا تستدعي initialize() هنا
     */
    public void setControllers(ItemController itemController, 
                               SectionController sectionController,
                               VendorController vendorController) {
        this.itemController = itemController;
        this.sectionController = sectionController;
        this.vendorController = vendorController;
    }
    
    @FXML
    public void initialize() {
        if (itemController == null || sectionController == null || vendorController == null) {
            return; // انتظار حقن المتحكمات
        }
        
        // تهيئة السيناريو الأول: مخزون القسم
        initializeSectionInventory();
        
        // تهيئة السيناريو الثاني: مقارنة الموردين
        initializeVendorComparison();
        
        // تهيئة السيناريو الثالث: كتالوج المورد
        initializeVendorCatalog();
        
        // تحميل البيانات الأولية
        loadAllData();
    }
    
    /**
     * تهيئة السيناريو الأول: مخزون القسم
     */
    private void initializeSectionInventory() {
        invSkuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));
        invNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        invStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        
        sectionsList = FXCollections.observableArrayList();
        sectionInventoryComboBox.setItems(sectionsList);
        
        // تنسيق عرض القسم في ComboBox
        sectionInventoryComboBox.setCellFactory(param -> new ListCell<Section>() {
            @Override
            protected void updateItem(Section item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCode() + " - " + item.getLabel());
                }
            }
        });
        sectionInventoryComboBox.setButtonCell(new ListCell<Section>() {
            @Override
            protected void updateItem(Section item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCode() + " - " + item.getLabel());
                }
            }
        });
    }
    
    /**
     * تهيئة السيناريو الثاني: مقارنة الموردين
     */
    private void initializeVendorComparison() {
        compLicenseColumn.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));
        compContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        
        itemsList = FXCollections.observableArrayList();
        itemComparisonComboBox.setItems(itemsList);
        
        // تنسيق عرض الصنف في ComboBox
        itemComparisonComboBox.setCellFactory(param -> new ListCell<Item>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getSku() + " - " + item.getName());
                }
            }
        });
        itemComparisonComboBox.setButtonCell(new ListCell<Item>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getSku() + " - " + item.getName());
                }
            }
        });
    }
    
    /**
     * تهيئة السيناريو الثالث: كتالوج المورد
     */
    private void initializeVendorCatalog() {
        catSkuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));
        catNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        catSectionColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getSection().getLabel()
            )
        );
        
        vendorsList = FXCollections.observableArrayList();
        vendorCatalogComboBox.setItems(vendorsList);
        
        // تنسيق عرض المورد في ComboBox
        vendorCatalogComboBox.setCellFactory(param -> new ListCell<Vendor>() {
            @Override
            protected void updateItem(Vendor item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getLicenseNumber() + " - " + item.getContactName());
                }
            }
        });
        vendorCatalogComboBox.setButtonCell(new ListCell<Vendor>() {
            @Override
            protected void updateItem(Vendor item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getLicenseNumber() + " - " + item.getContactName());
                }
            }
        });
    }
    
    /**
     * تحميل جميع البيانات
     */
    private void loadAllData() {
        sectionsList.clear();
        sectionsList.addAll(sectionController.getAllSections());
        
        itemsList.clear();
        itemsList.addAll(itemController.getAllItems());
        
        vendorsList.clear();
        vendorsList.addAll(vendorController.getAllVendors());
    }
    
    /**
     * السيناريو الأول: عرض مخزون القسم المحدد
     */
    @FXML
    private void handleSectionInventoryQuery() {
        Section selectedSection = sectionInventoryComboBox.getValue();
        if (selectedSection == null) {
            sectionInventoryTable.setItems(FXCollections.observableArrayList());
            return;
        }
        
        // استخدام المتحكم للحصول على أصناف القسم
        List<Item> sectionItems = itemController.getItemsBySection(selectedSection.getCode());
        ObservableList<Item> items = FXCollections.observableArrayList(sectionItems);
        sectionInventoryTable.setItems(items);
    }
    
    /**
     * السيناريو الثاني: عرض الموردين الذين يوفرون الصنف المحدد
     */
    @FXML
    private void handleVendorComparisonQuery() {
        Item selectedItem = itemComparisonComboBox.getValue();
        if (selectedItem == null) {
            vendorComparisonTable.setItems(FXCollections.observableArrayList());
            return;
        }
        
        // الحصول على الموردين الذين يوفرون هذا الصنف
        List<Vendor> vendorsForItem = vendorController.getVendorsByItem(selectedItem.getSku());
        ObservableList<Vendor> vendors = FXCollections.observableArrayList(vendorsForItem);
        vendorComparisonTable.setItems(vendors);
    }
    
    /**
     * السيناريو الثالث: عرض كتالوج المورد (كافة الأصناف التي يوفرها)
     */
    @FXML
    private void handleVendorCatalogQuery() {
        Vendor selectedVendor = vendorCatalogComboBox.getValue();
        if (selectedVendor == null) {
            vendorCatalogTable.setItems(FXCollections.observableArrayList());
            return;
        }
        
        // الحصول على جميع الأصناف التي يوفرها المورد
        Set<Item> suppliedItems = selectedVendor.getItems();
        ObservableList<Item> items = FXCollections.observableArrayList(suppliedItems);
        vendorCatalogTable.setItems(items);
    }
}