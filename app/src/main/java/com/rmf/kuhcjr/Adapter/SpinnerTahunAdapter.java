package com.rmf.kuhcjr.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rmf.kuhcjr.Data.DataKantor;
import com.rmf.kuhcjr.Data.DataSKPTahun;
import com.rmf.kuhcjr.R;

import java.util.List;

public class SpinnerTahunAdapter extends ArrayAdapter<DataSKPTahun> {

    LayoutInflater mInflate;
    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
//    private User[] values;
    private List<DataSKPTahun> list;
    public SpinnerTahunAdapter(Context context, int textViewResourceId,
                               List<DataSKPTahun> list) {
        super(context, textViewResourceId, list);
        this.context = context;
        this.list = list;
        mInflate = LayoutInflater.from(context);
    }

    @Override
    public int getCount(){
        return list.size();
    }

    @Override
    public DataSKPTahun getItem(int position){
        return list.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
//        TextView label = (TextView) super.getView(position, convertView, parent);
//        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)


        // And finally return your dynamic (or custom) view for each spinner item
        return createItemView(position, convertView, parent);
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
//        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
//        label.setTextColor(Color.BLACK);
//        label.setText(list.get(position).getLokasi());



        return createItemView(position, convertView, parent);
    }
    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view = mInflate.inflate(R.layout.row_lokasi, parent, false);

        TextView lokasi = (TextView) view.findViewById(R.id.lokasi_text);

        DataSKPTahun dataSKPTahun = list.get(position);

        lokasi.setText(dataSKPTahun.getTahun());


        return view;
    }
}
