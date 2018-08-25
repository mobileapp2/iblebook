package in.oriange.iblebook.models;

public class GetReceivedDetailsListPojo {

    private String status;

    private String sender_id;

    private String shared_details_id;

    private String sender_name;

    private String type;

    private String mobile;

    private String record_id;

    private String message;

    private String sender_mobile;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getShared_details_id() {
        return shared_details_id;
    }

    public void setShared_details_id(String shared_details_id) {
        this.shared_details_id = shared_details_id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender_mobile() {
        return sender_mobile;
    }

    public void setSender_mobile(String sender_mobile) {
        this.sender_mobile = sender_mobile;
    }

    @Override
    public String toString() {
        return "ClassPojo [status = " + status + ", sender_id = " + sender_id + ", shared_details_id = " + shared_details_id + ", sender_name = " + sender_name + ", type = " + type + ", mobile = " + mobile + ", record_id = " + record_id + "]";
    }
}
