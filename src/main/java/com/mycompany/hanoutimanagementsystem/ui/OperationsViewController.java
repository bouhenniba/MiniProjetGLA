package com.mycompany.hanoutimanagementsystem.ui;

import com.mycompany.hanoutimanagementsystem.controller.ItemController;
import com.mycompany.hanoutimanagementsystem.controller.SectionController;
import com.mycompany.hanoutimanagementsystem.controller.VendorController;
import com.mycompany.hanoutimanagementsystem.model.Item;
import com.mycompany.hanoutimanagementsystem.model.Section;
import com.mycompany.hanoutimanagementsystem.model.Vendor;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.util.List;

public class OperationsViewController {

    // Unified View Components
    @FXML private TabPane operationsTabPane;
    @FXML private TableView<UnifiedOperationView> unifiedTable;
    @FXML private TableColumn<UnifiedOperationView, String> col1;
    @FXML private TableColumn<UnifiedOperationView, String> col2;
    @FXML private TableColumn<UnifiedOperationView, String> col3;
    @FXML private TableColumn<UnifiedOperationView, BigDecimal> col4;

    // Filter Controls
    @FXML private ComboBox<Section> sectionFilterComboBox;
    @FXML private ComboBox<Item> itemFilterComboBox;
    @FXML private ComboBox<Vendor> vendorFilterComboBox;

    // Controllers
    private ItemController itemController;
    private SectionController sectionController;
    private VendorController vendorController;

    // Observable Lists
    private ObservableList<Section> sectionsList;
    private ObservableList<Item> itemsList;
    private ObservableList<Vendor> vendorsList;
    private ObservableList<UnifiedOperationView> unifiedDataList;

    public void setControllers(ItemController itemController, SectionController sectionController, VendorController vendorController) {
        this.itemController = itemController;
        this.sectionController = sectionController;
        this.vendorController = vendorController;
    }

    @FXML
    public void initialize() {
        sectionsList = FXCollections.observableArrayList();
        itemsList = FXCollections.observableArrayList();
        vendorsList = FXCollections.observableArrayList();
        unifiedDataList = FXCollections.observableArrayList();

        unifiedTable.setItems(unifiedDataList);

        setupUnifiedTable();
        // ✅ حذف السطر الذي كان يسبب المشكلة
        // addDecimalValidation(supplyPriceField);
        setupFilterControls();
        setupTabPane();
        loadAllData();
    }

    private void setupUnifiedTable() {
        col1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        col2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        col3.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        col4.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getValue()));
    }

    private void setupFilterControls() {
        sectionFilterComboBox.setPromptText("اختر قسم");
        sectionFilterComboBox.setPrefWidth(300);
        sectionFilterComboBox.setOnAction(e -> handleSectionInventoryQuery());

        itemFilterComboBox.setPromptText("اختر صنف");
        itemFilterComboBox.setPrefWidth(350);
        itemFilterComboBox.setOnAction(e -> handleVendorComparisonQuery());

        vendorFilterComboBox.setPromptText("اختر مورد");
        vendorFilterComboBox.setPrefWidth(350);
        vendorFilterComboBox.setOnAction(e -> handleVendorCatalogQuery());
    }

    private void setupTabPane() {
        operationsTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                unifiedDataList.clear();
                updateTableColumns(newTab.getText());
            }
        });
    }

    private void updateTableColumns(String tabText) {
        if (tabText.contains("مخزون القسم")) {
            col1.setText("SKU");
            col2.setText("اسم الصنف");
            col3.setText("الكمية");
            col4.setText("السعر");
        } else if (tabText.contains("مقارنة الموردين")) {
            col1.setText("رقم الرخصة");
            col2.setText("جهة الاتصال");
            col3.setText("الصنف");
            col4.setText("سعر التوريد");
        } else if (tabText.contains("كتالوج المورد")) {
            col1.setText("SKU");
            col2.setText("اسم الصنف");
            col3.setText("القسم");
            col4.setText("السعر");
        }
    }

    private void loadAllData() {
        try {
            List<Section> sections = sectionController.getAllSections();
            List<Item> items = itemController.getAllItems();
            List<Vendor> vendors = vendorController.getAllVendors();

            sectionsList.setAll(sections);
            itemsList.setAll(items);
            vendorsList.setAll(vendors);

            sectionFilterComboBox.setItems(sectionsList);
            itemFilterComboBox.setItems(itemsList);
            vendorFilterComboBox.setItems(vendorsList);

        } catch (Exception e) {
            showError("خطأ في تحميل البيانات", e.getMessage());
        }
    }

    @FXML
    public void refreshAllData() {
        loadAllData();
        unifiedDataList.clear();
        showSuccess("تم تحديث البيانات بنجاح");
    }

    private void handleSectionInventoryQuery() {
        Section selected = sectionFilterComboBox.getValue();
        if (selected == null) return;
        try {
            List<Item> items = itemController.getItemsBySection(selected.getCode());
            unifiedDataList.clear();
            for (Item item : items) {
                unifiedDataList.add(new UnifiedOperationView(
                        String.valueOf(item.getSku()),
                        item.getName(),
                        String.valueOf(item.getStock()),
                        item.getPrice()
                ));
            }
        } catch (Exception e) {
            showError("خطأ", "فشل عرض المخزون: " + e.getMessage());
        }
    }

    private void handleVendorComparisonQuery() {
        Item selected = itemFilterComboBox.getValue();
        if (selected == null) return;
        try {
            List<com.mycompany.hanoutimanagementsystem.model.SupplyContract> contracts = 
                vendorController.getVendorsByItem(selected.getSku());
            unifiedDataList.clear();
            for (var contract : contracts) {
                unifiedDataList.add(new UnifiedOperationView(
                        contract.getVendor().getLicenseNumber(),
                        contract.getVendor().getContactName(),
                        selected.getName(),
                        contract.getSupplyPrice()
                ));
            }
            if (contracts.isEmpty()) {
                showInfo("لا توجد موردين", "لم يتم ربط أي موردين بهذا الصنف بعد.");
            }
        } catch (Exception e) {
            showError("خطأ", "فشل عرض الموردين: " + e.getMessage());
        }
    }

    private void handleVendorCatalogQuery() {
        Vendor selected = vendorFilterComboBox.getValue();
        if (selected == null) return;
        try {
            List<Item> items = vendorController.getItemsByVendor(selected.getLicenseNumber());
            unifiedDataList.clear();
            for (Item item : items) {
                unifiedDataList.add(new UnifiedOperationView(
                        String.valueOf(item.getSku()),
                        item.getName(),
                        item.getSection() != null ? item.getSection().getLabel() : "N/A",
                        item.getPrice()
                ));
            }
            if (items.isEmpty()) {
                showInfo("لا توجد أصناف", "لم يتم ربط أي أصناف بهذا المورد بعد.");
            }
        } catch (Exception e) {
            showError("خطأ", "فشل عرض الكتالوج: " + e.getMessage());
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle("نجاح");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    // Inner class for the unified table view
    public static class UnifiedOperationView {
        private final String id;
        private final String name;
        private final String description;
        private final BigDecimal value;

        public UnifiedOperationView(String id, String name, String description, BigDecimal value) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.value = value;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public BigDecimal getValue() { return value; }
    }
}