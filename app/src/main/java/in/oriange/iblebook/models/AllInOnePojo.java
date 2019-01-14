package in.oriange.iblebook.models;

import java.util.ArrayList;

public class AllInOnePojo {

    private String message;

    private ArrayList<AllInOneModel> result;

    private String type;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public ArrayList<AllInOneModel> getResult ()
    {
        return result;
    }

    public void setResult (ArrayList<AllInOneModel> result)
    {
        this.result = result;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }


}
