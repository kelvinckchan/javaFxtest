package app.view;

import app.Main;
import app.model.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ViewController {

	private ObservableList<String> comboBoxList = FXCollections.observableArrayList("DES", "3DES", "AES");

	@FXML
	private TableView<Model> ModelTable;
	@FXML
	private TableColumn<Model, String> firstNameColumn;
	@FXML
	private TableColumn<Model, String> lastNameColumn;
	@FXML
	private ComboBox<String> testComboBox;

	@FXML
	private Label firstNameLabel;
	@FXML
	private Label lastNameLabel;
	@FXML
	private Label streetLabel;
	@FXML
	private Label postalCodeLabel;
	@FXML
	private Label cityLabel;
	@FXML
	private Label birthdayLabel;

	// Reference to the main application.
	private Main mainApp;

	/**
	 * The constructor. The constructor is called before the initialize() method.
	 */
	public ViewController() {
	}

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		// Initialize the Model table with the two columns.
		firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
		lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());

		testComboBox.setItems(comboBoxList);

		// Clear Model details.
		showModelDetails(null);

		// Listen for selection changes and show the Model details when changed.
		ModelTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showModelDetails(newValue));
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;

		// Add observable list data to the table
		ModelTable.setItems(mainApp.getModelData());
	}

	/**
	 * Fills all text fields to show details about the Model. If the specified Model
	 * is null, all text fields are cleared.
	 * 
	 * @param Model
	 *            the Model or null
	 */
	private void showModelDetails(Model Model) {
		if (Model != null) {
			// Fill the labels with info from the Model object.
			firstNameLabel.setText(Model.getFirstName());
			lastNameLabel.setText(Model.getLastName());
			streetLabel.setText(Model.getStreet());
			postalCodeLabel.setText(Integer.toString(Model.getPostalCode()));
			cityLabel.setText(Model.getCity());

			// TODO: We need a way to convert the birthday into a String!
			// birthdayLabel.setText(...);
		} else {
			// Model is null, remove all the text.
			firstNameLabel.setText("");
			lastNameLabel.setText("");
			streetLabel.setText("");
			postalCodeLabel.setText("");
			cityLabel.setText("");
			birthdayLabel.setText("");
		}
	}

	/**
	 * Called when the user clicks on the delete button.
	 */
	@FXML
	private void handleDeleteModel() {
		int selectedIndex = ModelTable.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) {
			ModelTable.getItems().remove(selectedIndex);
			ModelTable.getSelectionModel().clearSelection();
		} else {
			// Nothing selected.
			showNothingSelectedAlertDialog();
		}
	}

	/**
	 * Called when the user clicks the new button. Opens a dialog to edit details
	 * for a new Model.
	 */
	@FXML
	private void handleNewModel() {
		Model tempModel = new Model();
		boolean okClicked = mainApp.showPersonEditDialog(tempModel);
		if (okClicked) {
			mainApp.getModelData().add(tempModel);
		}
	}

	/**
	 * Called when the user clicks the edit button. Opens a dialog to edit details
	 * for the selected Model.
	 */
	@FXML
	private void handleEditModel() {
		Model selectedModel = ModelTable.getSelectionModel().getSelectedItem();
		if (selectedModel != null) {
			boolean okClicked = mainApp.showPersonEditDialog(selectedModel);
			if (okClicked) {
				showModelDetails(selectedModel);
			}

		} else {
			// Nothing selected.
			showNothingSelectedAlertDialog();
		}
	}

	private void showNothingSelectedAlertDialog() {
		// Nothing selected.
		Alert alert = new Alert(AlertType.WARNING);
		alert.initOwner(mainApp.getPrimaryStage());
		alert.setTitle("No Selection");
		// alert.setHeaderText("No Model Selected");
		alert.setHeaderText(null);
		alert.setContentText("Please select a Model in the table.");
		alert.showAndWait();
	}
}