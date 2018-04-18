package com.example.poly.final_project.transport;

import android.provider.BaseColumns;


public final class BusStopContract {

    BusStopContract() {}

    public static class BusStopEntry implements BaseColumns {
        public static final String TABLE_NAME = "bus_stops";
        public static final String COLUMN_NAME_BUS_STOP_NO = "bus_stop_no";
    }
}
