package com.example.poly.final_project.transport;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.poly.final_project.R;

import java.util.ArrayList;


public class BusStopFragment extends Fragment {

    public static final String TAG = "BusStopFragment";

    Button add_bus_stop_btn;
    ListView bus_stop_listview;
    ArrayList<Integer> bus_stops = new ArrayList<Integer>();
    BusStopAdapter bus_stop_adapter;
    Cursor bus_stop_cursor;

    private EditText input_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bus_stop, container, false);

        add_bus_stop_btn = (Button) view.findViewById(R.id.add_bus_stop_btn);
        bus_stop_listview = (ListView) view.findViewById(R.id.bus_stop_listview);

        final OCTranspoDbHelper dbHelper = OCTranspoDbHelper.getInstance(getContext());
        bus_stop_cursor = dbHelper.getBusStops();

        bus_stop_cursor.moveToFirst();

        if(bus_stop_cursor.getCount() > 0){
            do{
                try {
                    bus_stops.add(dbHelper.getBusStopNoFromCursor(bus_stop_cursor));
                }catch(CursorIndexOutOfBoundsException e){
                    Log.e(TAG, e.getMessage());
                }

            }while(bus_stop_cursor.moveToNext());
        }


        bus_stop_adapter = new BusStopAdapter(getContext());
        bus_stop_listview.setAdapter(bus_stop_adapter);

        add_bus_stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "add_bus_stop_btn clicked.");
                processBusStopOperation(view.getContext(), true);
            }
        });

        bus_stop_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, final long l) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete bus stop "+ bus_stops.get(i) +"?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dbHelper.remove(l);
                                bus_stops.remove(i);
                                refreshListView();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();
            }
        });

        return view;
    }

    private void processBusStopOperation(final Context context, final boolean insert){
        input_text = new EditText(context);
        input_text.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(context)
                .setTitle("Enter Bus Stop Number")
                .setView(input_text)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(insert){
                            Log.d(TAG, "Inserting: " + input_text.getText().toString());
                            Integer bus_stop_number = Integer.parseInt(input_text.getText().toString().trim());
                            ContentValues values = new ContentValues();
                            values.put(BusStopContract.BusStopEntry.COLUMN_NAME_BUS_STOP_NO, bus_stop_number);
                            OCTranspoDbHelper dbHelper = OCTranspoDbHelper.getInstance(context);
                            dbHelper.insert(values);
                            bus_stops.add(bus_stop_number);
                            refreshListView();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    private void refreshListView(){
        bus_stop_cursor = OCTranspoDbHelper.getInstance(getContext()).getBusStops();
        bus_stop_adapter.notifyDataSetChanged();
    }

    private class BusStopAdapter extends ArrayAdapter<String> {

        public BusStopAdapter(Context ctx){
            super(ctx, 0);
        }

        public int getCount(){
            return bus_stops.size();
        }

        public String getItem(int position){
            return bus_stops.get(position).toString();
        }

        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = BusStopFragment.this.getLayoutInflater();

            View result = inflater.inflate(R.layout.bus_stop_item, null);

            TextView message_text = (TextView) result.findViewById(R.id.bus_stop_number);
            message_text.setText(getItem(position));
            return result;
        }

        public long getItemId(int position){
            bus_stop_cursor.moveToPosition(position);
            return OCTranspoDbHelper.getInstance(getContext()).getIdFromCursor(bus_stop_cursor);
        }
    }
}
