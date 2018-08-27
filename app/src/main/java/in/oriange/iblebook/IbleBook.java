package in.oriange.iblebook;

import android.app.Application;
import android.content.Context;

import java.io.File;

import in.oriange.iblebook.utilities.DataBaseHelper;

public class IbleBook extends Application {

    private Context context;
//    private boolean dbExist;
//    private DataBaseHelper dataBaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

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
