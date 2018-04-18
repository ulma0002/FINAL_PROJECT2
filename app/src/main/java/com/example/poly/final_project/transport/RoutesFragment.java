package com.example.poly.final_project.transport;


import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.poly.final_project.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class RoutesFragment extends Fragment {

    public static final String TAG = "RoutesFragment";

    public static final String TAG_BusStopNo = "StopNo";
    public static final String TAG_Routes = "Routes";
    public static final String TAG_Route = "Route";
    public static final String TAG_RouteNo = "RouteNo";
    public static final String TAG_RouteDirection = "Direction";
    public static final String TAG_RouteHeading = "RouteHeading";

    ListView routes_listview;
    ArrayList<HashMap> routes = new ArrayList<HashMap>();
    RoutesAdapter routesAdapter;
    Cursor bus_stop_cursor;
    OCTranspoDbHelper dbHelper;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_routes, container, false);

        routes_listview = (ListView) view.findViewById(R.id.routes_listview);
        routesAdapter = new RoutesAdapter(getContext());
        routes_listview.setAdapter(routesAdapter);

        dbHelper = OCTranspoDbHelper.getInstance(getContext());

        bus_stop_cursor = dbHelper.getBusStops();
        bus_stop_cursor.moveToFirst();

        ArrayList<Integer> bus_stops = new ArrayList<Integer>();

        if(bus_stop_cursor.getCount() > 0){
            do{
                bus_stops.add(dbHelper.getBusStopNoFromCursor(bus_stop_cursor));
            }while(bus_stop_cursor.moveToNext());
        }


        AsyncRoutesDownloader routesDownloader = new AsyncRoutesDownloader();
        routesDownloader.execute(bus_stops);

        routes_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putString(TAG_BusStopNo, routes.get(i).get(TAG_BusStopNo).toString());
                bundle.putString(TAG_RouteNo, routes.get(i).get(TAG_RouteNo).toString());
                bundle.putString(TAG_RouteDirection, routes.get(i).get(TAG_RouteDirection).toString());
                bundle.putString(TAG_RouteHeading, routes.get(i).get(TAG_RouteHeading).toString());

                RouteDetailsFragment fragment = new RouteDetailsFragment();
                fragment.setArguments(bundle);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_holder, fragment);
                ft.addToBackStack(TAG);
                ft.commit();
            }
        });

        return view;
    }

    private class RoutesAdapter extends ArrayAdapter<HashMap>{

        RoutesAdapter(Context context) {
            super(context, 0);
        }

        public int getCount(){
            return routes.size();
        }

        public HashMap getItem(int position){
            return routes.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = RoutesFragment.this.getLayoutInflater();

            View result = inflater.inflate(R.layout.route_item, null);

            HashMap<String, String> route = getItem(position);

            TextView stop_no = (TextView) result.findViewById(R.id.stop_no);
            stop_no.setText(route.get(TAG_BusStopNo));

            TextView route_no = (TextView) result.findViewById(R.id.route_no);
            route_no.setText(route.get(TAG_RouteNo));

            TextView route_direction = (TextView) result.findViewById(R.id.route_direction);
            route_direction.setText(route.get(TAG_RouteDirection));

            TextView route_heading = (TextView) result.findViewById(R.id.route_heading);
            route_heading.setText(route.get(TAG_RouteHeading));

            return result;
        }

//        public long getItemId(int position){
//            bus_stop_cursor.moveToPosition(position);
//            return OCTranspoDbHelper.getInstance(getContext()).getIdFromCursor(bus_stop_cursor);
//        }
    }

    private class AsyncRoutesDownloader extends AsyncTask<ArrayList<Integer>, String, ArrayList<HashMap>>{

        private final String API_KEY = "ab27db5b435b8c8819ffb8095328e775";
        private final String APP_ID = "223eb5c3";
        private final String BASE_URL = "https://api.octranspo1.com/v1.2/GetRouteSummaryForStop";
        private final String QUERY = String.format("%s?appID=%s&&apiKey=%s&stopNo=%s", BASE_URL, APP_ID, API_KEY, "%d");

        private Integer bus_stop_no;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle(getString(R.string.wait));
            progressDialog.setTitle(getString(R.string.fetch_route_info));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<HashMap> doInBackground(ArrayList<Integer>... integers) {
            ArrayList<HashMap> received_routes = new ArrayList<HashMap>();
            for(int i=0;i<integers[0].size();i++){
                bus_stop_no = integers[0].get(i);
                XmlPullParser recievedData = tryDownloadingXmlData(bus_stop_no);
                received_routes.addAll(tryParsingXmlData(recievedData));
            }

            return received_routes;
        }

        private XmlPullParser tryDownloadingXmlData(Integer bus_stop_number) {
            try{
                URL xmlUrl = new URL(String.format(QUERY, bus_stop_number));
                Log.d(TAG, "QUERY: "+xmlUrl.toString());
                XmlPullParser receivedData = XmlPullParserFactory.newInstance().newPullParser();
                receivedData.setInput(xmlUrl.openStream(), null);
                return receivedData;
            }catch(XmlPullParserException e) {
                Log.e(TAG, "XmlPullParserException", e);
            }catch(IOException e){
                Log.e(TAG, "IOException", e);
            }
            return null;
        }

        private ArrayList<HashMap> tryParsingXmlData(XmlPullParser recievedData) {
            if( recievedData != null){
                try{
                    return processXmLData(recievedData);
                }catch(XmlPullParserException e) {
                    Log.e(TAG, "XmlPullParserException", e);
                }catch(IOException e){
                    Log.e(TAG, "IOException", e);
                }
            }
            return null;
        }

        private ArrayList<HashMap> processXmLData(XmlPullParser parser) throws XmlPullParserException, IOException {
            ArrayList<HashMap> routes = new ArrayList<HashMap>();
            while(parser.next() != XmlPullParser.END_DOCUMENT){
                if(parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals(TAG_Routes)){
                    break;
                }
            }
            parser.require(XmlPullParser.START_TAG, null, TAG_Routes);
            while(parser.next() != XmlPullParser.END_TAG){
                Log.d(TAG, "processXmlData: inside  while" );
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                Log.d(TAG, "processXmlData: after if" );
                String tagName = parser.getName().toLowerCase();
                Log.d(TAG, "processXmlData: tagName: " + tagName );
                if(tagName.equals("route")){
                    routes.add(readRoute(parser));
                }else{
//                    skip(parser);
                }
            }
            Log.d(TAG, "processXmlData: " + routes.size());
            return routes;
        }

        private HashMap readRoute(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, TAG_Route);
            HashMap<String, String> route = new HashMap<String, String>();
            route.put(TAG_BusStopNo, bus_stop_no.toString());
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals(TAG_RouteNo)) {
                    route.put(TAG_RouteNo, readRouteNo(parser));
                } else if (name.equals(TAG_RouteDirection)) {
                    route.put(TAG_RouteDirection, readRouteDirection(parser));
                } else if (name.equals(TAG_RouteHeading)) {
                    route.put(TAG_RouteHeading, readRouteHeading(parser));
                } else {
                    skip(parser);
                }
            }
            Log.d(TAG, "Route: " + route.toString());
            return route;
        }

        private String readRouteNo(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, TAG_RouteNo);
            String routeno = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, TAG_RouteNo);
            Log.d(TAG, "readRouteNo: " + routeno);
            return routeno;
        }

        private String readRouteDirection(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, TAG_RouteDirection);
            String direction = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, TAG_RouteDirection);
            Log.d(TAG, "routeDirection: " + direction);
            return direction;
        }

        private String readRouteHeading(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, TAG_RouteHeading);
            String heading = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, TAG_RouteHeading);
            Log.d(TAG, "routeHeading: " + heading);
            return heading;
        }

        private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
            String result = "";
            if (parser.next() == XmlPullParser.TEXT) {
                result = parser.getText();
                parser.nextTag();
            }
            return result;
        }

        private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new IllegalStateException();
            }
            int depth = 1;
            while (depth != 0) {
                Log.d(TAG, "Skipping tagName: " + parser.getName());
                switch (parser.next()) {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap> hashMaps) {
            progressDialog.dismiss();
            routes = hashMaps;
            routesAdapter.notifyDataSetChanged();
        }
    }
}
