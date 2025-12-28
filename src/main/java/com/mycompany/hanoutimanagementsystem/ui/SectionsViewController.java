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
    
    /**
     * ✅ لا تستدعي initialize() هنا
     */
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
        String searchTerm = searchField.getText().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadSections();
            return;
        }
        
        ObservableList<Section> filteredList = FXCollections.observableArrayList();
        for (Section section : sectionController.getAllSections()) {
            if (section.getCode().toLowerCase().contains(searchTerm) ||
                section.getLabel().toLowerCase().contains(searchTerm)) {
                filteredList.add(section);
            }
        }
        sectionsTable.setItems(filteredList);
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
    }
    
    private void loadSections() {
        sectionsList.clear();
        sectionsList.addAll(sectionController.getAllSections());
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