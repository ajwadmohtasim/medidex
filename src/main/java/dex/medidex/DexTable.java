package dex.medidex;

import Medicine.Medicine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class DexTable {

    public static void setupTableColumns(TableView<Medicine> mediTable,
                                         TableColumn<Medicine, String> mediTableName,
                                         TableColumn<Medicine, String> mediTableType) {
        mediTableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        mediTableType.setCellValueFactory(new PropertyValueFactory<>("type"));
    }

    private static final String DB_URL = "jdbc:sqlite:src/main/resources/dex/medidex/medidex.db";

    public static ObservableList<Medicine> loadMedicineFromDatabase() {
        ObservableList<Medicine> medicineList = FXCollections.observableArrayList();

        String query = """
                SELECT m.MedicineName, m.MedicinePackSize, m.Company, t.TypeName, g.GenericName, g.GenericDose,
                       g.GenericSideEffects, g.GenericPrecautions, g.GenericModeOfAction, i.IndicationType
                FROM Medicine m
                JOIN Type t ON m.MedicineType = t.TypeID
                JOIN Generic g ON m.GenericID = g.GenericID
                JOIN Indication i ON g.IndicationID = i.IndicationID;
                """;
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                medicineList.add(new Medicine(
                        resultSet.getString("MedicineName"),
                        resultSet.getString("IndicationType"),
                        resultSet.getString("GenericName"),
                        resultSet.getString("GenericDose"),
                        resultSet.getString("GenericSideEffects"),
                        resultSet.getString("GenericPrecautions"),
                        resultSet.getString("GenericModeOfAction"),
                        resultSet.getString("Company"),
                        resultSet.getString("TypeName"),
                        resultSet.getString("MedicinePackSize")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error Loading Medicines from database: " + e.getMessage());
        }
        return medicineList;
    }
    public static void reloadMedicineTable(ObservableList<Medicine> medicineList,
                                     TableView<Medicine> mediTable) {
        medicineList = DexTable.loadMedicineFromDatabase();
        mediTable.setItems(medicineList);
    }
    public static void populateSort(Connection connection,
                                    ComboBox<String> genericBox,
                                    ComboBox<String> indicationBox,
                                    ComboBox<String> typeBox) {
        populateComboBox(connection, genericBox, "SELECT GenericName FROM Generic");
        populateComboBox(connection, indicationBox, "SELECT IndicationType FROM Indication");
        populateComboBox(connection, typeBox, "SELECT TypeName FROM Type");

        genericBox.getItems().addFirst("None");
        indicationBox.getItems().addFirst("None");
        typeBox.getItems().addFirst("None");

//        genericBox.getSelectionModel().selectFirst();
//        indicationBox.getSelectionModel().selectFirst();
//        typeBox.getSelectionModel().selectFirst();
    }

    private static void populateComboBox(Connection connection, ComboBox<String> comboBox, String query) {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            comboBox.getItems().clear(); // Clear existing items
            while (resultSet.next()) {
                comboBox.getItems().add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<Medicine> filterMedicines(ObservableList<Medicine> medicineList,
                                                           String selectedGeneric,
                                                           String selectedIndication,
                                                           String selectedType,
                                                           String keyword) {
        return medicineList.filtered(medicine ->
                (selectedGeneric == null || selectedGeneric.isEmpty() || "None".equals(selectedGeneric) || medicine.getGenericName().equals(selectedGeneric)) &&
                        (selectedIndication == null || selectedIndication.isEmpty() || "None".equals(selectedIndication) || medicine.getIndicationType().equals(selectedIndication)) &&
                        (selectedType == null || selectedType.isEmpty() || "None".equals(selectedType) || medicine.getType().equals(selectedType)) &&
                        (keyword.isEmpty() || medicine.getName().toLowerCase().contains(keyword.toLowerCase()))
        );
    }
}
