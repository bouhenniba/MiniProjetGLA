package com.mycompany.hanoutimanagementsystem.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.mycompany.hanoutimanagementsystem.model.*;
import com.mycompany.hanoutimanagementsystem.controller.*;

public class SectionsViewController {
    
    @FXML private TextField codeField;
    @FXML private TextField labelField;
    @FXML private TextField searchField;
    
    @FXML private TableView<Section> sectionsTable;
    @FXML private TableColumn<Section, String> codeColumn;
    @FXML private TableColumn<Section, String> labelColumn;
    @FXML private TableColumn<Section, Integer> itemCountColumn;
    
    private SectionController sectionController;
    private ObservableList<Section> sectionsList;
    private ObservableList<Section> allSections; // ✅ قائمة شاملة للبحث
    
    public void setController(SectionController sectionController) {
        this.sectionController = sectionController;
    }
    
    @FXML
    public void initialize() {
        if (sectionController == null) return;
        
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        labelColumn.setCellValueFactory(new PropertyValueFactory<>("label"));
        itemCountColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(
                cellData.getValue().getItems().size()
            ).asObject()
        );
        
        sectionsList = FXCollections.observableArrayList();
        sectionsTable.setItems(sectionsList);
        
        loadSections();
        
        // ✅ إضافة Listener للبحث التلقائي
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSections(newValue);
        });
        
        sectionsTable.getSelectionModel().selectedItemProperty().addListener(
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
            
            String code = codeField.getText();
            String label = labelField.getText();
            
            sectionController.createSection(code, label);
            
            Section newSection = new Section(code, label);
            allSections.add(newSection); // ✅ إضافة للقائمة الشاملة
            sectionsList.add(newSection);
            
            showSuccess("تم إضافة القسم بنجاح");
            handleClear();
            
        } catch (Exception e) {
            showError("خطأ", "فشلت عملية الإضافة: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleUpdate() {
        Section selectedSection = sectionsTable.getSelectionModel().getSelectedItem();
        if (selectedSection == null) {
            showError("خطأ", "يرجى اختيار قسم للتحديث");
            return;
        }
        
        try {
            if (!validateFields()) {
                showError("خطأ", "يرجى ملء جميع الحقول");
                return;
            }
            
            selectedSection.setLabel(labelField.getText());
            sectionController.updateSection(selectedSection);
            sectionsTable.refresh();
            
            showSuccess("تم تحديث القسم بنجاح");
            handleClear();
            
        } catch (Exception e) {
            showError("خطأ", "فشلت عملية التحديث: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleDelete() {
        Section selectedSection = sectionsTable.getSelectionModel().getSelectedItem();
        if (selectedSection == null) {
            showError("خطأ", "يرجى اختيار قسم للحذف");
            return;
        }
        
        if (!selectedSection.getItems().isEmpty()) {
            showError("خطأ", "لا يمكن حذف قسم يحتوي على أصناف");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("تأكيد الحذف");
        confirmAlert.setHeaderText("هل أنت متأكد من حذف هذا القسم؟");
        confirmAlert.setContentText(selectedSection.getLabel());
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                sectionController.deleteSection(selectedSection.getCode());
                allSections.remove(selectedSection); // ✅ حذف من القائمة الشاملة
                sectionsList.remove(selectedSection);
                showSuccess("تم الحذف بنجاح");
                handleClear();
            } catch (Exception e) {
                showError("خطأ", "فشلت عملية الحذف: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleSearch() {
        filterSections(searchField.getText());
    }
    
    @FXML
    private void handleRefresh() {
        loadSections();
        searchField.clear();
    }
    
    @FXML
    private void handleClear() {
        codeField.clear();
        labelField.clear();
        sectionsTable.getSelectionModel().clearSelection();
        searchField.clear();
        filterSections(""); // ✅ عرض الكل
    }
    
    // ✅ دالة الفلترة الصحيحة - تستخدم setAll بدلاً من setItems
    private void filterSections(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            sectionsList.setAll(allSections); // ✅ استرجاع كل البيانات
            return;
        }
        
        String lowerCaseFilter = searchTerm.toLowerCase().trim();
        ObservableList<Section> filteredList = FXCollections.observableArrayList();
        
        for (Section section : allSections) {
            if (section.getCode().toLowerCase().contains(lowerCaseFilter) ||
                section.getLabel().toLowerCase().contains(lowerCaseFilter)) {
                filteredList.add(section);
            }
        }
        
        sectionsList.setAll(filteredList); // ✅ تحديث المحتوى فقط
    }
    
    private void loadSections() {
        allSections = FXCollections.observableArrayList(
            sectionController.getAllSections()
        );
        sectionsList.clear();
        sectionsList.addAll(allSections);
    }
    
    private void populateFields(Section section) {
        codeField.setText(section.getCode());
        labelField.setText(section.getLabel());
    }
    
    private boolean validateFields() {
        return !codeField.getText().isEmpty() && !labelField.getText().isEmpty();
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