package in.oriange.iblebook.models;

public class AddressTypePojo {
    private String type;

    private String type_id;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    @Override
    public String toString() {
        return "ClassPojo [type = " + type + ", type_id = " + type_id + "]";
    }
}
