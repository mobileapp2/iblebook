package in.oriange.iblebook.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.oriange.iblebook.R;
import in.oriange.iblebook.models.GetAddressListPojo;

import java.util.List;

public class GetMyAddressListAdapter extends RecyclerView.Adapter<GetMyAddressListAdapter.MyViewHolder> {

    Context context;
    List<GetAddressListPojo> resultArrayList;

    public GetMyAddressListAdapter(Context context, List<GetAddressListPojo> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_bank, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_initletter.setText(String.valueOf(resultArrayList.get(position).getName().charAt(0)));
//        holder.tv_bankname.setText(resultArrayList.get(position).getBank_name());
        holder.tv_bankname.setText("Office");
        holder.tv_accountno.setText(resultArrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_initletter, tv_bankname, tv_accountno;


        public MyViewHolder(View view) {
            super(view);
            tv_initletter = (TextView) view.findViewById(R.id.tv_initletter);
            tv_bankname = (TextView) view.findViewById(R.id.tv_bankname);
            tv_accountno = (TextView) view.findViewById(R.id.tv_accountno);
        }
    }

}
