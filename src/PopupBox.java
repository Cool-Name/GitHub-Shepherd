import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class PopupBox extends Application {

	private static String title = "";
	private static String message = "";
	
	public static void setTitle(String _title)
	{
		title = _title;
	}
	
	public static void setMessage(String _message)
	{
		message = _message;
	}
	
    public static void main(String[] args) {
    	title = "test";
    	message = "cuck cuck cuck cuck";
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle(title);
        primaryStage.setWidth(300);
        primaryStage.setHeight(200);
        final Popup popup = new Popup();
        

        Button ok = new Button("Ok");
        
        ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
            }
        });
        
        TextArea area = new TextArea();
        
        /*
        Button hide = new Button("Hide");
        hide.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                popup.hide();
            }
        });*/

        Pane layout = new Pane();
        layout.setStyle("-fx-background-color: lightgrey; -fx-padding: 10;");
        layout.getChildren().addAll(ok);
        primaryStage.setScene(new Scene(layout));
        
        ok.setMinHeight(33);
        ok.setMinWidth(100);
        ok.setPrefHeight(33);
        ok.setPrefWidth(100);
        ok.setMaxHeight(33);
        ok.setMaxWidth(100);
        
        //mess.setLayoutX(primaryStage.get);
        ok.setLayoutX(primaryStage.getWidth()/2 - 100/2);
        ok.setLayoutY((int)(primaryStage.getHeight() * 0.75 - 50));
        
        primaryStage.show();
    }
}