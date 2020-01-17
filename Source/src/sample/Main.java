package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {
    public static Stage mainWindow;
    /*
        short, byte, long = integer
        string, wstring = string
        bool = boolean
        float = float

        icon links:
        integer - https://icons8.com/icon/51769/data
        float - https://icons8.com/icon/53095/hot-air-balloon
        string - https://icons8.com/icon/43033/typography
        boolean - https://icons8.com/icon/49747/conversion

        Flag Size: 200px * 200px * 72dpi (W x H x Dots per Inch)
    */

    //Set default language to English
    public static Language.Languages DefaultLanguage = Language.Languages.ENGLISH;
    public static Language _lang;

    //Data type Icons
    public static Image StringIcon, FloatIcon, BoolIcon, IntIcon;

    //Flag Icons
    public static Image English, Russian;

    public static List<EventsData> Events = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception{
        mainWindow = primaryStage;
        _lang = new Language(DefaultLanguage);
        SetupVariables();
        //"../scene/sample.fxml"

        //This means now Events is in ASC order based on event names, let's pass this through to the Controller (UI)
        //"../scene/dev/splash_screen.fxml"
        Parent root = FXMLLoader.load(getClass().getResource("/scene/dev/splash_screen.fxml"));

        mainWindow.setTitle(_lang.GetValue("Title"));
        mainWindow.setScene(new Scene(root, 1280, 720));
        mainWindow.initStyle(StageStyle.UTILITY); //Allows only X in window
        //Now setup language
        mainWindow.show();
    }


    private void SetupVariables()
    {
        //First, Let's assign our icons (StringIcon, FloatIcon etc)
        try {
            //Data type icons
            StringIcon = new Image(new FileInputStream("resources/str.png"));
            FloatIcon = new Image(new FileInputStream("resources/float.png"));
            BoolIcon = new Image(new FileInputStream("resources/bool.png"));
            IntIcon = new Image(new FileInputStream("resources/int.png"));

            //Language flags
            English = new Image(new FileInputStream("resources/flag/english.png"));
            Russian = new Image(new FileInputStream("resources/flag/russian.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Now, lets read in our events.json into JsonFile
        List<String> JsonFile = new ArrayList<>();

        try
        {
            JsonFile = Files.readAllLines(Paths.get("resources/events.json"));
        }
        catch(IOException e)
        {
            System.out.println("Error " + e.getMessage());
        }

        //Create our regex patterns
        Pattern EventHead = Pattern.compile("^\"(.+)\"(?:\\s+:\\s(.+))?");
        Pattern EventBody = Pattern.compile("^\\t\"type\"\\s:\\s\"(modevents|serverevents|gameevents)\"|^\\t\"(.+)\"\\s{4}\"(.+)\"\\s{4}(?:\"(.+)\")?", Pattern.MULTILINE);

        //Then we put it into the List 'Events'
        for (int i = 0; i < JsonFile.size(); i++)
        {
            DoOperation(JsonFile.get(i), EventHead, EventBody);
        }
        JsonFile = null; //de-reference the list as we don't require it any more
    }

    private void DoOperation(String line, Pattern p1, Pattern p2)
    {
        if(line.equals("{") || line.equals("}"))
            return;

        //Evaluate patterns
        boolean isEventName = Pattern.matches(p1.pattern(), line) && !Pattern.matches(p2.pattern(), line);

        if(!InEvents(line) && isEventName)
        {
            Matcher m = p1.matcher(line);
            if (m.find())
            {
                //[NOTE] : Group 2 will be "null" if this event doesn't have a comment along side it!
                boolean hasComment;

                try {hasComment = !m.group(2).equals("null");} catch (NullPointerException e) {hasComment = false;};

                if(hasComment){Events.add(new EventsData(m.group(1), m.group(2)));}else{Events.add(new EventsData(m.group(1)));}
            }
        }
        else
        {
            //TODO: Create a local variable for this procedure for efficient coding?
            //Does contain it... First lets find the entity

            Matcher m2 = p2.matcher(line);
            if (m2.find())
            {
                if(line.startsWith("\t\"type\""))
                {
                    //Set the current item in 'Events' list of attribute 'EventType' to 1st Match Group (Attributes event extraction file)
                    Events.get(Events.size() - 1).EventType = m2.group(1);
                }
                else
                {
                    String att_name = m2.group(2); //2nd Match Group (Attribute name)
                    String att_type = m2.group(3); //3rd Match Group (Attribute(s) Type)
                    String att_comment = (m2.group(4) != null || m2.group(4) != "") ? m2.group(4) : null; //4th Match (Comment, if any!)

                    //System.out.println(String.format("EVENT: %s (1:%s 2:%s 3:%s 4:%s)", Events.get(Events.size() - 1).EventName, m2.group(1), m2.group(2), m2.group(3), m2.group(4)));
                    try
                    {
                        Events.get(Events.size() - 1).AddAttribute(att_name, att_type, att_comment);
                    }
                    catch (Exception e)
                    {
                       System.out.print(true);
                    }
                }
            }


        }
    }

    private boolean InEvents(String name)
    {
        return Events.stream().anyMatch(e -> e.GetEventName().startsWith(name));
    }

    public static void ChangeConfigLanguage(Language.Languages newLang)
    {
        try
        {
            Path config = Paths.get("./CONFIG.cfg"); //Store it into where the .jar is launched from
            List<String> file = Files.readAllLines(config);
            file.set(0, "LANG = " + newLang.toString());
            Files.write(config, file, StandardCharsets.UTF_8, StandardOpenOption.CREATE);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("ERROR SETTING NEW LANGUAGE!");
        }
    }

    private static void InitialiseConfigFile(String[] args) throws IOException {
        //Get the path where we store/use the configuration file
        Path config = Paths.get("./CONFIG.cfg"); //Store it into where the .jar is launched from

        /*
            CONFIG STRUCTURE:
            Line[0] 1 - Program language

        */

        //Now, check if we already have it
        if(Files.exists(config))
        {
            //Config file exists!
            List<String> file = Files.readAllLines(config);

            //Step 1: Validate language
            String lg = file.get(0).replace("LANG = ", ""); //Get rid of "LANG = " in string
            Language.Languages lang;
            try
            {
                lang = Enum.valueOf(Language.Languages.class, lg);
                if(!lang.equals(null) && !DefaultLanguage.equals(lang))
                {
                    //This means that the config has a language that isn't assigned as ENGLISH
                    DefaultLanguage = lang;
                }
            }
            catch (IllegalArgumentException IAE)
            {
                //Leave alone, default will be english
                lang = DefaultLanguage;
                System.out.println(String.format("ERROR: Unknown Language \"%s\" - SET TO DEFAULT (ENGLISH)", lg));
            }
        }
        else
        {
            //Config file doesn't exist!
            List<String> newConfig = new ArrayList<>();

            Language.Languages lang = (GetArrayLength(args) > -1) ? GetLanguage(args[0]) : GetLanguage("");
            newConfig.add(String.format("LANG = %s", lang.toString()));
            DefaultLanguage = lang;
            Files.write(config, newConfig, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        }

        launch(args);
    }

    private static int GetArrayLength(Object[] array)
    {
        try
        {
            return (array.length > 0) ? array.length : -1;
        }
        catch(ArrayIndexOutOfBoundsException ae)
        {
            return -1;
        }
    }

    private static Language.Languages GetLanguage(String argument)
    {
        //If has -ru argument
        if(argument.equals("-ru")){return Language.Languages.RUSSIAN;}

        //If nothing has matched, use this default language
        return Language.Languages.ENGLISH;
    }

    public static void main(String[] args) throws IOException {
        //ENGLISH BY DEFAULT
        InitialiseConfigFile(args);
    }
}
