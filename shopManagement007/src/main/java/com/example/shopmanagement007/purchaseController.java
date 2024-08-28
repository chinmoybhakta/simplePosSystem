package com.example.shopmanagement007;

import com.example.shopmanagement007.model.Product;
import com.mysql.cj.protocol.x.Notice;
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
import javafx.scene.control.TableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Pattern;



public class purchaseController implements Initializable {

    @FXML
    private AnchorPane Pane;

    @FXML
    private TextField itemCode;

    @FXML
    private TextField sellerName;

    @FXML
    private TextField sellerMobile;

    @FXML
    private TableView<Product> purchaseDetails;

    @FXML
    private TableColumn<Product, String> no;

    @FXML
    private TableColumn<Product, String> code;

    @FXML
    private TableColumn<Product, String> description;

    @FXML
    private TableColumn<Product, Float> price;

    @FXML
    private TableColumn<Product, Integer> quantity;

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
    void logout_Button(ActionEvent event) {
        // To close the application
        Stage stage = (Stage)Pane.getScene().getWindow();
        stage.close();
        System.out.println("Successfully logged out");
    }

    @FXML
    void switchToSale(ActionEvent event) {
        changeScene cs = new changeScene(event, "marketSale.fxml");
        // switch to marketSale scene
    }

    @FXML
    void switchToAdd(ActionEvent event) {
        changeScene cs = new changeScene(event, "AddItem.fxml");
        // switch AddItem scene
    }

    @FXML
    void print(ActionEvent event) {
        // To print cashmemo
        // Check if sellerName, sellerMobile, or purchaseDetails (TableView) is empty
        if (sellerName.getText().isEmpty() || sellerMobile.getText().isEmpty() || purchaseDetails.getItems().isEmpty()) {
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
                new Label("Seller: " + sellerName.getText()),
                new Label("Mobile: " + sellerMobile.getText()),
                new Label("Date: " + LocalDate.now().toString()),
                new Label("================================="),
                new Label("Items Stocked:")
        );

        // Add product details to the cash memo
        for (Product product : purchaseDetails.getItems()) {
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
                        newQuantity = dbQuantity + product.getQuantity();

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
                e.printStackTrace(); // Print stack trace for better debugging
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
                        PreparedStatement ps = conn.prepareStatement("INSERT INTO purchase_details(name, mobile, purchaseDetails) VALUES(?,?,?)");
                ) {
                    ps.setString(1, sellerName.getText());
                    ps.setString(2, sellerMobile.getText());
                    ps.setString(3, details.toString());

                    int rowsInserted = ps.executeUpdate();

                    if (rowsInserted > 0) {
                        System.out.println("A new record has been inserted successfully.");
                        sellerName.clear();
                        sellerMobile.clear();
                        sd.setText("0.00");
                        less.setText("0.00");
                        cash.setText("0.00");
                        mrp.setText("0.00");
                        total.setText("0.00");
                        change.setText("0.00");
                        due.setText("0.00");

                        purchaseDetails.getItems().clear();
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
    }


    ObservableList<Product> product = FXCollections.observableArrayList();
    // Initialize ObservableList

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
        // if cursor is in itemCode and enter pressed
        if(event.getCode() == KeyCode.ENTER && itemCode.getText() != null) {
            String productCode = itemCode.getText();

            DBconnection dBconnection = new DBconnection();
            Connection conn = dBconnection.dbConnection();
            try(
                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM stock_details WHERE code = ?");
            ){
                ps.setString(1, productCode);
                ResultSet rs = ps.executeQuery();

                if(rs.next()) {
                    Product existingProduct = productExists(productCode);

                    if (existingProduct != null) {
                        // Product exists, increase its quantity
                        existingProduct.setQuantity(existingProduct.getQuantity() + 1);
                    } else {
                        // Product does not exist, add it to the list with an initial quantity of 1
                        product.add(new Product(rs.getString("code"), rs.getString("description"), rs.getString("unit"), rs.getFloat("price"), rs.getFloat("salePrice"), 1, rs.getInt("vat")));
                    }

                    setMrp(purchaseDetails, quantity, price);
                    itemCode.clear();
                }
            }catch (SQLException e){
                System.out.println("SQL EXCEPTION");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        no.setCellValueFactory(cellData -> {
            // Calculate the row number based on the index
            int rowIndex = purchaseDetails.getItems().indexOf(cellData.getValue()) + 1;
            return new SimpleStringProperty(String.valueOf(rowIndex));
        });
        no.setSortable(false);

        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        code.setCellValueFactory(new PropertyValueFactory<>("code"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        purchaseDetails.setItems(product);
        purchaseDetails.setEditable(true);

        quantity.setCellFactory(TextFieldTableCell.forTableColumn(new SafeIntegerStringConverter()));
        quantity.setOnEditCommit(this::editableQuantity);

        purchaseDetails.setOnKeyPressed(event->{
            // To delete selected item
            if (event.getCode() == KeyCode.DELETE) {
                Product selectedProduct = purchaseDetails.getSelectionModel().getSelectedItem();

                if (selectedProduct != null) {
                    purchaseDetails.getItems().remove(selectedProduct);
                    setMrp(purchaseDetails, quantity, price);
                }
            }
        });

        Pattern positiveDouble = Pattern.compile("^[+]?(0|[1-9]\\d*)(\\.\\d+)?$");
        // checks sd, less, cash is positiveDouble or not

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

    public void editableQuantity(TableColumn.CellEditEvent<Product, Integer> event) {
        Product product = event.getRowValue();
        Integer newValue = event.getNewValue();

        if (newValue != null && newValue > 0) {
            product.setQuantity(newValue);
            setMrp(purchaseDetails, quantity, price);
        } else {
            showAlert("Invalid Quantity", "Quantity must be a positive integer.");
            // Refresh the table to revert the invalid change
            purchaseDetails.refresh();
        }
    }

    private void showAlert(String title, String message) {
        // Pop up alert method
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    public void setMrp(TableView<Product> purchaseDetails, TableColumn<Product, Integer> quantity, TableColumn<Product, Float> price) {
        ObservableList<Product> items = purchaseDetails.getItems();
        Double total = 0.0;

        for (Product item : items) {
            Integer itemQuantity = item.getQuantity();
            Float itemPrice = item.getPrice();

            if (quantity != null && price != null) {
                total += itemQuantity * itemPrice;
            }
        }
        mrp.setText(String.format("%.2f", total));
        setTotalAndChange();
    }

    public void setTotalAndChange() {
        Double Net = 0.00, Change = 0.00, Due = 0.00;
        String MRP = mrp.getText();
        String SD = sd.getText();
        String LESS = less.getText();
        String CASH = cash.getText();

        if(!MRP.trim().isEmpty() && !LESS.trim().isEmpty() && !SD.trim().isEmpty()) {
            Net = Double.parseDouble(MRP) + Double.parseDouble(SD) - Double.parseDouble(LESS);
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
        // for 2 digits after decimal point
    }

}
