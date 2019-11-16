package sample;

import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

public class EventsData
{
    public String EventType;
    public String EventName;
    public String EventComment;

    private List<EAttributes> _attributes;

    public EventsData(String name)
    {this.EventName = name; this.EventComment = null;}

    public EventsData(String name, String Comment)
    {this.EventName = name; this.EventComment = Comment;}

    public boolean HasTypeAssigned() { return EventName != null; }
    public boolean HasAttributes() {return this._attributes != null; }

    public void SetType(String _Type)
    {this.EventType = _Type;}

    public void AddAttribute(String Name, String Type, String Comment) throws Exception {
        if(this._attributes == null) //Create list if it hasn't been created
            this._attributes = new ArrayList<EAttributes>();

        if(Name.isEmpty()|| Type.isEmpty())
            throw new Exception("Name or Type has not been referenced (EventsData @ L:42)");

        this._attributes.add(new EAttributes(Name, Type, Comment));
    }

    public String GetEventName()
    {
        return this.EventName;
    }

    public List<EAttributes> GetAttributes()
    {
        return this._attributes;
    }


}
