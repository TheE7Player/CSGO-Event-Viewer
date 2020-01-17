package scene.dev;

import TheE7Player.ProductComparer;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import sample.Main;
import scene.Controller;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;


public class splash_controller
{

    @FXML
    private StackPane stack_pane;

    @FXML
    private Label version_tag, statusText;

    private void SetStatusText(String text)
    {
        Platform.runLater(() -> statusText.setText(text));
    }

    public void Youtube() throws IOException, URISyntaxException
    {
        OpenUrl("https://www.youtube.com/TheE7Player");
    }
    public void Discord() throws IOException, URISyntaxException
    {
        OpenUrl("https://discord.gg/bfrGfJ8");
    }
    public void Twitter() throws IOException, URISyntaxException {
        OpenUrl("https://twitter.com/thee7player");
    }

    private void OpenUrl(String link) throws IOException, URISyntaxException
    {
        if (Desktop.isDesktopSupported()) {
            // Windows
            Desktop.getDesktop().browse(new URI(link));
        } else {
            // Ubuntu
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("/usr/bin/firefox -new-window " + link);
        }
    }

    private void OpenBrowserLatestEdition() throws IOException, URISyntaxException {
        //https://stackoverflow.com/a/17950164
        String url = "https://github.com/TheE7Player/CSGO-Event-Viewer/releases/latest";

        if (Desktop.isDesktopSupported()) {
            // Windows
            Desktop.getDesktop().browse(new URI(url));
        } else {
            // Ubuntu
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("/usr/bin/firefox -new-window " + url);
        }
    }

    private void UpdateDialog(String title, String Text, String UpdateTxt)
    {
        JFXDialogLayout DialogContainer = new JFXDialogLayout();

        //Assign it's header and description text (Head and Body)
        DialogContainer.setHeading(new Text(title));
        DialogContainer.setBody(new Text(Text + System.lineSeparator() + UpdateTxt));

        //Create a button with the text from param "ButtonText"
        JFXButton buttonYes = new JFXButton(Main._lang.GetValue("D_BTN_Y"));
        JFXButton buttonNo = new JFXButton(Main._lang.GetValue("D_BTN_N"));

        //Create a dialog called "msg" which holds all the information and displays back to user
        JFXDialog msg = new JFXDialog(stack_pane, DialogContainer, JFXDialog.DialogTransition.CENTER);

        //Assign the buttons click event to close down the dialog, and assign its action to DialogLayout too!
        buttonNo.setOnAction(event -> {msg.close();});
        buttonYes.setOnAction(event ->
        {
            try
            {
                OpenBrowserLatestEdition();
            }
            catch(Exception e)
            {
                System.out.println("ERROR LOADING LINK -> https://github.com/TheE7Player/CSGO-Event-Viewer/releases/latest");
                e.printStackTrace();

            }
            msg.close();
        });

        DialogContainer.setActions(buttonYes, buttonNo);

        //Now, we finally assign it back to the main entity and display to our user!
        msg.setContent(DialogContainer);
        msg.show();
    }

    @FXML
    public void initialize() throws InterruptedException {
        String version = Controller.BuildNumber;
        version_tag.setText("Version " + version);
        sample.Language l = Main._lang;
        SetStatusText(l.GetValue("SC_C"));
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1500);

                if(!Controller.UpdateToDate()) {
                    SetStatusText(l.GetValue("SC_U"));

                    Thread.sleep(1000);
                    UpdateDialog(Main._lang.GetValue("UP_T"), Main._lang.GetValue("UP_D"), String.format(Main._lang.GetValue("UP_L"), Controller.BuildNumber, ProductComparer.GetLatestVersion()));
                }
                else
                {
                    SetStatusText(l.GetValue("SC_I"));
                }
                Thread.sleep(2000);
                SetStatusText(l.GetValue("SC_R"));
                Thread.sleep(2000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }).whenComplete((i, e) -> {
            Platform.runLater(() ->{
            Parent root = null;
            try {
                //We cannot use .getClass() as we're not in sample.Main, so we access the class directly to get the resource location!
                //.getClass() would return "class scene.dev.splash_controller" and not "class sample.Main"
                root = FXMLLoader.load(sample.Main.class.getResource("/scene/sample.fxml"));
                Main.mainWindow.setScene(new Scene(root, 1280, 720));
            } catch (IOException ex) {
                ex.printStackTrace();
            }});
        });
    }
}


