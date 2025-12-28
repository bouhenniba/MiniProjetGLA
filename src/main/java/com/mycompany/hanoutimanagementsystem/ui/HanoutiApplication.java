package com.mycompany.hanoutimanagementsystem.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.mycompany.hanoutimanagementsystem.controller.*;
import com.mycompany.hanoutimanagementsystem.dao.*;

public class HanoutiApplication extends Application {
    
    private ItemController itemController;
    private SectionController sectionController;
    private VendorController vendorController;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // تهيئة الـ DAOs
        ItemDAO itemDAO = new ItemDAO();
        SectionDAO sectionDAO = new SectionDAO();
        VendorDAO vendorDAO = new VendorDAO();
        
        // تهيئة المتحكمات
        itemController = new ItemController(itemDAO, sectionDAO);
        sectionController = new SectionController(sectionDAO);
        vendorController = new VendorController(vendorDAO, itemDAO);
        
        // تحميل FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        loader.setControllerFactory(this::createController);
        
        Parent root = loader.load();
        
        // إعداد النافذة
        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        
        primaryStage.setTitle("نظام إدارة حنوتي - Hanouti Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }
    
    private Object createController(Class<?> controllerClass) {
        try {
            if (controllerClass == MainViewController.class) {
                return new MainViewController(itemController, sectionController, vendorController);
            }
            
            Object controller = controllerClass.getDeclaredConstructor().newInstance();
            
            if (controller instanceof ItemsViewController) {
                ((ItemsViewController) controller).setControllers(itemController, sectionController);
            } else if (controller instanceof SectionsViewController) {
                ((SectionsViewController) controller).setController(sectionController);
            } else if (controller instanceof VendorsViewController) {
                ((VendorsViewController) controller).setController(vendorController);
            } else if (controller instanceof OperationsViewController) {
                ((OperationsViewController) controller).setControllers(itemController, sectionController, vendorController);
            }
            
            return controller;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create controller: " + controllerClass.getName(), e);
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}