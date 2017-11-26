package app;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import app.model.Model;
import app.model.ModelWrapper;
import app.view.EditDialogController;
import app.view.RootLayoutController;
import app.view.ViewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class Main extends Application {
	private Stage primaryStage;
	private BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("The App");
		initRootLayout();
		showView();
	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {

		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			scene.getStylesheets().add(getClass().getResource("./style/application.css").toExternalForm());
			primaryStage.setScene(scene);
			// Give the controller access to the main app.
			RootLayoutController controller = loader.getController();
			controller.setMainApp(this);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Try to load last opened person file.
		File file = getModelFilePath();
		if (file != null) {
			loadModelDataFromFile(file);
		}
	}

	/**
	 * Shows the model overview inside the root layout.
	 */
	public void showView() {
		try {
			// Load model overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/View.fxml"));
			AnchorPane personOverview = (AnchorPane) loader.load();

			// Set model overview into the center of root layout.
			rootLayout.setCenter(personOverview);

			// Give the controller access to the main app.
			ViewController controller = loader.getController();
			controller.setMainApp(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Opens a dialog to edit details for the specified model. If the user clicks
	 * OK, the changes are saved into the provided model object and true is
	 * returned.
	 * 
	 * @param model
	 *            the model object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 */
	public boolean showPersonEditDialog(Model model) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/EditDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit Model");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			scene.getStylesheets().add(getClass().getResource("./style/application.css").toExternalForm());
			dialogStage.setScene(scene);

			// Set the model into the controller.
			EditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setModel(model);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Returns the model file preference, i.e. the file that was last opened. The
	 * preference is read from the OS specific registry. If no such preference can
	 * be found, null is returned.
	 * 
	 * @return
	 */
	public File getModelFilePath() {
		Preferences prefs = Preferences.userNodeForPackage(Main.class);
		String filePath = prefs.get("filePath", null);
		if (filePath != null) {
			return new File(filePath);
		} else {
			return null;
		}
	}

	/**
	 * Sets the file path of the currently loaded file. The path is persisted in the
	 * OS specific registry.
	 * 
	 * @param file
	 *            the file or null to remove the path
	 */
	public void setModelFilePath(File file) {
		Preferences prefs = Preferences.userNodeForPackage(Main.class);
		if (file != null) {
			prefs.put("filePath", file.getPath());

			// Update the stage title.
			primaryStage.setTitle("AddressApp - " + file.getName());
		} else {
			prefs.remove("filePath");

			// Update the stage title.
			primaryStage.setTitle("AddressApp");
		}
	}

	/**
	 * Loads model data from the specified file. The current model data will be
	 * replaced.
	 * 
	 * @param file
	 */
	public void loadModelDataFromFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(ModelWrapper.class);
			Unmarshaller um = context.createUnmarshaller();

			// Reading XML from the file and unmarshalling.
			ModelWrapper wrapper = (ModelWrapper) um.unmarshal(file);

			modelData.clear();
			modelData.addAll(wrapper.getModels());

			// Save the file path to the registry.
			setModelFilePath(file);

		} catch (Exception e) { // catches ANY exception
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not load data");
			alert.setContentText("Could not load data from file:\n" + file.getPath());

			alert.showAndWait();
		}
	}

	/**
	 * Saves the current model data to the specified file.
	 * 
	 * @param file
	 */
	public void saveModelDataToFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(ModelWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Wrapping our model data.
			ModelWrapper wrapper = new ModelWrapper();
			wrapper.setModels(modelData);

			// Marshalling and saving XML to the file.
			m.marshal(wrapper, file);

			// Save the file path to the registry.
			setModelFilePath(file);
		} catch (Exception e) { // catches ANY exception
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not save data");
			alert.setContentText("Could not save data to file:\n" + file.getPath());

			alert.showAndWait();
		}
	}

	/**
	 * The data as an observable list of Persons.
	 */
	private ObservableList<Model> modelData = FXCollections.observableArrayList();

	/**
	 * Returns the data as an observable list of Persons.
	 * 
	 * @return
	 */
	public ObservableList<Model> getModelData() {
		return modelData;
	}

	/**
	 * Returns the main stage.
	 * 
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Constructor
	 */
	public Main() {
		// Add some sample data
		modelData.add(new Model("Hans", "Muster"));
		modelData.add(new Model("Ruth", "Mueller"));
		modelData.add(new Model("Heinz", "Kurz"));
		modelData.add(new Model("Cornelia", "Meier"));
	}

}
