package com.example.shopmanagement007;

import com.example.shopmanagement007.model.Product;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class saleController implements Initializable {

    @FXML
    private TextField itemCode;

    @FXML
    private TextField customerName;

    @FXML
    private TextField customerMobile;

    @FXML
    private TableView<Product> saleDetails;

    @FXML
    private TableColumn<Product, String> noCol;

    @FXML
    private TableColumn<Product, String> codeCol;

    @FXML
    private TableColumn<Product, String> descriptionCol;

    @FXML
    private TableColumn<Product, Float> priceCol;

    @FXML
    private TableColumn<Product, Integer> quantityCol;

    @FXML
    private TableColumn<Product, Integer> vatCol;

    @FXML
    private Label mrp;

    @FXML
    private TextField sd;

    @FXML
    private Label vat;

    @FXML
    private TextField less;

    @FXML
    private Label total;

    @FXML
    private TextField cash;

    @FXML
    private Label change;

    @FXML
    private Label due;

    @FXML
    void print(ActionEvent event) {
        // Check if sellerName, sellerMobile, or purchaseDetails (TableView) is empty
        if (customerName.getText().isEmpty() || customerMobile.getText().isEmpty() || saleDetails.getItems().isEmpty()) {
            showAlert("Missing Information", "Please ensure that seller name, seller mobile, and purchase details are filled out before printing.");
            return; // Exit the method if any of these fields are empty
        }
        Integer dbQuantity = 0, newQuantity = 0;

        DBconnection dBconnection = new DBconnection();
        Connection conn = dBconnection.dbConnection();

        StringBuilder details = new StringBuilder();

        // Create a VBox to hold the cash memo details
        VBox cashMemo = new VBox(10);
        cashMemo.getChildren().addAll(
                new Label("Shop Name: XYZ Store"),
                new Label("Name: " + customerName.getText()),
                new Label("Mobile: " + customerMobile.getText()),
                new Label("Date: " + LocalDate.now().toString()),
                new Label("================================="),
                new Label("Items Purchased:")
        );

        // Add product details to the cash memo
        for (Product product : saleDetails.getItems()) {
            details.append(product.getDescription())
                    .append(" - ")
                    .append(product.getPrice())
                    .append(" x ")
                    .append(product.getQuantity())
                    .append(" ");

            cashMemo.getChildren().add(
                    new Label(product.getDescription() + " - " + product.getPrice() + " x " + product.getQuantity())
            );
            try (
                    PreparedStatement ps0 = conn.prepareStatement("SELECT quantity FROM stock_details WHERE code = ?");
            ) {
                ps0.setString(1, product.getCode());
                try (ResultSet rs0 = ps0.executeQuery()) {
                    if (rs0.next()) {
                        dbQuantity = rs0.getInt("quantity");
                        newQuantity = dbQuantity - product.getQuantity();

                        try (
                                PreparedStatement ps1 = conn.prepareStatement("UPDATE stock_details SET quantity = ? WHERE code = ?");
                        ) {
                            ps1.setInt(1, newQuantity);
                            ps1.setString(2, product.getCode());
                            ps1.executeUpdate(); // Use executeUpdate() for an UPDATE query
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println("SQL PROBLEM");
            }
        }

        details.append(" === ")
                .append("MRP:")
                .append(mrp.getText())
                .append(" SD:")
                .append(sd.getText())
                .append(" Less:")
                .append(less.getText())
                .append(" Cash:")
                .append(cash.getText())
                .append(" Change:")
                .append(change.getText())
                .append(" Due:")
                .append(due.getText());

        // Add totals to the cash memo
        cashMemo.getChildren().addAll(
                new Label("================================="),
                new Label("MRP: " + mrp.getText()),
                new Label("SD: " + sd.getText()),
                new Label("Less: " + less.getText()),
                new Label("Total: " + total.getText()),
                new Label("Cash: " + cash.getText()),
                new Label("Change: " + change.getText()),
                new Label("Due: " + due.getText())
        );

        // Create a PrinterJob
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null) {
            // Print the cash memo
            boolean success = printerJob.printPage(cashMemo);
            if (success) {
                printerJob.endJob();
                try (
                        PreparedStatement ps = conn.prepareStatement("INSERT INTO sale_details(name, mobile, saleDetails) VALUES(?,?,?)");
                ) {
                    ps.setString(1, customerName.getText());
                    ps.setString(2, customerMobile.getText());
                    ps.setString(3, details.toString());

                    int rowsInserted = ps.executeUpdate();

                    if (rowsInserted > 0) {
                        System.out.println("A new record has been inserted successfully.");
                        customerName.clear();
                        customerMobile.clear();
                        sd.setText("0.00");
                        less.setText("0.00");
                        cash.setText("0.00");
                        mrp.setText("0.00");
                        total.setText("0.00");
                        change.setText("0.00");
                        due.setText("0.00");
                        vat.setText("0.00");

                        saleDetails.getItems().clear();
                    }
                } catch (SQLException e) {
                    System.out.println("Problem in insert function: " + e.getMessage());
                } finally {
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                    } catch (SQLException e) {
                        System.out.println("Problem closing connection: " + e.getMessage());
                    }
                }


            } else {
                showAlert("Print Error", "Failed to print the cash memo.");
            }
        } else {
            showAlert("Printer Error", "No printer found.");
        }

        //print cashmemo
    }

    @FXML
    void purchase_Button(ActionEvent event) {
        changeScene cs = new changeScene(event, "marketPurchase.fxml");
        //switch to marketPurchase scene
    }

    public void editableQuantityCol(TableColumn.CellEditEvent<Product, Integer> event) {
        //set up quantiy coloumn as editing coloumn
        Product product = event.getRowValue();
        Integer newValue = event.getNewValue();

        if (newValue == null || newValue <= 0) {
            showAlert("Invalid Quantity", "Quantity must be a positive integer.");
            saleDetails.refresh();
            return;
        }

        DBconnection dBconnection = new DBconnection();
        Connection conn = dBconnection.dbConnection();
        try (
                PreparedStatement ps = conn.prepareStatement("SELECT quantity FROM stock_details WHERE code = ?");
        ) {
            ps.setString(1, product.getCode());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int availableQuantity = rs.getInt("quantity");

                    if (newValue > availableQuantity) {
                        showAlert("Quantity Exceeds Stock", "The entered quantity exceeds the available stock.");
                        saleDetails.refresh();
                    } else {
                        product.setQuantity(newValue);
                        setMrp(saleDetails, quantityCol, priceCol);
                    }
                } else {
                    showAlert("Product Not Found", "The product was not found in the stock.");
                    saleDetails.refresh();
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL EXCEPTION");
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Problem closing connection: " + e.getMessage());
            }
        }
    }


    ObservableList<Product> product = FXCollections.observableArrayList();
    //Initialize observablelist

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        noCol.setCellValueFactory(cellData -> {
            // Calculate the row number based on the index
            int rowIndex = saleDetails.getItems().indexOf(cellData.getValue()) + 1;
            return new SimpleStringProperty(String.valueOf(rowIndex));
        });
        noCol.setSortable(false);

        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        vatCol.setCellValueFactory(new PropertyValueFactory<>("vat"));

        saleDetails.setItems(product);
        saleDetails.setEditable(true);

        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new SafeIntegerStringConverter()));
        quantityCol.setOnEditCommit(this::editableQuantityCol);

        saleDetails.setOnKeyPressed(event->{
            if (event.getCode() == KeyCode.DELETE) {
                Product selectedProduct = saleDetails.getSelectionModel().getSelectedItem();

                if (selectedProduct != null) {
                    saleDetails.getItems().remove(selectedProduct);
                    setMrp(saleDetails, quantityCol, priceCol);
                }
            }
        });

        Pattern positiveDouble = Pattern.compile("^[+]?(0|[1-9]\\d*)(\\.\\d+)?$");
        //check if sd, less and cash is positiveDouble or not

        sd.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { //if Focus lost
                if (sd.getText().isEmpty()) {
                    sd.setText("0.00");
                }
                setTotalAndChange();
            }
        });

        sd.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (positiveDouble.matcher(sd.getText()).matches()) {
                    setTotalAndChange();
                } else {
                    sd.setText("0.00");
                }
            }
        });

        less.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { //if Focus lost
                if (less.getText().isEmpty()) {
                    less.setText("0.00");
                }
                setTotalAndChange();
            }
        });

        less.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (positiveDouble.matcher(less.getText()).matches()) {
                    setTotalAndChange();
                } else {
                    less.setText("0.00");
                }
            }
        });

        cash.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { //if Focus lost
                if (cash.getText().isEmpty()) {
                    cash.setText("0.00");
                }
                setTotalAndChange();
            }
        });

        cash.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (positiveDouble.matcher(cash.getText()).matches()) {
                    setTotalAndChange();
                } else {
                    cash.setText("0.00");
                }
            }
        });
    }

    private Product productExists(String productCode) {
        for (Product p : product) {
            if (p.getCode().equals(productCode)) {
                return p; // Return the existing product
            }
        }
        return null; // Product not found
    }

    @FXML
    void enterPressed(KeyEvent event) {
        // if cursor in itemCode and enter pressed

        if (event.getCode() == KeyCode.ENTER && itemCode.getText() != null && !itemCode.getText().trim().isEmpty()) {
            String productCode = itemCode.getText().trim();

            DBconnection dBconnection = new DBconnection();

            try (Connection conn = dBconnection.dbConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM stock_details WHERE code = ?")) {

                ps.setString(1, productCode);
                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        int availableQuantity = rs.getInt("quantity");

                        if (availableQuantity < 1) {
                            showAlert("Out of Stock", "The product '" + rs.getString("description") + "' is out of stock and cannot be added.");
                        } else {
                            Product existingProduct = productExists(productCode);

                            if (existingProduct != null) {
                                // Check if increasing quantity exceeds stock
                                if (existingProduct.getQuantity() + 1 > availableQuantity) {
                                    showAlert("Quantity Exceeds Stock", "Cannot add more of '" + existingProduct.getDescription() + "' than available in stock.");
                                } else {
                                    // Product exists, increase its quantity
                                    existingProduct.setQuantity(existingProduct.getQuantity() + 1);
                                }
                            } else {
                                // Product does not exist, add it to the list with an initial quantity of 1
                                product.add(new Product(
                                        rs.getString("code"),
                                        rs.getString("description"),
                                        rs.getString("unit"),
                                        rs.getFloat("price"),
                                        rs.getFloat("salePrice"),
                                        1,
                                        rs.getInt("vat")
                                ));
                            }

                            setMrp(saleDetails, quantityCol, priceCol);
                            itemCode.clear();
                        }
                    } else {
                        showAlert("Product Not Found", "No product found with the code: " + productCode);
                    }
                }

            } catch (SQLException e) {
                System.out.println("SQL EXCEPTION: " + e.getMessage());
                showAlert("Database Error", "An error occurred while accessing the database.");
            }
        }
    }


    private void showAlert(String title, String message) {
        // pop up alert method
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    public void setMrp(TableView<Product> saleDetails, TableColumn<Product, Integer> quantityCol, TableColumn<Product, Float> priceCol) {
        ObservableList<Product> items = saleDetails.getItems();
        Double total = 0.0, Vat = 0.0;

        for (Product item : items) {
            Integer itemQuantity = item.getQuantity();
            Float itemPrice = item.getPrice();

            if (quantityCol != null && priceCol != null) {
                total += itemQuantity * itemPrice;
                Vat += itemQuantity*itemPrice*(item.getVat())/100;
            }
        }
        mrp.setText(String.format("%.2f", total));
        vat.setText(String.format("%.2f", Vat));
        setTotalAndChange();
    }

    public void setTotalAndChange() {
        Double Net = 0.00, Change = 0.00, Due = 0.00;
        String MRP = mrp.getText();
        String SD = sd.getText();
        String LESS = less.getText();
        String CASH = cash.getText();
        String VAT = vat.getText();

        if(!MRP.trim().isEmpty() && !LESS.trim().isEmpty() && !SD.trim().isEmpty()) {
            Net = Double.parseDouble(MRP) + Double.parseDouble(SD) + Double.parseDouble(VAT) - Double.parseDouble(LESS);
        }
        if(!CASH.trim().isEmpty()) {
            if(Double.parseDouble(CASH) > Net) {
                Change = Double.parseDouble(CASH) - Net;
            }
            else {
                Due = Net - Double.parseDouble(CASH);
            }
        }

        total.setText(String.format("%.2f", Net));
        change.setText(String.format("%.2f", Math.rint(Change)));
        due.setText(String.format("%.2f", Math.floor(Due)));
        // for 2 digit after decimal point
    }

}
