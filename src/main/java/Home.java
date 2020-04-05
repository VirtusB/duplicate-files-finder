import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Home extends Application{

    public static void main(String[] args){
        Application.launch(Home.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setOnHidden(e -> {
            Platform.exit();
            System.exit(0);
        });

        Parent root = FXMLLoader.load(getClass().getResource("/Home.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("DupliFinder");
        stage.getIcons().add(new Image(Home.class.getResourceAsStream("/duplicate-icon.png")));
        stage.show();
    }

}