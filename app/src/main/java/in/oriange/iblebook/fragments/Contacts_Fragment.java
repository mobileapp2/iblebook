package in.oriange.iblebook.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.oriange.iblebook.R;
import in.oriange.iblebook.adapters.ContactListRVAapter;
import in.oriange.iblebook.models.ContactListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class Contacts_Fragment extends Fragment {
    private Context context;
    private RecyclerView rv_contacts;
    private ProgressDialog pd;
    private List<ContactListPojo> contactList;
    private String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS};
    private SearchView searchView;
    private FloatingActionButton fab_request;
    private UserSessionManager session;
    private String user_id;
    public List<String> mobileNumbers;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
        init(rootView);
        setDefault();
        setEventHandlers();
        return rootView;
    }

    private void init(View rootView) {

        context = getActivity();
        pd = new ProgressDialog(context);
        session = new UserSessionManager(context);

        rv_contacts = rootView.findViewById(R.id.rv_contacts);
        fab_request = rootView.findViewById(R.id.fab_request);
        contactList = new ArrayList<ContactListPojo>();
        rv_contacts.setLayoutManager(new LinearLayoutManager(getContext()));
        searchView = rootView.findViewById(R.id.searchView);
        searchView.setFocusable(false);
        new getMobileNumbers().execute();
    }

    public class getMobileNumbers extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JSONObject obj = new JSONObject();
            try {
                obj.put("type", "getAllMobileNumbers");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            res = WebServiceCalls.APICall(ApplicationConstants.USERAPI, obj.toString());
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            mobileNumbers = new ArrayList<String>();
                            for (int i = 0; i < jsonarr.length(); i++) {
                                JSONObject jsonObj = jsonarr.getJSONObject(i);

                                mobileNumbers.add(jsonObj.getString("mobile"));

                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setDefault() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            askPermission();
        } else {
            new contactList().execute();
        }

        session = new UserSessionManager(context);
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            user_id = json.getString("user_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refresh() {
        if (contactList == null || contactList.size() == 0)
            setDefault();
    }

    private List getContactList2() {

        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactList.add(new ContactListPojo(String.valueOf(name.charAt(0)), name, phoneNumber.replaceAll("\\s+", "")));

        }
        phones.close();

        Set<ContactListPojo> s = new HashSet<ContactListPojo>();
        s.addAll(contactList);
        contactList = new ArrayList<ContactListPojo>();
        contactList.addAll(s);

        Collections.sort(contactList, new Comparator<ContactListPojo>() {
            @Override
            public int compare(ContactListPojo o1, ContactListPojo o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return contactList;
    }

    private void setEventHandlers() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!query.equals("")) {
                    searchView.clearFocus();
                    ArrayList<ContactListPojo> contactsSearchedList = new ArrayList<>();
                    for (ContactListPojo contacts : contactList) {
                        String contactToBeSearched = contacts.getName().toLowerCase() + contacts.getPhoneNo().toLowerCase();
                        if (contactToBeSearched.contains(query.toLowerCase())) {
                            contactsSearchedList.add(contacts);
                        }
                    }
                } else if (query.equals("")) {
                    bindRecyclerview(contactList);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    ArrayList<ContactListPojo> contactsSearchedList = new ArrayList<>();
                    for (ContactListPojo contacts : contactList) {
                        String contactToBeSearched = contacts.getName().toLowerCase() + contacts.getPhoneNo().toLowerCase();
                        if (contactToBeSearched.contains(newText.toLowerCase())) {
                            contactsSearchedList.add(contacts);
                        }
                    }
                } else if (newText.equals("")) {
                    bindRecyclerview(contactList);
                }
                return true;
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if (!query.equals("")) {
                    ArrayList<ContactListPojo> addressSearchedList = new ArrayList<>();
                    for (ContactListPojo contacts : contactList) {
                        String contactToBeSearched = contacts.getName().toLowerCase() +
                                contacts.getPhoneNo().toLowerCase();
                        if (contactToBeSearched.contains(query.toLowerCase())) {
                            addressSearchedList.add(contacts);
                        }
                    }
                    bindRecyclerview(addressSearchedList);
                } else {
                    bindRecyclerview(contactList);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    ArrayList<ContactListPojo> addressSearchedList = new ArrayList<>();
                    for (ContactListPojo contacts : contactList) {
                        String contactToBeSearched = contacts.getName().toLowerCase() +
                                contacts.getPhoneNo().toLowerCase();
                        if (contactToBeSearched.contains(newText.toLowerCase())) {
                            addressSearchedList.add(contacts);
                        }
                    }
                    bindRecyclerview(addressSearchedList);
                } else if (newText.equals("")) {
                    bindRecyclerview(contactList);
                }
                return true;
            }
        });


        fab_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSendRequestAlert();
            }
        });

    }

    private void createSendRequestAlert() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.prompt_request, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Make Request");
        alertDialogBuilder.setView(promptView);

        final EditText edt_mobile = promptView.findViewById(R.id.edt_mobile);
        final EditText edt_message = promptView.findViewById(R.id.edt_message);
        final TextView tv_requesttype = promptView.findViewById(R.id.tv_requesttype);

        tv_requesttype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] remarkName = {"Address Detail", "PAN Detail", "GST Detail", "Bank Detail", "All in One Details"};

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
                builderSingle.setTitle("Select Type");
                builderSingle.setCancelable(false);

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

                for (int i = 0; i < remarkName.length; i++) {
                    arrayAdapter.add(remarkName[i]);
                }

                builderSingle.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_requesttype.setText(remarkName[which]);
                    }
                });
                AlertDialog alert11 = builderSingle.create();
                alert11.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                alert11.show();
            }
        });

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialod, int id) {
                if (!Utilities.isMobileNo(edt_mobile)) {
                    Utilities.showMessageString(context, "Please Enter Valid Mobile Number");
                    return;
                }
                if (tv_requesttype.getText().toString().trim().equals("")) {
                    Utilities.showMessageString(context, "Please Select Type");
                    return;
                }
                if (edt_message.getText().toString().trim().equals("")) {
                    Utilities.showMessageString(context, "Please Enter Message");
                    return;
                }
                String type = "";

                if (tv_requesttype.getText().toString().trim().equals("Address Detail"))
                    type = "address";
                else if (tv_requesttype.getText().toString().trim().equals("PAN Detail"))
                    type = "pan";
                else if (tv_requesttype.getText().toString().trim().equals("GST Detail"))
                    type = "gst";
                else if (tv_requesttype.getText().toString().trim().equals("Bank Detail"))
                    type = "bank";
                else if (tv_requesttype.getText().toString().trim().equals("All in One Details"))
                    type = "allinone";

                if (Utilities.isNetworkAvailable(context)) {
                    new SendRequest().execute(
                            edt_message.getText().toString().trim(),
                            user_id,
                            edt_mobile.getText().toString().trim(),
                            type
                    );
                } else {
                    Utilities.showMessageString(context, "Please Check Internet Connection");
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int view) {
                dialog.cancel();
            }
        });

        alertDialogBuilder.setCancelable(false);
        AlertDialog alert = alertDialogBuilder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alert.show();
    }

    private void bindRecyclerview(List<ContactListPojo> contactList) {
        ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(new ScaleInAnimationAdapter(new ContactListRVAapter(context, contactList, mobileNumbers)));
        alphaAdapter.setDuration(500);
        alphaAdapter.setInterpolator(new OvershootInterpolator());
        alphaAdapter.setFirstOnly(false);
        rv_contacts.setAdapter(alphaAdapter);
    }

    public void askPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(PERMISSIONS, 1);
            return;
        } else {
            new contactList().execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new contactList().execute();
                } else {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                    builder.setTitle("Alert");
                    builder.setMessage("Please provide permission to allow Address Book to access your contacts");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.getPackageName(), null)));
//                            askPermission();
                        }
                    });
                    builder.create();
                    AlertDialog alertD = builder.create();
                    alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                    alertD.show();
                }
            }
        }
    }

    public class contactList extends AsyncTask<Void, Void, List> {

        @Override
        protected List doInBackground(Void... voids) {
            List contactList = getContactList2();
            return contactList;
        }

        @Override
        protected void onPostExecute(List list) {
            super.onPostExecute(list);
            bindRecyclerview(list);
        }
    }

    public class SendRequest extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JSONObject obj = new JSONObject();
            try {
                obj.put("task", "RequestData");
                obj.put("message", params[0]);
                obj.put("sender_id", params[1]);
                obj.put("mobile", params[2]);
                obj.put("type", params[3]);
                obj.put("record_id", "");
                obj.put("status", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            res = WebServiceCalls.APICall(ApplicationConstants.SENDREQUESTAPI, obj.toString());
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        Utilities.showAlertDialog(context, "Success", "Request Sent Successfully", true);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
