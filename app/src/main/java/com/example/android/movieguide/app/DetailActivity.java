package com.example.android.movieguide.app;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieguide.app.data.MoviesDBHelper;
import com.squareup.picasso.Picasso;

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

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        private MovieInfo m;
        private DetailsAdapter dAdapter;
        private ArrayList<Reviews> rev = new ArrayList<>();
        private ListView listview;


        private CommonAdapter cAdapter;


        private static final String SHARE_HASHTAG = " #MovieguideApp";


        public DetailFragment() {
        }

        public void insertFavoriteMovies() {

            MoviesDBHelper helper = new MoviesDBHelper(getActivity());
            helper.getWritableDatabase();
            if (helper.addFAVORITEMOVIES(m) != -1) {
                Toast.makeText(getActivity(), "successfully added", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


            Intent i = getActivity().getIntent();
            m = (MovieInfo) i.getParcelableExtra("com.example.android.movieguide.app.MovieInfo");

            String number = m.getMovieId();
            String title = m.getTitle();
            String posterStr = m.getPosterPath();
            String backdropStr = m.getBackdropPath();
            String detailPosterStr = m.getDetailPoster();
            String overview = m.getOverview();
            String rd = m.getReleaseDate();
            //String rd = i.getStringExtra("releaseDate");
            //Toast.makeText(getActivity(), "rd = " + rd, Toast.LENGTH_LONG).show();

            double popularity = m.getPopularity();

            ImageView backDrop = (ImageView) rootView.findViewById(R.id.backdrop_image_view);
            Picasso.with(getActivity()).load(backdropStr).into(backDrop);

            ImageView posterImage = (ImageView) rootView.findViewById(R.id.poster_image_view);
            Picasso.with(getActivity()).load(detailPosterStr).into(posterImage);

            ((TextView) rootView.findViewById(R.id.title_text_view)).setText(title);
            ((TextView) rootView.findViewById(R.id.overview_text_view)).setText(overview);

            TextView rdText = (TextView) rootView.findViewById(R.id.releaseDate_text_view);
            rdText.setText(rd);



            //((TextView) rootView.findViewById(R.id.releaseDate_text_view)).setText(rd);
//            ((TextView) rootView.findViewById(R.id.voteAverage_text_View)).setText(String.valueOf(vote));
//            TextView t = (TextView) rootView.findViewById(R.id.title_text);
//            t.setText(title);

            FetchTrailersTask trailerTask = new FetchTrailersTask();
            trailerTask.execute(number);

            FetchReviewsTask reviewsTask = new FetchReviewsTask();
            reviewsTask.execute(number);

            ArrayList<ExtraBase> t = new ArrayList<>();
            ListView tList = (ListView) rootView.findViewById(R.id.trailers_listview);
            cAdapter = new CommonAdapter(getActivity(), t);
            tList.setAdapter(cAdapter);
            tList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    ExtraBase b = cAdapter.getItem(position);
                    Uri uri = Uri.parse(b.getKey());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });

//            ArrayList<Reviews> arrayOfUsers = new ArrayList<>();
//            DetailsAdapter adapter = new DetailsAdapter(getActivity(), arrayOfUsers);
//            ListView listView = (ListView) rootView.findViewById(R.id.reviews_listview);
//            listView.setAdapter(adapter);
            listview = (ListView) rootView.findViewById(R.id.reviews_listview);
            dAdapter = new DetailsAdapter(getActivity(), rev);
            listview.setAdapter(dAdapter);

            final Button addFAV = (Button) rootView.findViewById(R.id.fav_button);
            addFAV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    insertFavoriteMovies();
                    //addFAV.setBackground(movies);
                }
            });

            return rootView;
        }


        public class FetchReviewsTask extends AsyncTask<String, Void, ArrayList<Reviews>> {

            private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();


            private ArrayList<Reviews> getReviewsDataFromJson(String jsonStr)
                    throws JSONException {
                // JSON Node names
                final String TAG_MOVIE_NUMBER = "id";
                final String TAG_RESULT = "results";
                final String TAG_REVIEW_ID = "id";
                final String TAG_REVIEW_AUTHOR = "author";
                final String TAG_REVIEW_CONTENT = "content";
                final String TAG_REVIEW_URL = "url";


                JSONObject reviewsDataJson = new JSONObject(jsonStr);
                //json object movie number know as movie id
                //JSONObject movieID= reviewsDataJson.getJSONObject(TAG_MOVIE_NUMBER);
                String movieNumber = reviewsDataJson.getString(TAG_MOVIE_NUMBER);


                //json array results
                JSONArray trailers = reviewsDataJson.getJSONArray(TAG_RESULT);


                // ArrayList<MovieInfo> ReviewsList = new ArrayList<MovieInfo>();

                ArrayList<Reviews> ReviewsList = new ArrayList<>();

                for (int i = 0; i < trailers.length(); i++) {

                    // JSON Node names
                    JSONObject c = trailers.getJSONObject(i);
                    String reviewID = c.getString(TAG_REVIEW_ID);
                    String author = c.getString(TAG_REVIEW_AUTHOR);
                    String content = c.getString(TAG_REVIEW_CONTENT);
                    String url = c.getString(TAG_REVIEW_URL);

//                    MovieInfo movieReview = new MovieInfo();
//                    movieReview.setMovieID(movieNumber);
//                    movieReview.setMovieReviewURL(url);

                    Reviews movieReview = new Reviews(movieNumber, reviewID, author, content, url);

                    ReviewsList.add(i, movieReview);

                }


                for (Reviews s : ReviewsList) {
                    Log.v(LOG_TAG, "Reviews entry: " + s);

                }

                return ReviewsList;

            }

            @Override
            protected ArrayList<Reviews> doInBackground(String... params) {

                if (params.length == 0) {
                    return null;
                }

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String moviesJsonStr = null;
//                String number = "209112";
//                String mode = "reviews";

                try {


                    // Construct the URL for the moviedb query
                    //http://api.themoviedb.org/3/movie/209112/reviews?api_key=00000000000000000000000


                    final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie";

                    final String APPID_PARAM = "api_key";
                    Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                            .appendPath(params[0])
                            .appendPath("reviews")
                            .appendQueryParameter(APPID_PARAM, BuildConfig.TheMovieDB_API_KEY)
                            .build();

                    URL url = new URL(builtUri.toString());
                    Log.v(LOG_TAG, "http://api.themoviedb.org/3/movie/209112/videos?api_key=dee364a81187df2c66fa2851bb30b111");
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
                    return getReviewsDataFromJson(moviesJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                return null;
            }


            @Override
            protected void onPostExecute(ArrayList<Reviews> result) {
                if (result != null) {
                    if (result != null) {
                        dAdapter.clear();
                        for (Reviews s : result) {
                            dAdapter.add(s);
                        }
                    }
                }
            }


        }

        public class FetchTrailersTask extends AsyncTask<String, Void, ArrayList<ExtraBase>> {

            //private CommonAdapter cAdapter;

            private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

            private ArrayList<ExtraBase> getTrailersDataFromJson(String jsonStr)
                    throws JSONException {
                // JSON Node names
                final String TAG_MOVIE_NUMBER = "id";
                final String TAG_RESULT = "results";
                final String TAG_TRAILER_ID = "id";
                final String TAG_TRAILER_KEY = "key";
                final String TAG_TRAILER_NAME = "name";
                final String TAG_TRAILER_SITE = "site";
                final String TAG_TRAILER_SIZE = "size";
                final String TAG_TRAILER_TYPE = "type";


                JSONObject trailersDataJson = new JSONObject(jsonStr);
                //json object movie number know as movie id
                String movieID = trailersDataJson.getString(TAG_MOVIE_NUMBER);
                //  String movieNumber = movieID.getString(TAG_MOVIE_NUMBER);


                //json array results
                JSONArray trailers = trailersDataJson.getJSONArray(TAG_RESULT);


                ArrayList<ExtraBase> TrailersList = new ArrayList<>();

                //MovieList = new ArrayList<>();
                for (int i = 0; i < trailers.length(); i++) {

                    // JSON Node names
                    JSONObject c = trailers.getJSONObject(i);
                    String trailerID = c.getString(TAG_TRAILER_ID);
                    String key = c.getString(TAG_TRAILER_KEY);
                    String name = c.getString(TAG_TRAILER_NAME);
                    String site = c.getString(TAG_TRAILER_SITE);
                    String size = c.getString(TAG_TRAILER_SIZE);
                    String type = c.getString(TAG_TRAILER_TYPE);

//            MovieInfo movieTrailer = new MovieInfo();
//            movieTrailer.setMovieID(movieID);
//            movieTrailer.setMovieTrailerKey(key);
                    ExtraBase movieTrailer = new ExtraBase(movieID, trailerID, key, name, site, size, type);


                    TrailersList.add(i, movieTrailer);

                }


                for (ExtraBase s : TrailersList) {
                    Log.v(LOG_TAG, "Trailers entry: " + s);

                }

                return TrailersList;

            }

            @Override
            protected ArrayList<ExtraBase> doInBackground(String... params) {
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

                    //http://api.themoviedb.org/3/movie/140607/videos?api_key=00000000000000000000000


                    final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie";

                    final String APPID_PARAM = "api_key";
                    Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                            .appendPath(params[0])
                            .appendPath("videos")
                            .appendQueryParameter(APPID_PARAM, BuildConfig.TheMovieDB_API_KEY)
                            .build();

                    URL url = new URL(builtUri.toString());
                    Log.v(LOG_TAG, "http://api.themoviedb.org/3/movie/209112/videos?api_key=dee364a81187df2c66fa2851bb30b111");
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
                    return getTrailersDataFromJson(moviesJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                return null;
            }


            @Override
//        protected void onPostExecute(String[] result) {
            protected void onPostExecute(ArrayList<ExtraBase> result) {
                if (result != null) {
                    if (result != null) {
                        cAdapter.clear();
                        for (ExtraBase s : result) {
                            cAdapter.add(s);
                        }
                    }
                }
            }
        }
    }
}