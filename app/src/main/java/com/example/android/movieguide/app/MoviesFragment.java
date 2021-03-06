package com.example.android.movieguide.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MoviesFragment extends Fragment {


   private MovieInfo movie;

//    private  ArrayList<MovieInfo> MovieList = new ArrayList<MovieInfo>();
//private  ArrayList<MovieInfo> MovieList;
    private MovieAdapter movieAdapter;

    private GridView gridview;



    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Add this line in order for this fragment to handle menu events.
        inflater.inflate(R.menu.moviesfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        gridview = (GridView) rootView.findViewById(R.id.gridview);
//        posterAdapter = new ImageAdapter(getActivity());
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                String anyText = (String) movieAdapter.getItem(position);
//                //String anyText = String.valueOf(posterAdapter.getItem(position));
//                Toast.makeText(getActivity(), anyText, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, anyText);
//                startActivity(intent);


                movie = (MovieInfo) movieAdapter.getItem(position);
                Toast.makeText(getActivity(), movie.getTitle(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getActivity(),DetailActivity.class);
                i.putExtra("com.example.android.movieguide.app.MovieInfo" , movie);
                startActivity(i);

            }
        });

        return rootView;
    }

    private void updateMovies(){
        FetchMoviesTask moviesTask = new FetchMoviesTask();
//           moviesTask.execute("popular");
        // new GetContacts().execute();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting = prefs.getString(getString(R.string.pref_sorting_key), getString(R.string.pref_sorting_most_popular));
        moviesTask.execute(sorting);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<MovieInfo>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private ArrayList<MovieInfo> getMoviesDataFromJson(String jsonStr)
                throws JSONException {
            // JSON Node names
            final String TAG_RESULT = "results";
            final String TAG_POSTER_PATH = "poster_path";
            final String TAG_OVERVIEW = "overview";
            final String TAG_RELEASE_DATE = "release_date";
            final String TAG_GENRE_IDS = "genre_ids";
            final String TAG_ID = "id";
            final String TAG_ORIGINAL_TITLE = "original_title";
            final String TAG_ORIGINAL_LANGUAGE ="original_language";
            final String TAG_TITLE = "title";
            final String TAG_BACKDROP_PATH = "backdrop_path";
            final String TAG_POPULARITY = "popularity";
            final String TAG_VOTE_COUNT = "vote_count";
            final String TAG_VIDEO= "video";
            final String TAG_VOTE_AVERAGE = "vote_average";


            JSONObject moviesDataJson = new JSONObject(jsonStr);
            JSONArray movies = moviesDataJson.getJSONArray(TAG_RESULT);

            // ArrayList<String> resultMovie = new ArrayList<String>();
            //ArrayList<MovieInfo> resultMovie = new ArrayList<MovieInfo>();
           ArrayList<MovieInfo> MovieList = new ArrayList<MovieInfo>();

            //MovieList = new ArrayList<>();
            for (int i = 0; i < movies.length(); i++) {

                // JSON Node names
                JSONObject c = movies.getJSONObject(i);
                String id = c.getString(TAG_ID);
                String title = c.getString(TAG_TITLE);
                String overview = c.getString(TAG_OVERVIEW);
                String poster = c.getString(TAG_POSTER_PATH);
                // poster = "http://image.tmdb.org/t/p/w185/"+poster;
                String backDrop = c.getString(TAG_BACKDROP_PATH);
                // backDrop= "http://image.tmdb.org/t/p/w500/" + backDrop;
                String releaseDate = c.getString(TAG_RELEASE_DATE);
                double voteAverage = c.getDouble(TAG_VOTE_AVERAGE);
                double popularity = c.getDouble(TAG_POPULARITY);
                String language = c.getString(TAG_ORIGINAL_LANGUAGE);
                //boolean video = c.getBoolean(TAG_VIDEO);
                double voteCount = c.getDouble(TAG_VOTE_COUNT);

                MovieInfo movieDetails = new MovieInfo(id ,title, overview, poster, backDrop, releaseDate, voteAverage, popularity, language, voteCount);
                MovieList.add(i,movieDetails);

            }


            for (MovieInfo s : MovieList) {
                Log.v(LOG_TAG, "Movies entry: " + s);

            }

            return MovieList;

        }

        @Override
        protected ArrayList<MovieInfo> doInBackground(String... params) {
            // protected String[] doInBackground(String... params){

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {


                // Construct the URL for the moviedb query
                //http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
                //http://api.themoviedb.org/3/movie/top_rated?api_key=[YOUR_API_KEY]
                //http://api.themoviedb.org/3/movie/popular?api_key=dee364a81187df2c66fa2851bb30b111

                /*** me
                 String baseUrl = "http://api.themoviedb.org/3/movie/top_rated?";
                 String apiKey = "api_key="+ BuildConfig.TheMovieDB_API_KEY;
                 URL url = new URL(baseUrl.concat(apiKey));
                 Log.v(LOG_TAG, "url is : "+ url);***/

                //abd-allah zidan
                final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie";
                final String QUERY_PARAM = "top_rated";
                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(APPID_PARAM, BuildConfig.TheMovieDB_API_KEY)
                        .build();
//.appendQueryParameter(QUERY_PARAM,params[0])
                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "http://api.themoviedb.org/3/movie/popular?api_key=dee364a81187df2c66fa2851bb30b111");
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
               // Log.v(LOG_TAG, "Movies JSON String: " + moviesJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }


        @Override
//        protected void onPostExecute(String[] result) {
        protected void onPostExecute(ArrayList<MovieInfo> result){
            if (result != null) {

                movieAdapter = new MovieAdapter(getActivity(), result);
                gridview.setAdapter(movieAdapter);

                //posterAdapter.clear();
//                for (String newMoviesStr : result) {
//                    posterAdapter.add(newMoviesStr);
//                }
            }
        }
    }


}


