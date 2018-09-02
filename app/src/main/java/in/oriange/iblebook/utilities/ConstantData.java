package in.oriange.iblebook.utilities;

import java.util.ArrayList;

import in.oriange.iblebook.models.GetAddressListPojo;
import in.oriange.iblebook.models.GetBankListPojo;
import in.oriange.iblebook.models.GetTaxListPojo;

public class ConstantData {

    public static ConstantData _instance;
    private String latitude;
    private String longitude;
    private ArrayList<GetAddressListPojo> addressList;
    private ArrayList<GetBankListPojo> bankList;
    private ArrayList<GetTaxListPojo> gstList;
    private ArrayList<GetTaxListPojo> panList;

    private ConstantData() {
    }

    public static ConstantData getInstance() {
        if (_instance == null) {
            _instance = new ConstantData();
        }
        return _instance;
    }

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

    public ArrayList<GetAddressListPojo> getAddressList() {
        return addressList;
    }

    public void setAddressList(ArrayList<GetAddressListPojo> addressList) {
        this.addressList = addressList;
    }

    public ArrayList<GetBankListPojo> getBankList() {
        return bankList;
    }

    public void setBankList(ArrayList<GetBankListPojo> bankList) {
        this.bankList = bankList;
    }

    public ArrayList<GetTaxListPojo> getGstList() {
        return gstList;
    }

    public void setGstList(ArrayList<GetTaxListPojo> gstList) {
        this.gstList = gstList;
    }

    public ArrayList<GetTaxListPojo> getPanList() {
        return panList;
    }

    public void setPanList(ArrayList<GetTaxListPojo> panList) {
        this.panList = panList;
    }
}