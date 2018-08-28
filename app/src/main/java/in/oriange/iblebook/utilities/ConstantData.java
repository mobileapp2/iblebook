package in.oriange.iblebook.utilities;

import java.util.ArrayList;

import in.oriange.iblebook.models.GetAddressListPojo;

public class ConstantData {

    public static ConstantData _instance;

    private ConstantData() {
    }

    public static ConstantData getInstance() {
        if (_instance == null) {
            _instance = new ConstantData();
        }
        return _instance;
    }

    private String latitude;
    private String longitude;

    public static ConstantData get_instance() {
        return _instance;
    }

    public static void set_instance(ConstantData _instance) {
        ConstantData._instance = _instance;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    private ArrayList<GetAddressListPojo> addressList;

    public ArrayList<GetAddressListPojo> getAddressList() {
        return addressList;
    }

    public void setAddressList(ArrayList<GetAddressListPojo> addressList) {
        this.addressList = addressList;
    }
}