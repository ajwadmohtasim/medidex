package dex.medidex;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private static Stage primaryStage;
    private static final Map<String, Scene> sceneCache = new HashMap<>();

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxmlFile, int width, int height) {
        try {
            if (!sceneCache.containsKey(fxmlFile)) {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlFile));
                Scene scene = new Scene(loader.load(), width, height);
                sceneCache.put(fxmlFile, scene);
            }
            primaryStage.setScene(sceneCache.get(fxmlFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
