package in.oriange.iblebook.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import in.oriange.iblebook.models.GetBankListPojo;
import in.oriange.iblebook.models.GetTaxListPojo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "";
    private static String DB_NAME = "Database";
    private static final int DATABASE_VERSION = 4;

    private SQLiteDatabase myDataBase;
    private final Context myContext;


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        if (android.os.Build.VERSION.SDK_INT >= 4.2) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        System.out.println("Database path : " + DB_PATH);
    }

    public void createDataBase(boolean dbExist) throws IOException {
        if (dbExist) {

        } else {

            this.getReadableDatabase();
            try {
                System.out.println("Database copying started");
                copyDataBase();
                System.out.println("Database copying completed");
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private void copyDataBase() throws IOException {

        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    //*********************************************BANK DETAILS*******************************************************//

    public long insertBankDetailsInDb(String user_id,
                                      String name,
                                      String alias,
                                      String bank_name,
                                      String ifsc_code,
                                      String account_no,
                                      String file_path,
                                      String is_sent) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", user_id);
        values.put("name", name);
        values.put("alias", alias);
        values.put("bank_name", bank_name);
        values.put("ifsc_code", ifsc_code);
        values.put("account_no", account_no);
        values.put("file_path", file_path);
        values.put("is_sent", is_sent);
        long result = -1;
        try {
            database.beginTransaction();
            result = database.insertOrThrow("bank_details", null, values);
            System.out.println("Result :" + result);
            if (result == -1) {

            } else {
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }
        return result;
    }

    public long updateBankDetailsInDb(String bank_id,
                                      String user_id,
                                      String name,
                                      String alias,
                                      String bank_name,
                                      String ifsc_code,
                                      String account_no,
                                      String file_path,
                                      String is_sent) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", user_id);
        values.put("name", name);
        values.put("alias", alias);
        values.put("bank_name", bank_name);
        values.put("ifsc_code", ifsc_code);
        values.put("account_no", account_no);
        values.put("file_path", file_path);
        values.put("is_sent", is_sent);
        long result = -1;
        try {
            database.beginTransaction();
            result = database.update("bank_details", values, "bank_id=" + bank_id, null);
            if (result == -1) {

            } else {
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }
        return result;
    }

    public long deleteBankDetailsFromDb(String bank_id) {
        SQLiteDatabase database = this.getWritableDatabase();

        long result = -1;
        database.beginTransaction();

        result = database.delete("bank_details", "bank_id=?", new String[]{bank_id});

        if (result == -1)
            database.endTransaction();
        else
            database.setTransactionSuccessful();

        database.endTransaction();
        database.close();
        return result;
    }

    public ArrayList<GetBankListPojo> getBankListFromDb() {
        ArrayList<GetBankListPojo> bankList = new ArrayList<>();
        try {

            final String TABLE_NAME = "bank_details";
            String selectQuery = "SELECT  * FROM " + TABLE_NAME;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    GetBankListPojo data = new GetBankListPojo();
                    String bank_id = cursor.getString(0);
                    String user_id = cursor.getString(1);
                    String name = cursor.getString(2);
                    String alias = cursor.getString(3);
                    String bank_name = cursor.getString(4);
                    String ifsc_code = cursor.getString(5);
                    String account_no = cursor.getString(6);
                    String file_path = cursor.getString(7);
                    String is_sent = cursor.getString(8);

                    data.setBank_id(bank_id);
                    data.setAccount_holder_name(name);
                    data.setAlias(alias);
                    data.setBank_name(bank_name);
                    data.setIfsc_code(ifsc_code);
                    data.setAccount_no(account_no);
                    data.setDocument(file_path);
                    data.setCreated_by(user_id);
                    data.setUpdated_by(user_id);
                    bankList.add(data);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bankList;
    }

    //*********************************************TAX DETAILS*******************************************************//

    public long insertTaxDetailsInDb(String user_id,
                                     String name,
                                     String alias,
                                     String pan_number,
                                     String gst_number,
                                     String pan_document,
                                     String gst_document,
                                     String is_sent) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", user_id);
        values.put("name", name);
        values.put("alias", alias);
        values.put("pan_number", pan_number);
        values.put("gst_number", gst_number);
        values.put("pan_document", pan_document);
        values.put("gst_document", gst_document);
        values.put("is_sent", is_sent);
        long result = -1;
        try {
            database.beginTransaction();
            result = database.insertOrThrow("tax_details", null, values);
            if (result == -1) {

            } else {
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }
        return result;
    }

    public long updateTaxDetailsInDb(String tax_id,
                                     String user_id,
                                     String name,
                                     String alias,
                                     String pan_number,
                                     String gst_number,
                                     String pan_document,
                                     String gst_document,
                                     String is_sent)  {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", user_id);
        values.put("name", name);
        values.put("alias", alias);
        values.put("pan_number", pan_number);
        values.put("gst_number", gst_number);
        values.put("pan_document", pan_document);
        values.put("gst_document", gst_document);
        values.put("is_sent", is_sent);
        long result = -1;
        try {
            database.beginTransaction();
            result = database.update("tax_details", values, "tax_id=" + tax_id, null);
            if (result == -1) {

            } else {
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }
        return result;
    }


    public long deleteTaxDetailsFromDb(String tax_id) {
        SQLiteDatabase database = this.getWritableDatabase();

        long result = -1;
        database.beginTransaction();

        result = database.delete("tax_details", "tax_id=?", new String[]{tax_id});

        if (result == -1)
            database.endTransaction();
        else
            database.setTransactionSuccessful();

        database.endTransaction();
        database.close();
        return result;
    }

    public ArrayList<GetTaxListPojo> getGSTListFromDb() {
        ArrayList<GetTaxListPojo> gstList = new ArrayList<>();
        try {

            final String TABLE_NAME = "tax_details";
            String selectQuery = "SELECT  * FROM " + TABLE_NAME;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    GetTaxListPojo data = new GetTaxListPojo();
                    String tax_id = cursor.getString(0);
                    String user_id = cursor.getString(1);
                    String name = cursor.getString(2);
                    String alias = cursor.getString(3);
                    String pan_number = cursor.getString(4);
                    String gst_number = cursor.getString(5);
                    String pan_document = cursor.getString(6);
                    String gst_document = cursor.getString(7);
                    String is_sent = cursor.getString(8);

                    if (!gst_number.equals("")) {
                        data.setTax_id(tax_id);
                        data.setName(name);
                        data.setAlias(alias);
                        data.setPan_number(pan_number);
                        data.setGst_number(gst_number);
                        data.setPan_document(pan_document);
                        data.setGst_document(gst_document);
                        data.setCreated_by(user_id);
                        data.setUpdated_by(user_id);
                        gstList.add(data);
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gstList;
    }

    public ArrayList<GetTaxListPojo> getPANListFromDb() {
        ArrayList<GetTaxListPojo> panList = new ArrayList<>();
        try {

            final String TABLE_NAME = "tax_details";
            String selectQuery = "SELECT  * FROM " + TABLE_NAME;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    GetTaxListPojo data = new GetTaxListPojo();
                    String tax_id = cursor.getString(0);
                    String user_id = cursor.getString(1);
                    String name = cursor.getString(2);
                    String alias = cursor.getString(3);
                    String pan_number = cursor.getString(4);
                    String gst_number = cursor.getString(5);
                    String pan_document = cursor.getString(6);
                    String gst_document = cursor.getString(7);
                    String is_sent = cursor.getString(8);

                    if (!pan_number.equals("")) {
                        data.setTax_id(tax_id);
                        data.setName(name);
                        data.setAlias(alias);
                        data.setPan_number(pan_number);
                        data.setGst_number(gst_number);
                        data.setPan_document(pan_document);
                        data.setGst_document(gst_document);
                        data.setCreated_by(user_id);
                        data.setUpdated_by(user_id);
                        panList.add(data);
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return panList;
    }

}
