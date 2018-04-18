package com.example.poly.final_project.transport;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poly.final_project.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.poly.final_project.transport.RoutesFragment.TAG_BusStopNo;
import static com.example.poly.final_project.transport.RoutesFragment.TAG_Route;
import static com.example.poly.final_project.transport.RoutesFragment.TAG_RouteDirection;
import static com.example.poly.final_project.transport.RoutesFragment.TAG_RouteHeading;
import static com.example.poly.final_project.transport.RoutesFragment.TAG_RouteNo;
import static com.example.poly.final_project.transport.RoutesFragment.TAG_Routes;

public class RouteDetailsFragment extends Fragment {

    public static final String TAG = "RouteDetailsFragment";

    public static final String TAG_TripLabel = "RouteLabel";
    public static final String TAG_TripDirection = "RouteDirection";
    public static final String TAG_Trips = "Trips";
    public static final String TAG_Trip = "Trip";
    public static final String TAG_TripDestination = "TripDestination";
    public static final String TAG_TripStartTime = "TripStartTime";
    public static final String TAG_TripAdjustedTime = "AdjustedScheduleTime";
    public static final String TAG_TripLatitude = "Latitude";
    public static final String TAG_TripLongitude = "Longitude";
    public static final String TAG_TripGPSSpeed = "GPSSpeed";

    public static String bus_stop_no;
    public static String route_no;
    public static String route_heading;
    public static String route_direction;

    TextView textView_route_no;
    TextView textView_route_heading;
    TextView textView_route_direction;

    ListView trips_listview;
    TripsAdapter tripsAdapter;
    ArrayList<HashMap> trips = new ArrayList<HashMap>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_route_details, container, false);

        bus_stop_no = getArguments().getString(TAG_BusStopNo);
        route_no = getArguments().getString(TAG_RouteNo);
        route_heading = getArguments().getString(TAG_RouteHeading);
        route_direction = getArguments().getString(TAG_RouteDirection);

        textView_route_no = (TextView) view.findViewById(R.id.route_no);
        textView_route_no.setText(R.string.route+": " + route_no);
        textView_route_heading = (TextView) view.findViewById(R.id.route_heading);
        textView_route_heading.setText(R.string.route_name+": " + route_heading);
        textView_route_direction = (TextView) view.findViewById(R.id.route_direction);
        textView_route_direction.setText(R.string.route_direction": " + route_direction);

        trips_listview = (ListView) view.findViewById(R.id.trips_listview);
        tripsAdapter = new TripsAdapter(getContext());
        trips_listview.setAdapter(tripsAdapter);

        AsyncTripsDownloader tripsDownloader = new AsyncTripsDownloader();
        tripsDownloader.execute(bus_stop_no, route_no);

        return view;
    }

    private class TripsAdapter extends ArrayAdapter<HashMap> {

        TripsAdapter(Context context) {
            super(context, 0);
        }

        public int getCount(){
            return trips.size();
        }

        public HashMap getItem(int position){
            return trips.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = RouteDetailsFragment.this.getLayoutInflater();

            View result = inflater.inflate(R.layout.trip_item, null);

            HashMap<String, String> route = getItem(position);

            TextView trip_destination = (TextView) result.findViewById(R.id.trip_destination);
            trip_destination.setText(R.string.trip_destination + ": " + route.get(TAG_TripDestination));

            TextView trip_starttime = (TextView) result.findViewById(R.id.trip_start_time);
            trip_starttime.setText(R.string.trip_starttime + ": " + route.get(TAG_TripStartTime));

            TextView trip_adjusted_time = (TextView) result.findViewById(R.id.trip_adjusted_time);
            trip_adjusted_time.setText(R.string.trip_adjusted_time + ": " + route.get(TAG_TripAdjustedTime));

            TextView trip_location = (TextView) result.findViewById(R.id.trip_lat_lng);
            trip_location.setText(R.string.trip_location + " (Lat,Lng): " + route.get(TAG_TripLatitude)+","+route.get(TAG_TripLongitude));

            TextView trip_gps_speed = (TextView) result.findViewById(R.id.trip_gps_speed);
            trip_gps_speed.setText(R.string.trip_gps_speed + ": " + route.get(TAG_TripGPSSpeed));

            return result;
        }

//        public long getItemId(int position){
//            bus_stop_cursor.moveToPosition(position);
//            return OCTranspoDbHelper.getInstance(getContext()).getIdFromCursor(bus_stop_cursor);
//        }
    }

    private class AsyncTripsDownloader extends AsyncTask<String, String, ArrayList<HashMap>> {

        private final String API_KEY = "ab27db5b435b8c8819ffb8095328e775";
        private final String APP_ID = "223eb5c3";
        private final String BASE_URL = "https://api.octranspo1.com/v1.2/GetNextTripsForStop";
        private final String QUERY = String.format("%s?appID=%s&&apiKey=%s&stopNo=%s&routeNo=%s", BASE_URL, APP_ID, API_KEY, "%s", "%s");

        private String bus_stop_no;
        private String route_no;

        @Override
        protected ArrayList<HashMap> doInBackground(String... strings) {
            bus_stop_no = strings[0];
            route_no = strings[1];
            ArrayList<HashMap> received_trips = new ArrayList<HashMap>();
            XmlPullParser recievedData = tryDownloadingXmlData(bus_stop_no, route_no);
            received_trips.addAll(tryParsingXmlData(recievedData));
            return received_trips;
        }

        private XmlPullParser tryDownloadingXmlData(String bus_stop_no, String route_no) {
            try{
                URL xmlUrl = new URL(String.format(QUERY, bus_stop_no, route_no));
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
            Log.d(TAG, "Inside processXmlData");
            ArrayList<HashMap> trips = new ArrayList<HashMap>();
            while(parser.next() != XmlPullParser.END_DOCUMENT){
                if(parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals(TAG_Route)){
                    break;
                }
            }
            Log.d(TAG, "Found " + TAG_Route);
            parser.require(XmlPullParser.START_TAG, null, TAG_Route);
            while(parser.next() != XmlPullParser.END_TAG){
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String tagName = parser.getName();
                if(tagName.equals(TAG_TripDirection)){
                    Log.d(TAG, "Found " + TAG_TripDirection);
                    trips.addAll(processDirections(parser));
                }
            }
            return trips;
        }

        private ArrayList<HashMap> processDirections(XmlPullParser parser) throws XmlPullParserException, IOException {
            Log.d(TAG, "Inside processDirections");
            ArrayList<HashMap> trips = new ArrayList<HashMap>();
//            while(parser.next() != XmlPullParser.END_DOCUMENT){
//                if(parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals(TAG_TripDirection)){
//                    break;
//                }
//            }
            boolean match = true;
            parser.require(XmlPullParser.START_TAG, null, TAG_TripDirection);
            while(parser.next() != XmlPullParser.END_TAG){
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String tagName = parser.getName();
                if(tagName.equals(TAG_RouteNo)){
                    Log.d(TAG, "Found " + TAG_RouteNo);
                    if(!RouteDetailsFragment.route_no.equals(readRouteNo(parser))){
                        match = false;
                    }
                }else if(tagName.equals(TAG_TripLabel)){
                    Log.d(TAG, "Found " + TAG_TripLabel);
                    if(!RouteDetailsFragment.route_heading.equals(readTripLabel(parser))){
                        match = false;
                    }
                }else if(tagName.equals(TAG_RouteDirection)){
                    Log.d(TAG, "Found " + TAG_RouteDirection);
                    if(!RouteDetailsFragment.route_direction.equals(readRouteDirection(parser))){
                        match = false;
                    }
                }else if(tagName.equals(TAG_Trips)){
                    Log.d(TAG, "Found " + TAG_Trips + " Match: " + match);
                    if(match){
                        trips.addAll(processTrips(parser));
                    }else {
                        skip(parser);
                    }
                    match = true;
                }else{
                    Log.d(TAG, "Starting to skip from" + tagName);
                    skip(parser);
                }
            }
            return trips;
        }

        private ArrayList<HashMap> processTrips(XmlPullParser parser) throws XmlPullParserException, IOException {
            Log.d(TAG, "Inside processTrips");
            ArrayList<HashMap> trips = new ArrayList<HashMap>();
//            while(parser.next() != XmlPullParser.END_DOCUMENT){
//                if(parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals(TAG_Trips)){
//                    break;
//                }
//            }
            parser.require(XmlPullParser.START_TAG, null, TAG_Trips);
            while(parser.next() != XmlPullParser.END_TAG){
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String tagName = parser.getName();
                if(tagName.equals(TAG_Trip)){
                    Log.d(TAG, "Found " + TAG_Trip);
                    trips.add(readTrip(parser));
                }else{
                    skip(parser);
                }
            }

            return trips;
        }

        private HashMap readTrip(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, TAG_Trip);
            HashMap<String, String> route = new HashMap<String, String>();
            route.put(TAG_BusStopNo, bus_stop_no.toString());
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals(TAG_TripDestination)) {
                    Log.d(TAG, "Found " + TAG_TripDestination);
                    route.put(TAG_TripDestination, readTripDestination(parser));
                } else if (name.equals(TAG_TripStartTime)) {
                    Log.d(TAG, "Found " + TAG_TripStartTime);
                    route.put(TAG_TripStartTime, readTripStartTime(parser));
                } else if (name.equals(TAG_TripAdjustedTime)) {
                    Log.d(TAG, "Found " + TAG_TripAdjustedTime);
                    route.put(TAG_TripAdjustedTime, readTripAdjustedTime(parser));
                } else if (name.equals(TAG_TripLatitude)) {
                    Log.d(TAG, "Found " + TAG_TripLatitude);
                    route.put(TAG_TripLatitude, readTripLatitude(parser));
                } else if (name.equals(TAG_TripLongitude)) {
                    Log.d(TAG, "Found " + TAG_TripLongitude);
                    route.put(TAG_TripLongitude, readTripLongitude(parser));
                } else if (name.equals(TAG_TripGPSSpeed)) {
                    Log.d(TAG, "Found " + TAG_TripGPSSpeed);
                    route.put(TAG_TripGPSSpeed, readTripGPSSpeed(parser));
                } else {
                    Log.d(TAG, "Starting to skip from" + name);
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

        private String readTripLabel(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, TAG_TripLabel);
            String heading = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, TAG_TripLabel);
            Log.d(TAG, "routeHeading: " + heading);
            return heading;
        }

        private String readTripDestination(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, TAG_TripDestination);
            String str = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, TAG_TripDestination);
            Log.d(TAG, "routeHeading: " + str);
            return str;
        }

        private String readTripStartTime(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, TAG_TripStartTime);
            String str = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, TAG_TripStartTime);
            Log.d(TAG, "routeHeading: " + str);
            return str;
        }

        private String readTripAdjustedTime(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, TAG_TripAdjustedTime);
            String str = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, TAG_TripAdjustedTime);
            Log.d(TAG, "routeHeading: " + str);
            return str;
        }

        private String readTripLatitude(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, TAG_TripLatitude);
            String str = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, TAG_TripLatitude);
            Log.d(TAG, "routeHeading: " + str);
            return str;
        }

        private String readTripLongitude(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, TAG_TripLongitude);
            String str = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, TAG_TripLongitude);
            Log.d(TAG, "routeHeading: " + str);
            return str;
        }

        private String readTripGPSSpeed(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, TAG_TripGPSSpeed);
            String str = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, TAG_TripGPSSpeed);
            Log.d(TAG, "routeHeading: " + str);
            return str;
        }

        private String readTextTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, tag);
            String str = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, tag);
            Log.d(TAG, tag + ": " + str);
            return str;
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
            trips.addAll(hashMaps);
            tripsAdapter.notifyDataSetChanged();
        }
    }
}
