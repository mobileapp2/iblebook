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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.oriange.iblebook.R;
import in.oriange.iblebook.adapters.ContactListRVAapter;
import in.oriange.iblebook.models.ContactListPojo;
import in.oriange.iblebook.utilities.Utilities;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class Contacts_Fragment extends Fragment {
    private Context context;
    private RecyclerView rv_contacts;
    private ProgressDialog pd;
    private List<ContactListPojo> contactList;
    private String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS};
    private SearchView searchView;

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

        rv_contacts = rootView.findViewById(R.id.rv_contacts);
        contactList = new ArrayList<ContactListPojo>();
        rv_contacts.setLayoutManager(new LinearLayoutManager(getContext()));
        searchView = rootView.findViewById(R.id.searchView);
    }

    private void setDefault() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            askPermission();
        } else {
            new contactList().execute();
        }
    }

    public void refresh() {
        if (contactList.size() == 0)
            setDefault();
    }

    public class contactList extends AsyncTask<Void, Void, List> {

        @Override
        protected List doInBackground(Void... voids) {
            List contactList = getContactList();
            return contactList;
        }

        @Override
        protected void onPostExecute(List list) {
            super.onPostExecute(list);
            ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(new ScaleInAnimationAdapter(new ContactListRVAapter(context, list)));
            alphaAdapter.setDuration(500);
            alphaAdapter.setInterpolator(new OvershootInterpolator());
            alphaAdapter.setFirstOnly(false);
//            rv_contacts.setAdapter(new SlideInBottomAnimationAdapter(alphaAdapter));
            rv_contacts.setAdapter(alphaAdapter);
        }
    }

    private List getContactList() {
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactList.add(new ContactListPojo(String.valueOf(name.charAt(0)), name, phoneNo.replaceAll("\\s+", "")));
                    }

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

                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return contactList;
    }

    private void setEventHandlers()  {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                ArrayList<ContactListPojo> contactsSearchedList = new ArrayList<>();
                for (ContactListPojo contacts : contactList) {
                    if (contacts.getName() != null && contacts.getName().toLowerCase().contains(query.toLowerCase())) {
                        contactsSearchedList.add(contacts);
                    }
                }

                if (contactsSearchedList.size() == 0) {
                    Utilities.showAlertDialog(context, "Fail", "No Such Contact Found", false);
                    searchView.setQuery("", false);
                    ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(new ScaleInAnimationAdapter(new ContactListRVAapter(context, contactList)));
                    alphaAdapter.setDuration(500);
                    alphaAdapter.setInterpolator(new OvershootInterpolator());
                    alphaAdapter.setFirstOnly(false);
                    rv_contacts.setAdapter(alphaAdapter);
                } else {
                    ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(new ScaleInAnimationAdapter(new ContactListRVAapter(context, contactsSearchedList)));
                    alphaAdapter.setDuration(500);
                    alphaAdapter.setInterpolator(new OvershootInterpolator());
                    alphaAdapter.setFirstOnly(false);
                    rv_contacts.setAdapter(alphaAdapter);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(new ScaleInAnimationAdapter(new ContactListRVAapter(context, contactList)));
                    alphaAdapter.setDuration(500);
                    alphaAdapter.setInterpolator(new OvershootInterpolator());
                    alphaAdapter.setFirstOnly(false);
                    rv_contacts.setAdapter(alphaAdapter);
                }
                return true;
            }
        });

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
                    builder.show();
                }
            }
        }
    }

}
