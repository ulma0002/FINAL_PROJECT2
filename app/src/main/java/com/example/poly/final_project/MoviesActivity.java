package com.example.poly.final_project;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MoviesActivity extends AppCompatActivity {

    protected static final String ACTIVITY_NAME = "MoviesActivity";
    ArrayList<Movie> movies_list = new ArrayList<>();
    ArrayList<Long>  movies_ID   = new ArrayList<>();
    ListView       listView;
    Button         buttonAdd;
    //EditText       moviesText;
    MoviesAdapter  moviesAdapter;
    Cursor         cursor;
    SQLiteDatabase db;
    ProgressBar    progressBar;
    Toolbar        toolbar;
    private boolean isTablet;
    private boolean importedFromXml = false;

    private String title;
    private String actors;
    private String length;
    private String description;
    private String rating; //rating (out of 4 starts)
    private String genre; //(comedy, horror, action…)
    private String url; //url of the movie poster
    private long id;
    Bitmap pic;

    Double high_rating = 0.0;
    Double low_rating  = 5.0;
    Double average_rating;

    MovieFragment movieFragment = new MovieFragment();


    public class MoviesAdapter extends ArrayAdapter<Movie> {
        //constructor for ChatAdapter that takes a Context parameter,
        // and passes it to the parent constructor (ArrayAdapter)
        MoviesAdapter(Context ctx)

        {
            super(ctx, 0);
        }

        @Override
        public int getCount()
        //returns the number of rows that
        // will be in the listView,the number of strings in the array list
        {
            return movies_list.size();
        }

        @Override
        public Movie getItem(int position)
        //returns the item to show in the list at the specified position
        {
            return movies_list.get(position);
        }

        @Override
        public long getItemId(int position) {
//returns the database id of the item at position i

            return movies_ID.get(position);
            //return position;
        }

        //then iterates in a for loop, calling  getView(int position)
        @Override
        public View getView(int position, View result, ViewGroup parent) {
            LayoutInflater inflater = MoviesActivity.this.getLayoutInflater();
            View           resList  = result;
           // if (resList == null) {
                result = inflater.inflate(R.layout.movie_row_line, null);//set movie_row_line for displaying items
            //}

            Movie myMovie = getItem(position);

            TextView movies_title = result.findViewById(R.id.title);//get id for  view
            movies_title.setText(myMovie.getTitle());
            TextView movies_actor = result.findViewById(R.id.actors);
            movies_actor.setText(myMovie.getActors());
            TextView movies_length = result.findViewById(R.id.length);
            movies_length.setText(myMovie.getLength());

            ImageView       imageView = result.findViewById(R.id.image);
            String url       = myMovie.getUrl();
            String          fileName  = url.substring(url.lastIndexOf(':') + 1);
            FileInputStream fis       = null;
            try {
                fis = openFileInput(fileName + ".png");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            pic = BitmapFactory.decodeStream(fis);

            imageView.setImageBitmap(pic);
            return result;//this returns the movie_row_line that will be positioned at the specified row in the list
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        //creates a temporary ChatDatabaseHelper object
        MovieDatabaseHelper movieDbHelper = new MovieDatabaseHelper(this);

        // gets a writable database and stores that as an instance variable
        db = movieDbHelper.getWritableDatabase();

         isTablet = findViewById(R.id.frameLayout) != null;

        listView = findViewById(R.id.list_View);
        buttonAdd = findViewById(R.id.button_Add_movie);

        moviesAdapter = new MoviesAdapter(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progressBar);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("title", movies_list.get(i).getTitle());
                bundle.putString("actors", movies_list.get(i).getActors());
                bundle.putString("length", movies_list.get(i).getLength());
                bundle.putString("description", movies_list.get(i).getDescription());
                bundle.putString("rating", movies_list.get(i).getRating());
                bundle.putString("genre", movies_list.get(i).getGenre());
                bundle.putString("url", movies_list.get(i).getUrl());
                bundle.putLong("ID", id);
//if on tablet:
                if(!isTablet)//this is a phone
                 {
                Intent goTo = new Intent(MoviesActivity.this, MovieDetails.class);
                goTo.putExtras(bundle);
                startActivityForResult(goTo, 1);

                }
               else //this is Tablet
                {
                    FragmentManager     fragmentManager     = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //movieFragment.setIsTablet(true);
                    movieFragment.setArguments( bundle );
                    fragmentTransaction.addToBackStack(" ") //You can call transaction.addToBackStack(String name) if you want to undo this transaction
                            // with the back button. Otherwise the back button changes the Activity. The name parameter is optional.
                            .replace( R.id.frameLayout , movieFragment)
                            .commit();
                }
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MoviesActivity.this, MovieFavorite.class), 5);
            }
        });
        cursor = db.query(true, MovieDatabaseHelper.TABLE_NAME, new String[]
                        {MovieDatabaseHelper.KEY_ID, MovieDatabaseHelper.KEY_TITLE},
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {

            movies_list.add(new Movie(cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_ACTORS)),
                    cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_LENGTH)),
                    cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_RATING)),
                    cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_GENRE)),
                    cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_URL)),
                    new Long(cursor.getInt(cursor.getColumnIndex(MovieDatabaseHelper.KEY_ID)))));
            cursor.moveToNext();
        }
        listView.setAdapter(moviesAdapter);
    }


    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.toolbar_menu, m);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem mi) {
        int                 id = mi.getItemId();
        AlertDialog.Builder builder;

        switch (id) {
            case R.id.action_one:
                Intent clinic_intent = new Intent(MoviesActivity.this, ClinicActivity.class);
                startActivityForResult(clinic_intent, 50);
                break;

            case R.id.action_two:
                Intent transport_intent = new Intent(MoviesActivity.this, TransportActivity.class);
                startActivityForResult(transport_intent, 50);
                break;
            case R.id.action_three:
                Intent quiz_intent = new Intent(MoviesActivity.this, QuizActivity.class);
                startActivityForResult(quiz_intent, 50);
                break;
            case R.id.action_about:
                Log.d("Toolbar", "Action about selected");
                builder = new AlertDialog.Builder(this);
                builder.setTitle("Movie Activity version 1.0 was created by Galina Ulman.");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
//getWindow().getDecorView().getRootView()
                //findViewById(android.R.id.content)
                break;
            case R.id.data_load:
                new MovieQuery().execute();
                if (importedFromXml)
                    Snackbar.make(getWindow().getDecorView().getRootView(),"Movies were successfully imported", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                importedFromXml=false;
                break;
            case R.id.report_load:
                //--------------statistics on the highest, lowest, and average movie rating--------------
                int k=0;
                average_rating =0.0;
                for (int i=0;  i < movies_list.size(); i++ ) {
                    try {
                        Double itemRating = Double.parseDouble(movies_list.get(i).getRating());
                        average_rating += itemRating;
                        if (itemRating> high_rating) high_rating = itemRating;
                        if (itemRating< low_rating) low_rating = itemRating;
                        k++;
                    } catch (Exception e) {
                        continue;
                    }
                }
                average_rating = average_rating/((double)(k));
                if (low_rating ==5.0) low_rating=0.0;
                String averageFormated = new DecimalFormat("##.##").format(average_rating);

                AlertDialog.Builder builderSummary = new AlertDialog.Builder(MoviesActivity.this);
                builderSummary.setTitle("Report about stored data: ");
                builderSummary.setMessage(getString(R.string.highest_result)+" "+high_rating+getString(R.string.lowest_result)+" "+low_rating+getString(R.string.average_result) +" "+averageFormated)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                builderSummary.create().show();
                break;


        }
        return true;
    }

    public void deleteMovie(long id) {
        db.delete(MovieDatabaseHelper.TABLE_NAME, "ID is ?", new String[]{Long.toString(id)});
        if (isTablet) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.remove(movieFragment)
                    .commit();
        }
        cursor = db.query(MovieDatabaseHelper.TABLE_NAME,
                new String[]{MovieDatabaseHelper.KEY_ID, MovieDatabaseHelper.KEY_TITLE},
                null,
                null,
                null,
                null,
                null,
                null);
        // msgChat_list = new ArrayList<>();
        //chat_ID = new ArrayList<>();

        //remove old messages:
       // movies_ID.clear();
        movies_list.clear();

//load new messages:
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            //int ind = cursor.getColumnIndex(MovieDatabaseHelper.KEY_ID);
            //movies_ID.add(new Long(cursor.getInt(ind)));
            movies_list.add(new Movie(cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_ACTORS)),
                    cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_LENGTH)),
                    cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_RATING)),
                    cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_GENRE)),
                    cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_URL)),
                    new Long(cursor.getInt(cursor.getColumnIndex(MovieDatabaseHelper.KEY_ID)))));
            cursor.moveToNext();
        }
        moviesAdapter.notifyDataSetChanged();//reload table
    }

    public void onActivityResult(int req, int res, Intent data) {
        if (res == 10) {
            id = data.getExtras().getLong("ID");
            title=data.getExtras().getString("TITLE");
            actors=data.getExtras().getString("ACTORS");
            length=data.getExtras().getString("LENGTH");
            description=data.getExtras().getString("DESCRIPTION");
            rating=data.getExtras().getString("RATING");
            genre=data.getExtras().getString("GENRE");
            url=data.getExtras().getString("URL");
            //deleteMovie(id);

            ContentValues newData = new ContentValues();
            newData.put(MovieDatabaseHelper.KEY_TITLE, title);
            newData.put(MovieDatabaseHelper.KEY_ACTORS, actors);
            newData.put(MovieDatabaseHelper.KEY_LENGTH, length);
            newData.put(MovieDatabaseHelper.KEY_DESCRIPTION, description);
            newData.put(MovieDatabaseHelper.KEY_RATING, rating);
            newData.put(MovieDatabaseHelper.KEY_GENRE, genre);
            newData.put(MovieDatabaseHelper.KEY_URL, url);

            movies_list.add(new Movie(title, actors,  length,  description, rating,  genre,  url, (db.insert(MovieDatabaseHelper.TABLE_NAME, null, newData))));
            Toast.makeText(MoviesActivity.this, "Add movie", Toast.LENGTH_LONG);
        }
        if(res == 20) // delete a movie
        {
            long id=data.getExtras().getLong("ID");
            deleteMovie(id);
            Toast.makeText(MoviesActivity.this, "Movie Deleted", Toast.LENGTH_SHORT).show();
        }

        if(res == 30) // update a movie
        {
            updateMovie(data);
            Toast.makeText(MoviesActivity.this, "Movie is updated", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateMovie(Intent data) {
        long id=data.getExtras().getLong("ID");

        String title=data.getExtras().getString("TITLE");
        String actors=data.getExtras().getString("ACTORS");
        String length=data.getExtras().getString("LENGTH");
        String description=data.getExtras().getString("DESCRIPTION");
        String rating=data.getExtras().getString("RATING");
        String genre=data.getExtras().getString("GENRE");
        String url=data.getExtras().getString("URL");

        ContentValues newData = new ContentValues();
        newData.put(MovieDatabaseHelper.KEY_TITLE, title);
        newData.put(MovieDatabaseHelper.KEY_ACTORS, actors);
        newData.put(MovieDatabaseHelper.KEY_LENGTH, length);
        newData.put(MovieDatabaseHelper.KEY_DESCRIPTION, description);
        newData.put(MovieDatabaseHelper.KEY_RATING, rating);
        newData.put(MovieDatabaseHelper.KEY_GENRE, genre);
        newData.put(MovieDatabaseHelper.KEY_URL, url);

        db.update(MovieDatabaseHelper.TABLE_NAME, newData, "ID="+id, null);

        movies_list.clear();
        cursor.moveToFirst();
        while(!cursor.isAfterLast() ) {

            movies_list.add(new Movie(cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_TITLE)),
                    cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_ACTORS)),
                    cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_LENGTH)),
                    cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_RATING)),
                    cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_GENRE)),
                    cursor.getString(cursor.getColumnIndex(MovieDatabaseHelper.KEY_URL)),
                    new Long(cursor.getInt(cursor.getColumnIndex(MovieDatabaseHelper.KEY_ID)))));

            cursor.moveToNext();
        }
    }

    public class MovieQuery extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... args) {



            try {
                boolean isTitle, isActors, isLength, isDescription, isRating, isGenre, isUrl;

                isTitle = isActors = isLength = isDescription = isRating = isGenre = isUrl = false;

                URL               url1 = new URL("http://torunski.ca/CST2335/MovieInfo.xml");
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();

                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(conn.getInputStream(), null);
                publishProgress(25);
                int nextType = parser.next();
                while ((nextType ) != XmlPullParser.END_DOCUMENT) {
                    switch (nextType) {
                        case XmlPullParser.START_TAG: {
                            String name = parser.getName();
                            if(name==null )continue;
                            if (name.equals("Title")) {
                                isTitle = true;
                            }
                            if (name.equals("Actors")) {
                                isActors = true;
                            }
                            if (name.equals("Length")) {
                                isLength= true;
                            }
                            if (name.equals("Description")) {
                                isDescription = true;
                            }
                            if (name.equals("Rating")) {
                                isRating = true;
                            }
                            if (name.equals("Genre")) {
                                isGenre = true;
                            }
                            if (name.equals("URL")) {
                                url = parser.getAttributeValue(null, "value");
                                String fileName = url.substring(url.lastIndexOf(':') + 1);
                                if (fileExistance(fileName)) {
                                    FileInputStream fis = null;
                                    try {
                                        fis = openFileInput(fileName + ".png");
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    pic = BitmapFactory.decodeStream(fis);

                                } else {
                                    pic = getImage(url);
                                    FileOutputStream outputStream = openFileOutput(fileName + ".png", Context.MODE_PRIVATE);
                                    pic.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                    outputStream.flush();
                                    outputStream.close();
                                }
                            }
                        }

                        break;
                        case XmlPullParser.TEXT:
                            String text = parser.getText();
                            if (isTitle)
                                title = text;
                            else if (isActors)
                                actors = text;
                            else if (isLength)
                                length = text;
                            else if (isDescription)
                                description = text;
                            else if (isRating)
                                rating = text;
                            else if (isGenre)
                                genre = text;
                            break;

                        case XmlPullParser.END_TAG: {
                            String name = parser.getName();
                            if (name.equals("Movie")) {

                                ContentValues newData = new ContentValues();
                                newData.put(MovieDatabaseHelper.KEY_TITLE, title);
                                newData.put(MovieDatabaseHelper.KEY_ACTORS, actors);
                                newData.put(MovieDatabaseHelper.KEY_LENGTH, length);
                                newData.put(MovieDatabaseHelper.KEY_DESCRIPTION, description);
                                newData.put(MovieDatabaseHelper.KEY_RATING, rating);
                                newData.put(MovieDatabaseHelper.KEY_GENRE, genre);
                                newData.put(MovieDatabaseHelper.KEY_URL, url);

                                movies_list.add(new Movie(title, actors,  length,  description, rating,  genre,  url, (db.insert(MovieDatabaseHelper.TABLE_NAME, null, newData))));
                                importedFromXml=true;
                            }
                            if (name.equals("Title")) {
                                isTitle = false;
                            }
                            if (name.equals("Actors")) {
                                isActors = false;
                            }
                            if (name.equals("Length")) {
                                isLength= false;
                            }
                            if (name.equals("Description")) {
                                isDescription = false;
                            }
                            if (name.equals("Rating")) {
                                isRating = false;
                            }
                            if (name.equals("Genre")) {
                                isGenre = false;
                            }
                        }

                    }
                    publishProgress(50);
                    nextType = parser.next();
                }
                moviesAdapter.notifyDataSetChanged();

            } catch (Exception e) {
                Log.d("Exception:", e.getMessage());
            }
            publishProgress(75);
            return "Done";
        }

        public void onProgressUpdate(Integer... value) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);
        }

        public void onPostExecute(String result) {
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setMax(100); // 100 maximum value for the progress value
            progressBar.setProgress(50); // 50 default progress value for the progress bar
        }

        public Bitmap getImage(String urlString) {
            Bitmap downloaded = null;
            try {

                URL url = new URL(urlString);
                publishProgress(100);

                HttpURLConnection connection = null;

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    downloaded = BitmapFactory.decodeStream(connection.getInputStream());
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

            } catch (Exception e) {
                return null;
            }
            return downloaded;
        }


        public boolean fileExistance(String fname) {
            File file = getBaseContext().getFileStreamPath(fname + ".png");
            if (file.exists())
                Log.i("MoviesActivity", "Found the image locally");

            else
                Log.i("MoviesActivity", "Need to download the image");
            return file.exists();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
        Log.i(ACTIVITY_NAME, "In onDestroy()");
    }

}
