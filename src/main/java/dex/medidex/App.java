package dex.medidex;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.net.URL;

public class App extends Application {
    @Override
    public void start(Stage stage)  {
        String iconPath = "/dex/medidex/icon.png";
        URL iconURL = getClass().getResource(iconPath);
        if (iconURL != null) {
            Image icon = new Image(iconURL.toString());
            stage.getIcons().add(icon);
        } else {
            System.out.println("Icon file not found!");
        }

        stage.setTitle("Medidex - A Medicine Database");
        stage.setResizable(false);
        SceneManager.setPrimaryStage(stage);
        SceneManager.switchScene("App.fxml", 1280, 720);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}