import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextArea;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.function.Consumer;

public class MyController {
    @FXML
    private AnchorPane root;
    @FXML
    private TextArea serverLog;
    @FXML
    private TextField PortNumber;
    @FXML
    private TextField ClientNumber;

    Server server;
    Consumer<Serializable> call = e -> {
        Platform.runLater(()->{
            serverLog.appendText(e.toString()+"\n");
        });
    };

    Consumer<Serializable> ClientsPlusPlus = e-> {
        // Get the current string and add 1
        Platform.runLater(() -> {
            int currentNum;
            try {
                currentNum = Integer.parseInt(ClientNumber.getText());
                currentNum++;
            } catch (Exception f) {
                // This catch block deals with when there are zero clients
                currentNum = 1;
            }
            ClientNumber.setText(Integer.toString(currentNum));
        });
    };

    // Make sure to catch that IO somewhere!
    public void PortNumberMethod(){
        // How to do this portion?
            try{
                int portNumber = Integer.parseInt(PortNumber.getText());
                // When you reset the server then you can change the port number!
                PortNumber.setEditable(false);
                server = new Server(call,portNumber);
            }catch (Exception e){
                // Not a valid integer
                serverLog.appendText("Please enter a valid port number\n");
                PortNumber.clear();
            }

    }

}
