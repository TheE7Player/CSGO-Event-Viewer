package sample;

public class EAttributes
{
    private String _n; //Name
    private String _t; //Type
    private String _c; //Comment

    public EAttributes(String Name, String Type, String Comment)
    {
        this._n = Name;
        this._t = Type;
        this._c = Comment; //<- Reminder: This was this._n, this killed the operation! YOU VOLATILE REBEL MISTAKE! (Reminder to scream at computer after fix)
    }

    //Getters
    public String GetName(){return this._n;}
    public String GetType(){return this._t;}
    public String GetComment(){return this._c;}

}
