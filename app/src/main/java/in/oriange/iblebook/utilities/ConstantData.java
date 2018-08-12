package in.oriange.iblebook.utilities;

public class ConstantData {

    public static ConstantData _instance;

    private ConstantData() {}

    public static ConstantData getInstance() {
        if (_instance == null) {
            _instance = new ConstantData();
        }
        return _instance;
    }

}