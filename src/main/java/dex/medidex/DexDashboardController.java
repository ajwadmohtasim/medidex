package dex.medidex;

import Medicine.Medicine;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.MenuItem;
import javafx.fxml.Initializable;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.sql.Connection;

public class DexDashboardController implements Initializable{
    // TextFields
    @FXML
    private TextField AddGeneric;
    @FXML
    private TextField AddIndication;
    @FXML
    private TextField AddType;
    @FXML
    private TextField EditCompany;
    @FXML
    private TextField EditDose;
    @FXML
    private TextField EditMedicine;
    @FXML
    private TextField EditModeOfAction;
    @FXML
    private TextField EditPrecautions;
    @FXML
    private TextField EditSideEffects;
    @FXML
    private TextField EditPacksize;
    @FXML
    private TextField searchbar;

    // ChoiceBoxes
    @FXML
    private ChoiceBox<String> EditGeneric;
    @FXML
    private ChoiceBox<String> EditIndication;
    @FXML
    private ChoiceBox<String> EditType;

    // ComboBoxes
    @FXML
    private ComboBox<String> sortByGenericOption;
    @FXML
    private ComboBox<String> sortByIndicationOption;
    @FXML
    private ComboBox<String> sortByTypeOption;

    // TableView and TableColumns
    @FXML
    private TableView<Medicine> mediTable;
    @FXML
    private TableColumn<Medicine, String> mediTableName;
    @FXML
    private TableColumn<Medicine, String> mediTableType;

    // MenuItems
    @FXML
    private MenuItem menubarFileClose;
    @FXML
    private MenuItem menubarFileHome;


    private Connection connection;
    private final String dbURL = "jdbc:sqlite:src/main/resources/dex/medidex/medidex.db";
    private ObservableList<Medicine> medicineList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            connection = DriverManager.getConnection(dbURL);

            medicineList = DexTable.loadMedicineFromDatabase();
            DexTable.setupTableColumns(mediTable, mediTableName, mediTableType);
            mediTable.setItems(medicineList);
            DexTable.populateSort(connection, sortByGenericOption, sortByIndicationOption, sortByTypeOption);

            sortByGenericOption.setOnAction(event -> updateTableWithFilters());
            sortByIndicationOption.setOnAction(event -> updateTableWithFilters());
            sortByTypeOption.setOnAction(event -> updateTableWithFilters());
            searchbar.textProperty().addListener((observable, oldValue, newValue) -> updateTableWithFilters());
            menubarFileHome.setOnAction(actionEvent -> {
                SceneManager.switchScene("App.fxml", 1280, 720);
            });
            menubarFileClose.setOnAction(event -> Platform.exit());
            populateEditFields();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTableWithFilters() {
        String selectedGeneric = sortByGenericOption.getSelectionModel().getSelectedItem();
        String selectedIndication = sortByIndicationOption.getSelectionModel().getSelectedItem();
        String selectedType = sortByTypeOption.getSelectionModel().getSelectedItem();
        String keyword = searchbar.getText();
        ObservableList<Medicine> filteredList = DexTable.filterMedicines(medicineList, selectedGeneric, selectedIndication, selectedType, keyword);
        mediTable.setItems(filteredList);
    }

    private void populateChoiceBox(ChoiceBox<String> choiceBox, String query) {
        try (Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String item = resultSet.getString(1);
                choiceBox.getItems().add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void populateEditFields() {
        populateChoiceBox(EditType, "SELECT TypeName FROM Type");
        populateChoiceBox(EditGeneric, "SELECT GenericName FROM Generic");
        populateChoiceBox(EditIndication, "SELECT IndicationType FROM Indication");
    }
    @FXML
    private void addGeneric() {
        String selectedIndication = EditIndication.getValue();
        if (selectedIndication == null || selectedIndication.isEmpty()) {
            System.out.println("Please select an Indication.");
            return;
        }
        int indicationID = fetchIndicationID(selectedIndication);
        if (indicationID == -1) {
            System.out.println("Failed to fetch IndicationID");
            return;
        }

        String genericName = AddGeneric.getText().trim();
        String dose = EditDose.getText().trim();
        String sideEffect = EditSideEffects.getText().trim();
        String precautions = EditPrecautions.getText().trim();
        String modeOfAction = EditModeOfAction.getText().trim();

        if (genericName.isEmpty() || dose.isEmpty() || sideEffect.isEmpty() || precautions.isEmpty() || modeOfAction.isEmpty()) {
            System.out.println("Please fill all the details");
            return;
        }

        String insertQuery = "INSERT INTO Generic (GenericName, GenericDose, GenericSideEffects, GenericPrecautions, GenericModeOfAction, IndicationID) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, genericName);
            statement.setString(2, dose);
            statement.setString(3, sideEffect);
            statement.setString(4, precautions);
            statement.setString(5, modeOfAction);
            statement.setInt(6, indicationID);
            statement.executeUpdate();
            System.out.println("Generic added successfully.");
            populateChoiceBox(EditGeneric, "SELECT GenericName FROM Generic");
            DexTable.populateSort(connection, sortByGenericOption, sortByIndicationOption, sortByTypeOption);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void addIndication() {
        String newIndication = AddIndication.getText().trim();
        addItem(newIndication, "IndicationType", "IndicationID", EditIndication, "Indication");
    }
    @FXML
    private void addType() {
        String newType = AddType.getText().trim();
        addItem(newType, "TypeName", "TypeID" ,EditType, "Type");
    }

    private void addItem(String itemName, String columnName, String columnID,  ChoiceBox<String> choiceBox, String tableName) {
        itemName = itemName.trim();
        if (itemName.isEmpty()) {
            System.out.println("Please fill in the required fields.");
            return;
        }
        try {
            connection.setAutoCommit(false);
            String checkQuery = "SELECT " + columnID +  " FROM " + tableName + " WHERE LOWER(" + columnName + ") = LOWER(?)";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setString(1, itemName);
                ResultSet resultSet = checkStatement.executeQuery();
                if (resultSet.next()) {
                    System.out.println(itemName + " already exists in the database.");
                    return;
                }
            }
            String insertQuery = "INSERT INTO " + tableName + " (" + columnName + ") VALUES (?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, itemName);
                insertStatement.executeUpdate();
                System.out.println(itemName + " added successfully.");
            }
            connection.commit();
            choiceBox.getItems().clear();
            populateChoiceBox(choiceBox, "SELECT " + columnName + " FROM " + tableName);
            DexTable.populateSort(connection, sortByGenericOption, sortByIndicationOption, sortByTypeOption);

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void addMedicine() {
        String medicineName = EditMedicine.getText().trim();
        String genericName = EditGeneric.getValue();
        String typeName = EditType.getValue();
        String company = EditCompany.getText().trim();
        String packsize = EditPacksize.getText().trim();
        if (medicineName.isEmpty() || company.isEmpty() || packsize.isEmpty() || typeName == null || genericName == null) {
            System.out.println("Please fill in all the required fields.");
            return;
        }
        try {
            connection.setAutoCommit(false);
            int genericID = fetchGenericID(genericName);
            int typeID = fetchTypeID(typeName);
            if (typeID == -1) {
                System.out.println("Invalid type Selected");
                return;
            }
            String insertMedicineQuery = "INSERT INTO Medicine (MedicineName, MedicinePackSize, MedicineType, Company, GenericID) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertMedicineQuery)) {
                preparedStatement.setString(1, medicineName);
                preparedStatement.setString(2, packsize); // Pack size (optional)
                preparedStatement.setInt(3, typeID);
                preparedStatement.setString(4, company);
                preparedStatement.setInt(5, genericID);
                preparedStatement.executeUpdate();
            }
            connection.commit();
            System.out.println("Medicine added Successfully.");
            DexTable.reloadMedicineTable(medicineList, mediTable);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        }
    }

    private int fetchIndicationID(String indicationName) {
        String query = "SELECT IndicationID FROM Indication WHERE IndicationType = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, indicationName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("IndicationID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int fetchGenericID(String genericName) {
        String query = "SELECT GenericID FROM Generic WHERE GenericName = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, genericName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("GenericID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int fetchTypeID(String typeName) throws SQLException {
        String query = "SELECT TypeID FROM Type WHERE TypeName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, typeName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("TypeID");
            }
        }
        return -1;
    }
}
