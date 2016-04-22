//package com.example.android.movieguide.app;
//
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//
//public class FetchTrailersTask extends AsyncTask<String, Void, ArrayList<ExtraBase>> {
//
//    private CommonAdapter cAdapter;
//
//    private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();
//
//    private ArrayList<ExtraBase> getTrailersDataFromJson(String jsonStr)
//            throws JSONException {
//        // JSON Node names
//        final String TAG_MOVIE_NUMBER= "id";
//        final String TAG_RESULT = "results";
//        final String TAG_TRAILER_ID = "id";
//        final String TAG_TRAILER_KEY= "key";
//        final String TAG_TRAILER_NAME= "name";
//        final String TAG_TRAILER_SITE= "site";
//        final String TAG_TRAILER_SIZE= "size";
//        final String TAG_TRAILER_TYPE= "type";
//
//
//        JSONObject trailersDataJson = new JSONObject(jsonStr);
//        //json object movie number know as movie id
//        String movieID= trailersDataJson.getString(TAG_MOVIE_NUMBER);
//        //  String movieNumber = movieID.getString(TAG_MOVIE_NUMBER);
//
//
//        //json array results
//        JSONArray trailers = trailersDataJson.getJSONArray(TAG_RESULT);
//
//
//        ArrayList<ExtraBase> TrailersList = new ArrayList<>();
//
//        //MovieList = new ArrayList<>();
//        for (int i = 0; i < trailers.length(); i++) {
//
//            // JSON Node names
//            JSONObject c = trailers.getJSONObject(i);
//            String trailerID = c.getString(TAG_TRAILER_ID);
//            String key = c.getString(TAG_TRAILER_KEY);
//            String name = c.getString(TAG_TRAILER_NAME);
//            String site = c.getString(TAG_TRAILER_SITE);
//            String size= c.getString(TAG_TRAILER_SIZE);
//            String type = c.getString(TAG_TRAILER_TYPE);
//
////            MovieInfo movieTrailer = new MovieInfo();
////            movieTrailer.setMovieID(movieID);
////            movieTrailer.setMovieTrailerKey(key);
//            ExtraBase movieTrailer = new ExtraBase(movieID, trailerID , key , name , site , size , type);
//
//
//            TrailersList.add(i,movieTrailer);
//
//        }
//
//
//        for (ExtraBase s : TrailersList) {
//            Log.v(LOG_TAG, "Trailers entry: " + s);
//
//        }
//
//        return TrailersList;
//
//    }
//
//    @Override
//    protected ArrayList<ExtraBase> doInBackground(String... params) {
//        // protected String[] doInBackground(String... params){
//
//        // If there's no zip code, there's nothing to look up.  Verify size of params.
//        if (params.length == 0) {
//            return null;
//        }
//        // These two need to be declared outside the try/catch
//        // so that they can be closed in the finally block.
//        HttpURLConnection urlConnection = null;
//        BufferedReader reader = null;
//
//        // Will contain the raw JSON response as a string.
//        String moviesJsonStr = null;
//
//
//        try {
//
//
//            // Construct the URL for the moviedb query
//
//            //http://api.themoviedb.org/3/movie/140607/videos?api_key=00000000000000000000000
//
//
//            final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie";
//
//            final String APPID_PARAM = "api_key";
//            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
//                    .appendPath(params[0])
//                    .appendPath("videos")
//                    .appendQueryParameter(APPID_PARAM, BuildConfig.TheMovieDB_API_KEY)
//                    .build();
//
//            URL url = new URL(builtUri.toString());
//            Log.v(LOG_TAG, "http://api.themoviedb.org/3/movie/209112/videos?api_key=dee364a81187df2c66fa2851bb30b111");
//            Log.v(LOG_TAG, "Built URI " + builtUri.toString());
//
//            // Create the request to OpenWeatherMap, and open the connection
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");
//            urlConnection.connect();
//
//            // Read the input stream into a String
//            InputStream inputStream = urlConnection.getInputStream();
//            StringBuffer buffer = new StringBuffer();
//            if (inputStream == null) {
//                // Nothing to do.
//                return null;
//            }
//            reader = new BufferedReader(new InputStreamReader(inputStream));
//            String line;
//            while ((line = reader.readLine()) != null) {
//
//                buffer.append(line + "\n");
//            }
//
//            if (buffer.length() == 0) {
//                // Stream was empty.  No point in parsing.
//                return null;
//            }
//            moviesJsonStr = buffer.toString();
//            // Log.v(LOG_TAG, "Movies JSON String: " + moviesJsonStr);
//
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "Error ", e);
//
//            return null;
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (final IOException e) {
//                    Log.e(LOG_TAG, "Error closing stream", e);
//                }
//            }
//        }
//
//        try {
//            return getTrailersDataFromJson(moviesJsonStr);
//        } catch (JSONException e) {
//            Log.e(LOG_TAG, e.getMessage(), e);
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//    @Override
////        protected void onPostExecute(String[] result) {
//    protected void onPostExecute(ArrayList<ExtraBase> result){
//        if (result != null) {
//            if (result != null) {
//                cAdapter.clear();
//                for (ExtraBase s : result) {
//                    cAdapter.add(s); }
//            }
//        }
//    }
//}