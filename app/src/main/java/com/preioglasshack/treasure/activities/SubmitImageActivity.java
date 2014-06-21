package com.preioglasshack.treasure.activities;

import android.app.Activity;
 import android.content.Intent;
 import android.os.Bundle;

 import java.io.File;
 import java.io.IOException;

 import org.apache.http.HttpEntity;
 import org.apache.http.HttpResponse;
 import org.apache.http.auth.AuthScope;
 import org.apache.http.auth.UsernamePasswordCredentials;
 import org.apache.http.client.ClientProtocolException;
 import org.apache.http.client.methods.HttpPost;
 import org.apache.http.client.methods.HttpPut;
 import org.apache.http.conn.ConnectTimeoutException;
 import org.apache.http.entity.mime.HttpMultipartMode;
 import org.apache.http.entity.mime.MultipartEntityBuilder;
 import org.apache.http.impl.client.DefaultHttpClient;
 import org.apache.http.params.BasicHttpParams;
 import org.apache.http.params.HttpConnectionParams;
 import org.apache.http.params.HttpParams;
 import org.apache.http.util.EntityUtils;
 import org.json.JSONException;
 import org.json.JSONObject;

 import android.app.Activity;
 import android.app.ProgressDialog;
 import android.content.Context;
 import android.content.Intent;
 import android.media.AudioManager;
 import android.os.AsyncTask;
 import android.os.Bundle;
 import android.os.FileObserver;
 import android.os.Message;
 import android.provider.MediaStore;
 import android.util.Log;

 import com.google.android.glass.media.CameraManager;
 import com.google.android.glass.media.Sounds;
 import com.preioglasshack.treasure.ui.MessageDialog;

public class SubmitImageActivity extends Activity {

     private static final String TAG = SubmitImageActivity.class.getSimpleName();

     private MessageDialog waitdialog;

     private String picture;
     private String thumbnail;
     private final int TAKE_PHOTO=1;
     private final static String REQUESTURL="http://api.moodstocks.com/v2/ref/";
     private final static String REQUESTSERVER="api.moodstocks.com";

     @Override
     protected void onCreate(Bundle bundle) {
         super.onCreate(bundle);
         Log.i(TAG,"onCreate()");
         Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
         startActivityForResult(intent, TAKE_PHOTO);
     }

     @Override
     protected void onResume() {
         super.onResume();
     }

     @Override
     protected void onPause() {
         super.onPause();
     }

     // picture taking handling for startActivityWithResult
     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         Log.i(TAG, "onActivityResult: " + requestCode + " : " + resultCode + " data=" + data);
         if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
             String picturepath = data.getStringExtra(CameraManager.EXTRA_PICTURE_FILE_PATH);
             String thumbnailpath = data.getStringExtra(CameraManager.EXTRA_THUMBNAIL_FILE_PATH);


             Log.i(TAG, "found accident photo path:" + picturepath);

             waitdialog = new MessageDialog.Builder(this)
                     .setMessage("Picture preparation...")
                     .setSecondaryMessage("pending")
                     .setDismissable(false)
                     .setAutoHide(false)
                     .build();

             waitdialog.show();

             processPictureWhenReady(picturepath);
             picture=picturepath;
             thumbnail=thumbnailpath;
             Log.i(TAG, "-----> saved picture " + picture);

         }

         super.onActivityResult(requestCode, resultCode, data);
         // /////////////////// finish(); // ////??????
     }

     private void processPictureWhenReady(final String picturePath) {
         final File pictureFile = new File(picturePath);

         if (pictureFile.exists()) {
             Log.i(TAG, "-----> File exists!");


             waitdialog.dismiss();
             // ###################### handle picture
             submitImage();

         } else {
             Log.i(TAG, "-----> File DOESNT exists!");
             // The file does not exist yet. Before starting the file observer, you
             // can update your UI to let the user know that the application is
             // waiting for the picture (for example, by displaying the thumbnail
             // image and a progress indicator).

             final File parentDirectory = pictureFile.getParentFile();
             FileObserver observer = new FileObserver(parentDirectory.getPath()) {
                 /// NEW      FileObserver observer = new FileObserver(parentDirectory.getPath(), FileObserver.CLOSE_WRITE | FileObserver.MOVED_TO) {
                 // Protect against additional pending events after CLOSE_WRITE is
                 // handled.
                 private boolean isFileWritten;

                 @Override
                 public void onEvent(int event, String path) {
                     Log.d(TAG, "FileObserver onEvent()");

                     if (!isFileWritten) {
                         Log.d(TAG, "NOT isFileWritten");
                         // For safety, make sure that the file that was created in
                         // the directory is actually the one that we're expecting.
                         File affectedFile = new File(parentDirectory, path);
                         // OLD            isFileWritten = (event == FileObserver.CLOSE_WRITE && affectedFile.equals(pictureFile));
                         isFileWritten = affectedFile.equals(pictureFile);


                         if (isFileWritten) {
                             Log.d(TAG, "isFileWritten");
                             stopWatching();

                             // Now that the file is ready, recursively call
                             // processPictureWhenReady again (on the UI thread).
                             runOnUiThread(new Runnable() {
                                 @Override
                                 public void run() {
                                     Log.d(TAG, "run() in UIThread");
                                     processPictureWhenReady(picturePath);
                                 }
                             });
                         }
                     }
                 }
             };
             Log.d(TAG, "observer.startWatching()");

             observer.startWatching();
         }
     }




     // submit report (photos, constat, date, gps) and returns confirmation number
     public static String sendToBackend(String pic)
             throws ConnectTimeoutException, ClientProtocolException, IOException {
         Log.i(TAG, "submitting picture ");
         String result = null;


         // set short connection timeout to avoid long pause on Glass
         HttpParams httpParameters = new BasicHttpParams();
         HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
         HttpConnectionParams.setSoTimeout(httpParameters, 5000);
         DefaultHttpClient client = new DefaultHttpClient(httpParameters);

         client.getCredentialsProvider().setCredentials(
                 new AuthScope(REQUESTSERVER, 80),
                 new UsernamePasswordCredentials(IdentifyImageActivity.API_KEY,
                         IdentifyImageActivity.API_SECRET));

         String name=pic.substring(pic.lastIndexOf("/")+1, pic.lastIndexOf("."));
         Log.i(TAG, "saving id="+name);
         HttpPut put = new HttpPut(REQUESTURL+name);
         ///    HttpPost post = new HttpPost(BACKENDURL);


         MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
         entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
         entityBuilder.addBinaryBody("image_file", new File(pic));

         HttpEntity entity = entityBuilder.build();
         put.setEntity(entity);

         Log.d(TAG, "performing HTTP PUT request to :" + REQUESTURL+name);
         HttpResponse response = client.execute(put);
         Log.d(TAG, "completed HTTP PUT request - response :" + response.getStatusLine());

         HttpEntity httpEntity = response.getEntity();
         result = EntityUtils.toString(httpEntity);
         Log.d(TAG, "completed HTTP PUT request - result :" + result);


         HttpPost post=new HttpPost(REQUESTURL+name+"/offline");

         Log.d(TAG, "performing HTTP PUT request to :" + REQUESTURL+name+"/offline");
         response = client.execute(post);
         Log.d(TAG, "completed HTTP POST request - response :" + response.getStatusLine());

         httpEntity = response.getEntity();
         result = EntityUtils.toString(httpEntity);
         Log.d(TAG, "completed HTTP POST request - result :" + result);

         return result;
     }


     private void submitImage() {

         new AsyncTask<String, Void, String> (){

             private final String ERROR_MESSAGE = "[ERROR]";

             @Override
             protected String doInBackground(String... params) {
                 String result=null;
                 try {
                     result=sendToBackend(thumbnail); // ##### picture
                 } catch (ConnectTimeoutException cte) {
                     Log.w(TAG+"+Async", "---> ConnectTimeoutException "+cte);
                     result=ERROR_MESSAGE;
                 } catch (ClientProtocolException cpe) {
                     Log.w(TAG+"+Async", "---> ClientProtocolException "+cpe);
                     result=ERROR_MESSAGE;
                 } catch (IOException ioe) {
                     Log.w(TAG+"+Async", "---> IOException "+ioe);
                     result=ERROR_MESSAGE;
                 }

                 return result;
             }

             @Override
             protected void onPreExecute(){
                 Log.i(TAG+"+Async", "onPreExecute Called");
                 waitdialog = new MessageDialog.Builder(SubmitImageActivity.this)
                         .setMessage("Image submission")
                         .setSecondaryMessage("pending")
                         .setDismissable(false)
                         .setAutoHide(false)
                         .setKeepScreenOn(true)
                         .build();

                 waitdialog.show();
             }

             @Override
             protected void onPostExecute(String result){
                 Log.i(TAG+"+Async", "onPostExecute Called - result="+result);
                 Log.i(TAG+"+Async", "dismiss dialog");
                 waitdialog.dismiss();

                 AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                 if (ERROR_MESSAGE.equals(result)) {
                     am.playSoundEffect(Sounds.ERROR);
                 } else {
                     am.playSoundEffect(Sounds.SUCCESS);

                     try {
                         JSONObject json = new JSONObject(result);

                         Intent intent = new Intent(SubmitImageActivity.this, RecordActivity.class);

                         intent.putExtra(PlaylistActivity.EXTRA_BOOK_ID, json.getString("id"));

                         startActivity(intent);
                     } catch (JSONException e) {
                         e.printStackTrace();
                         am.playSoundEffect(Sounds.ERROR);
                     }
                 }

                 finish();
             }
         }.execute();
     }
 }