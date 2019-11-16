package sample;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Language
{
    private Map<String, String> _Language;

    public enum Languages
    {
        ENGLISH,
        RUSSIAN
    }

    public Language(Languages LanguageType)
    {
        switch (LanguageType)
        {
            case ENGLISH:
                AppendLanguage("resources/Language/la_EN.lang");
                break;

            case RUSSIAN:
                AppendLanguage("resources/Language/la_RU.lang");
                break;
        }
    }

    private void AppendLanguage(String fileRef)
    {
        //Now, lets read in our events.json into JsonFile
        List<String> _File = new ArrayList<>();

        try
        {
            //fileRef
            _File = Files.readAllLines(Paths.get(fileRef));
        }
        catch(IOException e)
        {
            System.out.println("Error " + e.getMessage());
        }
        _Language = new HashMap<>();

        //Now map it into the map
        for (String line : _File)
        {
            //Skip comments
            if(line.startsWith("//") || line.isEmpty()) continue;

            //This means "line" isn't a comment
            String[] _l = line.split(" = ");
            _Language.put(_l[0], _l[1]);
        }
    }

    public String GetValue(String key)
    {
        String result = _Language.get(key);

        if(result.contains("\\r\\n"))
            result = result.replace("\\r\\n", System.lineSeparator());

        return result;
    }


}
