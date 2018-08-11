package in.oriange.iblebook.models;

public class ContactListPojo {

    private String initLetter;
    private String name;
    private String phoneNo;

    public ContactListPojo(String initLetter, String name, String phoneNo) {
        this.initLetter = initLetter;
        this.name = name;
        this.phoneNo = phoneNo;
    }

    public String getInitLetter() {
        return initLetter;
    }

    public void setInitLetter(String initLetter) {
        this.initLetter = initLetter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ContactListPojo) {
            ContactListPojo temp = (ContactListPojo) obj;
            if (/*this.name == temp.name && */this.phoneNo.equals(temp.phoneNo))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (/*this.name.hashCode() + */this.phoneNo.hashCode());

    }
}
