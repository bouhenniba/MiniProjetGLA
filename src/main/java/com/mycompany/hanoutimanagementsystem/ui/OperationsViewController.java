package com.mycompany.hanoutimanagementsystem.ui;

import com.mycompany.hanoutimanagementsystem.controller.ItemController;
import com.mycompany.hanoutimanagementsystem.controller.SectionController;
import com.mycompany.hanoutimanagementsystem.controller.VendorController;
import com.mycompany.hanoutimanagementsystem.model.Item;
import com.mycompany.hanoutimanagementsystem.model.Section;
import com.mycompany.hanoutimanagementsystem.model.SupplyContract;
import com.mycompany.hanoutimanagementsystem.model.Vendor;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

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

    // Item-Vendor Management
    @FXML private ComboBox<Item> manageItemComboBox;
    @FXML private ComboBox<Vendor> manageVendorComboBox;
    @FXML private TextField supplyPriceField;
    @FXML private ListView<SupplyContract> currentVendorsListView;

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
        addDecimalValidation(supplyPriceField);
        setupFilterControls();
        setupTabPane();
        setupManagementComboBoxes();
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
        if (tabText.contains("Section Inventory")) {
            col1.setText("SKU");
            col2.setText("اسم الصنف");
            col3.setText("الكمية");
            col4.setText("السعر");
        } else if (tabText.contains("Vendor Comparison")) {
            col1.setText("رقم الرخصة");
            col2.setText("جهة الاتصال");
            col3.setText("الصنف");
            col4.setText("سعر التوريد");
        } else if (tabText.contains("Vendor Catalog")) {
            col1.setText("SKU");
            col2.setText("اسم الصنف");
            col3.setText("القسم");
            col4.setText("السعر");
        }
    }

    private void setupManagementComboBoxes() {
        manageItemComboBox.setItems(itemsList);
        manageVendorComboBox.setItems(vendorsList);

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

        if (manageItemComboBox != null) {
            manageItemComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    updateCurrentVendorsList(newVal);
                } else {
                    currentVendorsListView.setItems(FXCollections.observableArrayList());
                }
            });
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
        manageItemComboBox.setValue(null);
        manageVendorComboBox.setValue(null);
        supplyPriceField.clear();
        currentVendorsListView.setItems(FXCollections.observableArrayList());
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
            List<SupplyContract> contracts = vendorController.getVendorsByItem(selected.getSku());
            unifiedDataList.clear();
            for (SupplyContract contract : contracts) {
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

    @FXML
    private void handleAddVendorToItem() {
        Item item = manageItemComboBox.getValue();
        Vendor vendor = manageVendorComboBox.getValue();
        String priceText = supplyPriceField.getText();

        if (item == null || vendor == null || priceText.trim().isEmpty()) {
            showError("خطأ", "يرجى اختيار صنف ومورد وإدخال سعر التوريد");
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceText);
            itemController.addVendorToItem(item.getSku(), vendor.getLicenseNumber(), price);
            updateCurrentVendorsList(item);
            showSuccess("✅ تم ربط المورد بالصنف بنجاح");
            supplyPriceField.clear();
            loadAllData();
        } catch (NumberFormatException e) {
            showError("خطأ", "صيغة السعر غير صحيحة");
        } catch (Exception e) {
            showError("خطأ", "فشلت عملية الربط: " + e.getMessage());
        }
    }

    @FXML
    private void handleRemoveVendorFromItem() {
        Item item = manageItemComboBox.getValue();
        SupplyContract contract = currentVendorsListView.getSelectionModel().getSelectedItem();

        if (item == null || contract == null) {
            showError("خطأ", "يرجى اختيار مورد من القائمة");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "هل أنت متأكد من إزالة هذا المورد؟", ButtonType.OK, ButtonType.CANCEL);
        confirmAlert.setTitle("تأكيد الإزالة");
        confirmAlert.setHeaderText(contract.getVendor().getContactName());

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                itemController.removeVendorFromItem(item.getSku(), contract.getVendor().getLicenseNumber());
                updateCurrentVendorsList(item);
                showSuccess("✅ تم إزالة المورد من الصنف");
                loadAllData();
            } catch (Exception e) {
                showError("خطأ", "فشلت عملية الإزالة: " + e.getMessage());
            }
        }
    }

    private void updateCurrentVendorsList(Item item) {
        if (currentVendorsListView == null || item == null) return;
        try {
            Item refreshed = itemController.findItem(item.getSku());
            if (refreshed != null) {
                currentVendorsListView.setItems(FXCollections.observableArrayList(refreshed.getVendorSupplies()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addDecimalValidation(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                textField.setText(oldValue);
            }
        });
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
