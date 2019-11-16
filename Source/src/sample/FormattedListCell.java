package sample;

import javafx.scene.control.ListCell;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;


public class FormattedListCell extends ListCell<String> {

    private Map<String, Background> _EventColour = new HashMap<>();

    public FormattedListCell(Map<String, Background> _input)
    {
        _EventColour = _input;
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(item);
        //System.out.println(item);
        setBackground(_EventColour.get(item));
        //setTextFill(isSevere(item)?Color.RED:Color.GREEN);
    }
}