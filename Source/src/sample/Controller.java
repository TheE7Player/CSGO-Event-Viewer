package sample;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.sun.xml.internal.ws.util.StringUtils;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class Controller {
    @FXML
    private TextArea codeDisplay;

    @FXML
    private ListView<String> eventList;

    @FXML
    private TreeView<String> EventTree;

    @FXML
    private Button CopyToClipBoard;

    @FXML
    private Label dt_icons, StrStr, FloStr, BoolStr, IntStr, in_squ;

    @FXML
    private CheckBox isGlobalFunction;

    @FXML
    private TextField searchBox;

    @FXML
    private Label BuildTag; // <- Holds the build number and date

    @FXML
    private String BuildNumber = "0.3";

    @FXML
    private ImageView icon_Str, icon_Float, icon_Bool, icon_Int;

    @FXML
    private Map<String, Background> EventColour = new HashMap<>();

    //Holds event names, no attributes!
    @FXML
    private List<String> EventsNames = new ArrayList<>();
    @FXML
    private StackPane Stack_pane;

    private void DoSearch(String toSearch)
    {
        //List<Person> beerDrinkers = persons.stream().filter(p -> p.getAge() > 16).collect(Collectors.toList());

        if(toSearch.length() < 1)
        {
            eventList.getItems().clear();
            for(String i : EventsNames)
                eventList.getItems().add(i);
        }
        else
        {
            List<String> result = EventsNames.stream().filter(p -> p.startsWith(searchBox.getText())).collect(Collectors.toList());

            if(result.size() > 0) {
                eventList.getItems().clear();
                for (String i : result)
                    eventList.getItems().add(i);
            }
            else
            {
                eventList.getItems().clear();
                eventList.getItems().add("No Results Found");
            }
        }

    }

    private void SetLanguage()
    {
        sample.Language l = Main._lang;

        searchBox.setPromptText(l.GetValue("L_ES"));
        dt_icons.setText(l.GetValue("L_DTI"));
        StrStr.setText(l.GetValue("L_DS"));
        FloStr.setText(l.GetValue("L_DF"));
        BoolStr.setText(l.GetValue("L_DB"));
        IntStr.setText(l.GetValue("L_DI"));
        in_squ.setText(l.GetValue("L_IS"));
        isGlobalFunction.setText(l.GetValue("L_GE"));
        CopyToClipBoard.setText(l.GetValue("B_CP"));
    }

    private void init()
    {
        //First, setup attributes data type icons
        icon_Str.setImage(Main.StringIcon); icon_Float.setImage(Main.FloatIcon); icon_Bool.setImage(Main.BoolIcon); icon_Int.setImage(Main.IntIcon);
        icon_Str.setCache(true); icon_Float.setCache(true); icon_Bool.setCache(true); icon_Int.setCache(true);

        //Grab only the names of the events
        EventsNames = Main.Events.stream().map(EventsData::GetEventName).collect(Collectors.toList());

        //Format ListView cells to reflect colour (Pink and Blue)
        eventList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> list) {
                return new FormattedListCell(EventColour);
            }
        });

        //Our TextBox search event (Key release event) to filter the results based on what text has been entered
        searchBox.setOnKeyReleased(event -> {
            //System.out.println(searchBox.getText());
            DoSearch(searchBox.getText());
        });
        SetLanguage();
    }

    //Code to execute when first launched
    @FXML
    public void initialize() {

        //Update build tag
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        Date date = new Date();
        BuildTag.setText(String.format("Build v%s (%s)", BuildNumber, formatter.format(date)));

        //Setup icons and Event names
        init();

        //Iterate through each event and display in listbox
        Background HasAttribute = new Background(new BackgroundFill(Color.LIGHTPINK, CornerRadii.EMPTY, Insets.EMPTY));
        Background NoAttribute = new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY));

        for(EventsData item : Main.Events)
        {
            eventList.getItems().add(item.EventName);
            if(item.HasAttributes())
            {
                EventColour.put(item.EventName, HasAttribute);
            }
            else
            {
                EventColour.put(item.EventName, NoAttribute);
            }
        }

        //Clear away the colour objects as we don't need them no longer
        HasAttribute = null; NoAttribute = null;
    }

    public void DoWork()
    {
        //Check if clicked item equals "No Results Found"
        if(eventList.getSelectionModel().getSelectedItem() == "No Results Found")
            return;

        int workingIndex = eventList.getSelectionModel().getSelectedIndex();

        if(workingIndex < EventsNames.size() && workingIndex >= 0) //Check if index is suitable
        {
            EventsData filteredItem = Main.Events.get(workingIndex);

            //Reset treeview for next item!
            EventTree.setRoot(null);

            //Setup treeview for new items
            TreeItem<String> root = new TreeItem<>(filteredItem.EventName);
            TreeItem<String> child;
            root.setExpanded(true);

            if(filteredItem.EventComment != null)
            {
                TreeItem<String> CommentHead = new TreeItem<>("Comment");
                TreeItem<String> CommentBody = new TreeItem<>(filteredItem.EventComment);
                CommentHead.getChildren().add(CommentBody);
                root.getChildren().add(CommentHead);
            }

            List<EAttributes> data_types = null;
            data_types = filteredItem.GetAttributes();
            if(data_types != null)
            if(filteredItem.HasAttributes()) {

                for (EAttributes item : data_types) {
                    child = new TreeItem<>(item.GetName(), GetDataImage(item.GetType()));

                    if (item.GetComment() != null) {
                        //Attribute has a comment!
                        String comment = StringUtils.capitalize(item.GetComment());
                        TreeItem<String> cm = new TreeItem<>(comment);
                        child.getChildren().add(cm);
                    }

                    root.getChildren().add(child);
                }
            }
            EventTree.setRoot(root);

            GenerateCode(filteredItem.EventName, data_types);
        }
    }

    private ImageView GetDataImage(String type)
    {
        //short, byte, long = integer
        //string, wstring = string
        //bool = boolean
        //float = float
        switch (type)
        {
            case "string": return new ImageView(Main.StringIcon);
            case "bool": return new ImageView(Main.BoolIcon);
            case "float": return new ImageView(Main.FloatIcon);
            case "integer": return new ImageView(Main.IntIcon);
            default: return null;
        }
    }

    public void CopyToClip() throws IOException {
        //System.out.println(codeDisplay.getText().length());
        if(codeDisplay.getText().length() == 0)
            ShowDialog(Main._lang.GetValue("D_NCS_T"), Main._lang.GetValue("D_NCS_D"), Main._lang.GetValue("D_BTN_T"));
        else
        {
            if(CopyBufferText == null)
                SetBufferText(codeDisplay.getText());
            else
            {
                if(codeDisplay.getText().equals(CopyBufferText))
                {
                    ShowDialog(Main._lang.GetValue("D_CSC_T"), Main._lang.GetValue("D_CSC_D"), Main._lang.GetValue("D_BTN_T"));
                }
                else
                {
                    SetBufferText(codeDisplay.getText());
                }
            }
        }
    }

    String CopyBufferText = null;

    private void SetBufferText(String Text) throws IOException {
        TryAssignToClipBoard(Text);
        CopyBufferText = Text;
        ShowDialog(Main._lang.GetValue("D_CSC_T"), Main._lang.GetValue("D_CSC_D"), Main._lang.GetValue("D_BTN_T"));
    }

    private void ShowDialog(String title, String Text, String ButtonText)
    {
        //Create a DialogLayout
        JFXDialogLayout DialogContainer = new JFXDialogLayout();

        //Assign it's header and description text (Head and Body)
        DialogContainer.setHeading(new Text(title));
        DialogContainer.setBody(new Text(Text));

        //Create a button with the text from param "ButtonText"
        JFXButton button = new JFXButton(ButtonText);

        //Create a dialog called "msg" which holds all the information and displays back to user
        JFXDialog msg = new JFXDialog(Stack_pane, DialogContainer, JFXDialog.DialogTransition.CENTER);

        //Assign the buttons click event to close down the dialog, and assign its action to DialogLayout too!
        button.setOnAction(event -> msg.close());
        DialogContainer.setActions(button);

        //Now, we finally assign it back to the main entity and display to our user!
        msg.setContent(DialogContainer);
        msg.show();
    }

    private void TryAssignToClipBoard(String Text) throws IOException {
        ClipboardContent content = new ClipboardContent();
        content.putString(Text);
        Clipboard.getSystemClipboard().setContent(content);
    }

    @FXML public void listClicked(MouseEvent arg0) { DoWork(); }

    public void GenerateCode(String EventCallName, List<EAttributes> parameters)
    {
        codeDisplay.clear(); //Clear textbox just incase text is here already!

        StringBuilder sb = new StringBuilder();

        if(!isGlobalFunction.isSelected())
            sb.append("function " + EventCallName + "(event_data)\n");
        else
            sb.append("::EVENT_"+EventCallName.toUpperCase()+" <- function(event_data)\n");

        sb.append("{\n");

        if(parameters == null)
        {
            sb.append("\t//Event call for event: " + EventCallName + "\n\t//This event doesn't carry any attributes! (Parameterless!)\n");
        }
        else {
            if (parameters.size() > 0) {

                sb.append("\t//Event call for event: " + EventCallName + "\n\t/* You can use the following attributes:\n");

                for (EAttributes item : parameters) {
                    sb.append("\t\tevent_data." + item.GetName() + "\n");
                }

                sb.append("\t*/\n");
            } else {
                sb.append("\t//Event call for event: " + EventCallName + "\n\t//This event doesn't carry any attributes! (Parameterless!)\n");
            }
        }

        sb.append("}\n");

        codeDisplay.setText(sb.toString());
    }
}
