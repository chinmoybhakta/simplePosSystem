package com.example.shopmanagement007;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class AddItemController implements Initializable {

    @FXML
    private TextField code;

    @FXML
    private TextField description;

    @FXML
    private ChoiceBox<String> unit;
    String[] unitName = {"kg", "g", "mg", "Pound", "Box", "Cartoon", "piece", "Drum", "Litr", "Gallon", "Set"};
    //In this choicebox we can add more unit

    @FXML
    private TextField unitPrice;

    @FXML
    private TextField unitSellingPrice;

    @FXML
    private TextField quantity;

    @FXML
    private TextField vat;


    @FXML
    void switchPurchaseScene(ActionEvent event) {
        changeScene cs = new changeScene(event, "marketPurchase.fxml");
    }

    @FXML
    void submit(ActionEvent event) {
        DBconnection dBconnection = new DBconnection();

        String Code = code.getText();
        String Description = description.getText();
        String UnitName = unit.getValue();
        String Price = unitPrice.getText();
        String SalePrice = unitSellingPrice.getText();
        String Quantity = quantity.getText();
        String Vat = vat.getText();

        Pattern positiveInteger = Pattern.compile("^\\d*$");
        //REGEX of positive integer
        Pattern positiveDouble = Pattern.compile("^[+]?(0|[1-9]\\d*)(\\.\\d+)?$");
        //REGEX of positive double

        if(Objects.equals(Code, "") || Objects.equals(Description, "") || Objects.equals(Price, "") || Objects.equals(Quantity, "")) {
            Alert msg = new Alert(Alert.AlertType.WARNING);
            msg.setTitle("Null Value Entered!");
            msg.setContentText("Please fill up the above fields.");
            msg.show();
        }

        else if(positiveDouble.matcher(Price).matches() && positiveInteger.matcher(Quantity).matches() && positiveInteger.matcher(Vat).matches()) {
            dBconnection.insertData(Description, Code, UnitName, Price, SalePrice, Quantity, Vat);

            code.clear();
            description.clear();
            unitPrice.clear();
            unitSellingPrice.clear();
            quantity.clear();
            vat.clear();
            //If Vat, Price & Quantity are valid then all the textfield will be clear for a new product entry

            Alert msg = new Alert(Alert.AlertType.INFORMATION);
            msg.setTitle("Successful!");
            msg.setContentText("Your item successfully added to database.");
            msg.show();
            //pop up successful message
        }

        else{
            Alert msg = new Alert(Alert.AlertType.WARNING);
            msg.setTitle("Wrong Value Entered!");
            msg.setContentText("Please fill up the above fields correctly!!!\nPrice must be float or integer and Quantity must be integer.");
            msg.show();
            //pop up warning message
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        unit.getItems().addAll(unitName); //choicebox initialization
    }

    DBconnection dBconnection = new DBconnection();
    Connection conn = dBconnection.dbConnection();
    //Connect with database

    @FXML
    void savePurchase(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Date");
        dialog.setHeaderText("Please Enter a valid Date");
        dialog.setContentText("Format: DD-MM-YY");

        Optional<String> result = dialog.showAndWait();

        // Only proceed if the user provided input
        if (result.isPresent()) {
            String userInput = result.get();  // Get the input from the dialog

            VBox memoBox = new VBox(10);
            memoBox.getChildren().add(new Label("- PURCHASE DETAILS -"));

            try {
                if (conn == null) {
                    throw new IllegalStateException("Database connection is not initialized");
                }

                // Query assuming the user input is a date
                String query = "SELECT * FROM purchase_details WHERE DATE(Date) = STR_TO_DATE(?, \"%d-%m-%y\")";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, userInput);

                ResultSet resultSet = ps.executeQuery();

                int i = 1;
                while (resultSet.next()) {
                    String productDate = resultSet.getString("Date");
                    String sellerName = resultSet.getString("name");
                    String sellerMobile = resultSet.getString("mobile");
                    String purchaseDetail = resultSet.getString("purchaseDetails");

                    Label label = new Label();
                    label.setText("No " + (i++) + " - " + productDate + " - " + sellerName + " - " + sellerMobile + " - " + purchaseDetail);
                    label.setWrapText(true);
                    label.setMaxWidth(500);
                    memoBox.getChildren().add(label);
                }

                // Printing the memoBox
                PrinterJob job = PrinterJob.createPrinterJob();
                if (job != null) {
                    boolean success = job.printPage(memoBox);
                    if (success) {
                        job.endJob();
                    } else {
                        System.out.println("Printing failed.");
                    }
                } else {
                    System.out.println("Could not create PrinterJob.");
                }

            } catch (SQLException e) {
                System.out.println("SQL EXCEPTION");
            } catch (IllegalStateException e) {
                System.out.println("ILLEGAL STATEMENT EXCEPTION");
            }
        } else {
            // Handle the case where the user did not enter anything
            System.out.println("No input was provided.");
        }
    }



    @FXML
    void saveSale(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Date");
        dialog.setHeaderText("Please Enter a valid Date");
        dialog.setContentText("Format: DD-MM-YY");

        Optional<String> result = dialog.showAndWait();

        // Only proceed if the user provided input
        if (result.isPresent()) {
            String userInput = result.get();  // Get the input from the dialog

            VBox memoBox = new VBox(10);
            memoBox.getChildren().add(new Label("- SALE DETAILS -   DATE: " + userInput));

            try {
                if (conn == null) {
                    throw new IllegalStateException("Database connection is not initialized");
                }

                // Query assuming the user input is a date
                String query = "SELECT * FROM sale_details WHERE DATE(Date) = STR_TO_DATE(?, \"%d-%m-%y\")";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, userInput);

                ResultSet resultSet = ps.executeQuery();

                int i = 1;
                while (resultSet.next()) {
                    String productDate = resultSet.getString("Date");
                    String customerName = resultSet.getString("name");
                    String customerMobile = resultSet.getString("mobile");
                    String saleDetail = resultSet.getString("saleDetails");

                    Label label = new Label();
                    label.setText("No " + (i++) + " - " + productDate + " - " + customerName + " - " + customerMobile + " - " + saleDetail);
                    label.setWrapText(true);
                    label.setMaxWidth(500);
                    memoBox.getChildren().add(label);
                }

                // Printing the memoBox
                PrinterJob job = PrinterJob.createPrinterJob();
                if (job != null) {
                    boolean success = job.printPage(memoBox);
                    if (success) {
                        job.endJob();
                    } else {
                        System.out.println("Printing failed.");
                    }
                } else {
                    System.out.println("Could not create PrinterJob.");
                }

            } catch (SQLException e) {
                System.out.println("SQL EXCEPTION");
            } catch (IllegalStateException e) {
                System.out.println("ILLEGAL STATEMENT EXCEPTION");
            }
        } else {
            // Handle the case where the user did not enter anything
            System.out.println("No input was provided.");
        }
    }

    @FXML
    void saveStock(ActionEvent event) {
        //print stock details in PDF

        VBox memoBox = new VBox(1);

        memoBox.getChildren().add(
                new Label("- STOCK DETAILS -")
        );

        try {
            if (conn == null) {
                throw new IllegalStateException("Database connection is not initialized");
            }

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM stock_details");

            int i = 1;
            while (resultSet.next()) {
                String productName = resultSet.getString("description");
                String productCode = resultSet.getString("code");
                String productUnit = resultSet.getString("unit");
                String productPrice = String.valueOf(resultSet.getFloat("price"));
                String productSalePrice = String.valueOf(resultSet.getFloat("salePrice"));
                String productQuantity = String.valueOf(resultSet.getInt("quantity"));
                String productVat = String.valueOf(resultSet.getInt("vat"));

                Label label = new Label();
                label.setText("No " + (i++) + " - " + productCode + " - " +productName + " - " + productPrice + " - " + productSalePrice + " - " + productQuantity + productUnit);
                label.setWrapText(true);
                label.setMaxWidth(500);
                memoBox.getChildren().add(label);
            }


            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                boolean success = job.printPage(memoBox);
                if (success) {
                    job.endJob();
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL EXCEPTION");
        } catch (IllegalStateException e) {
            System.out.println("ILLEGAL STATEMENT EXCEPTION");
        }
    }


}
