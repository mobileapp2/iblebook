package in.oriange.iblebook.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.oriange.iblebook.R;
import in.oriange.iblebook.models.ContactListPojo;

import java.util.List;

public class ContactListRVAapter extends RecyclerView.Adapter<ContactListRVAapter.MyViewHolder> {

    Context context;
    List<ContactListPojo> contactList;

    public ContactListRVAapter(Context context, List<ContactListPojo> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_contact, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_initletter.setText(contactList.get(position).getInitLetter());
        holder.tv_name.setText(contactList.get(position).getName());
        holder.tv_phoneno.setText(contactList.get(position).getPhoneNo());
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_initletter, tv_name, tv_phoneno;


        public MyViewHolder(View view) {
            super(view);
            tv_initletter = (TextView) view.findViewById(R.id.tv_initletter);
            tv_name = (TextView) view.findViewById(R.id.tv_bankname);
            tv_phoneno = (TextView) view.findViewById(R.id.tv_accountno);
        }
    }

}
