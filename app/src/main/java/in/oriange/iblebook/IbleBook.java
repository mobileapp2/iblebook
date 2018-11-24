package in.oriange.iblebook;

import android.app.Application;
import android.content.Context;

public class IbleBook extends Application {

    private Context context;
//    private boolean dbExist;
//    private DataBaseHelper dataBaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
//        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto-Medium.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf

//        try {
//            File dbFile = context.getDatabasePath("Database");
//            dbExist = dbFile.exists();
//            dataBaseHelper = new DataBaseHelper(context);
//
//            dataBaseHelper.createDataBase(dbExist);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
