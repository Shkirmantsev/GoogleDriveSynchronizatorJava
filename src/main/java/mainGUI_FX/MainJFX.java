
package mainGUI_FX;

import java.io.IOException;
import java.nio.file.Paths;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author Shkirmantsev
 */
public class MainJFX extends Application
{
    private static Stage stage;
    @Override
    public void start(Stage stage) throws IOException
    {
        MainJFX.stage=stage;
        
        Parent root = FXMLLoader.load(getClass().getResource( "/StartGUIWindow.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        setStageTitle("google Synchronizator");
        stage.show();
        

    }
     public static void setStageTitle(String newTitle) {
        MainJFX.stage.setTitle(newTitle);
        
       
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }
    
}
