package in.oriange.iblebook.models;

public class GetRequestsListPojo {

    private String message;

    private String request_id;

    private String status;

    private String sender_id;

    private String sender_name;

    private String type;

    private String mobile;

    private String sender_mobile;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getRequest_id ()
    {
        return request_id;
    }

    public void setRequest_id (String request_id)
    {
        this.request_id = request_id;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getSender_id ()
    {
        return sender_id;
    }

    public void setSender_id (String sender_id)
    {
        this.sender_id = sender_id;
    }

    public String getSender_name ()
    {
        return sender_name;
    }

    public void setSender_name (String sender_name)
    {
        this.sender_name = sender_name;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getMobile ()
    {
        return mobile;
    }

    public void setMobile (String mobile)
    {
        this.mobile = mobile;
    }

    public String getSender_mobile() {
        return sender_mobile;
    }

    public void setSender_mobile(String sender_mobile) {
        this.sender_mobile = sender_mobile;
    }
}
