package dex.medidex;

import Medicine.Medicine;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;


public class AppController {
    // Labels for Medicine Details
    @FXML
    private Label companyNameDetails;
    @FXML
    private Label doseDetails;
    @FXML
    private Label genericNameDetails;
    @FXML
    private Label indicationNameDetails;
    @FXML
    private Label indicationsDetails;
    @FXML
    private Label mediNameDetails;
    @FXML
    private Label modeOfActionDetails;
    @FXML
    private Label sideEffectDetails;
    @FXML
    private Label typeNameDetails;
    @FXML
    private Label packsizeDetails;
    @FXML
    private Label warningDetails;

    // TableView and TableColumns
    @FXML
    private TableView<Medicine> mediTable;
    @FXML
    private TableColumn<Medicine, String> mediTableName;
    @FXML
    private TableColumn<Medicine, String> mediTableType;

    // ComboBoxes
    @FXML
    private ComboBox<String> sortByGenericOption;
    @FXML
    private ComboBox<String> sortByIndicationOption;
    @FXML
    private ComboBox<String> sortByTypeOption;

    // MenuItems
    @FXML
    private MenuItem menubarEditDex;
    @FXML
    private MenuItem menubarFileClose;

    // TextFields
    @FXML
    private TextField searchbar;


    private ObservableList<Medicine> medicineList;
    private Connection connection;
    private final String dbURL = "jdbc:sqlite:src/main/resources/dex/medidex/medidex.db";

    @FXML
    public void initialize() throws SQLException {
        connection = DriverManager.getConnection(dbURL);
        medicineList = DexTable.loadMedicineFromDatabase();
        DexTable.setupTableColumns(mediTable, mediTableName, mediTableType);
        mediTable.setItems(medicineList);
        DexTable.populateSort(connection, sortByGenericOption, sortByIndicationOption, sortByTypeOption);

        mediTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateMedicineDetails(newValue);
            }
        });
        sortByGenericOption.setOnAction(event -> updateTable());
        sortByIndicationOption.setOnAction(event -> updateTable());
        sortByTypeOption.setOnAction(event -> updateTable());
        searchbar.textProperty().addListener((observable, oldValue, newValue) -> updateTable());
        menubarFileClose.setOnAction(event -> Platform.exit());
        menubarEditDex.setOnAction(actionEvent -> {
            SceneManager.switchScene("DexDashboard.fxml", 1280, 720);
        });
    }
    private void updateTable() {
        String selectedGeneric = sortByGenericOption.getSelectionModel().getSelectedItem();
        String selectedIndication = sortByIndicationOption.getSelectionModel().getSelectedItem();
        String selectedType = sortByTypeOption.getSelectionModel().getSelectedItem();
        String keyword = searchbar.getText();
        ObservableList<Medicine> filteredList = DexTable.filterMedicines(medicineList, selectedGeneric, selectedIndication, selectedType, keyword);
        mediTable.setItems(filteredList);
    }


    private void updateMedicineDetails(Medicine selectedMedicine) {
        mediNameDetails.setText(selectedMedicine.getName());
        genericNameDetails.setText(selectedMedicine.getGenericName());
        indicationNameDetails.setText(selectedMedicine.getIndicationType());
        typeNameDetails.setText(selectedMedicine.getType());
        indicationsDetails.setText(selectedMedicine.getIndicationType());
        doseDetails.setText(selectedMedicine.getDose());
        sideEffectDetails.setText(selectedMedicine.getSideEffects());
        warningDetails.setText(selectedMedicine.getPrecautions());
        modeOfActionDetails.setText(selectedMedicine.getModeOfAction());
        companyNameDetails.setText(selectedMedicine.getCompanyName());
        packsizeDetails.setText(selectedMedicine.getPacksize());
    }
}
