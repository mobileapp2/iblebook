package in.oriange.iblebook.models;

import java.io.Serializable;

public class GetAddressListPojo implements Serializable {

    private String created_by;

    private String status;

    private String alias;

    private String website;

    private String address_line_one;

    private String state;

    private String type;

    private String address_line_two;

    private String photo;

    private String type_id;

    private String country;

    private String pincode;

    private String updated_at;

    private String visiting_card;

    private String mobile_number;

    private String map_location_logitude;

    private String address_id;

    private String map_location_lattitude;

    private String name;

    private String created_at;

    private String updated_by;

    private String district;

    private String email_id;

    private String landline_number;

    private String contact_person_name;

    private String contact_person_mobile;

    public boolean isChecked;

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress_line_one() {
        return address_line_one;
    }

    public void setAddress_line_one(String address_line_one) {
        this.address_line_one = address_line_one;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress_line_two() {
        return address_line_two;
    }

    public void setAddress_line_two(String address_line_two) {
        this.address_line_two = address_line_two;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getVisiting_card() {
        return visiting_card;
    }

    public void setVisiting_card(String visiting_card) {
        this.visiting_card = visiting_card;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getMap_location_logitude() {
        return map_location_logitude;
    }

    public void setMap_location_logitude(String map_location_logitude) {
        this.map_location_logitude = map_location_logitude;
    }

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public String getMap_location_lattitude() {
        return map_location_lattitude;
    }

    public void setMap_location_lattitude(String map_location_lattitude) {
        this.map_location_lattitude = map_location_lattitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getLandline_number() {
        if (landline_number.equals("null")) {
            return "";
        } else {
            return landline_number;
        }
    }

    public void setLandline_number(String landline_number) {
        this.landline_number = landline_number;
    }

    public String getContact_person_name() {
        if (contact_person_name.equals("null")) {
            return "";
        } else {
            return contact_person_name;
        }
    }

    public void setContact_person_name(String contact_person_name) {
        this.contact_person_name = contact_person_name;
    }

    public String getContact_person_mobile() {
        if (contact_person_mobile.equals("null")) {
            return "";
        } else {
            return contact_person_mobile;
        }
    }

    public void setContact_person_mobile(String contact_person_mobile) {
        this.contact_person_mobile = contact_person_mobile;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
