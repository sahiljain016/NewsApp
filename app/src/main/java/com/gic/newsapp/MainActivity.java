package com.gic.newsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
ListView NewsListView;
    ArrayAdapter ListViewAdapter;
    String articleUrl;
    String articleTitle;
    String articleHTML;
    HttpURLConnection urlConnection;
    InputStream inputStream;
    String urlss;
    URL url;
    int data;
    InputStreamReader inputStreamReader;
    SQLiteDatabase articlesDB;
    ArrayList<String> newsTitles = new ArrayList<String>();
    ArrayList<String> newsUrl = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        articlesDB = this.openOrCreateDatabase("Articles",MODE_PRIVATE,null);

            articlesDB.execSQL("CREATE TABLE IF NOT EXISTS articles(title VARCHAR, url VARCHAR)");

        final DownloadTask downloadTask = new DownloadTask();
        try{
            downloadTask.execute("http://newsapi.org/v2/top-headlines?country=in&apiKey=2902fe41cc6045568c940516302f1fba");
        }catch (Exception e){

            e.printStackTrace();
        }


      //

      //  try{
     //
      //  }catch (Exception e){
      //      e.printStackTrace();
      //  }
NewsListView = (ListView) findViewById(R.id.NewsListView);
ListViewAdapter = new ArrayAdapter(this,R.layout.listview_ct, newsTitles);
NewsListView.setAdapter(ListViewAdapter);

NewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


        urlss = newsUrl.get(i);
        Log.i("urls", urlss);

        final DownloadTask1 downloadTask1 = new DownloadTask1();

        try {
            downloadTask1.execute(urlss);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
});

updateListView();
    }
public void getWebsite()
{

}
    public void updateListView(){

        Cursor c = articlesDB.rawQuery("SELECT * FROM articles",null);

        int TitleIndex = c.getColumnIndex("title");
        int UrlIndex = c.getColumnIndex("url");

        if(c.moveToFirst()){
            newsTitles.clear();
            newsUrl.clear();

            do{
                newsTitles.add(c.getString(TitleIndex));
                newsUrl.add(c.getString(UrlIndex));
            }while(c.moveToNext());
        }

    ListViewAdapter.notifyDataSetChanged();
    }
public class DownloadTask1 extends AsyncTask<String, Void, String>{


    @Override
       public String doInBackground(String... urlsss) {
        try {
           URL url1 = new URL(urlsss[0]);
          HttpURLConnection  urlConnection1 = (HttpURLConnection) url1.openConnection();
         InputStream   inputStream1 = urlConnection1.getInputStream();
          InputStreamReader  inputStreamReader1 = new InputStreamReader(inputStream1);
         int  data1 = inputStreamReader1.read();
            articleHTML = "";

            while (data1 != -1) {
                char current1 = (char) data1;
                articleHTML += current1;
                data1 = inputStreamReader1.read();
            }
            Log.i("HTMLS",articleHTML);
            Intent intent = new Intent(getApplicationContext(), NewsWebView.class);
            intent.putExtra("articleHTMl", articleHTML);
            startActivity(intent);
            return articleHTML;
        }catch(Exception e){

            e.printStackTrace();
        }
        return null;
    }
}


  public class DownloadTask extends AsyncTask<String, Void, String>{



      @Override
       protected String doInBackground(String... urls) {

            String result="";

             urlConnection = null;

            try{

                url= new URL(urls[0]);

                urlConnection=(HttpURLConnection) url.openConnection();

               inputStream = urlConnection.getInputStream();

                 inputStreamReader = new InputStreamReader(inputStream);
                 data = inputStreamReader.read();

                while(data != -1){

                    char current = (char) data;
                    result +=current;
                    data= inputStreamReader.read();
                }
                Log.i("ggd", result);


    int NoofArticles = 5;
   JSONObject jsonObject = new JSONObject(result);
    String articles = jsonObject.getString("articles");
    Log.i("trst", articles);
JSONArray jsonArray = new JSONArray(articles);

articlesDB.execSQL("DELETE FROM articles");
    for(int i=0; i < NoofArticles;i++){
        JSONObject jsonPart = jsonArray.getJSONObject(i);
        articleTitle = jsonPart.getString("title");
        Log.i("titles", articleTitle);
        articleUrl = jsonPart.getString("url");
        Log.i("HTML",articleUrl);
        String sql = "INSERT INTO articles (title, url) VALUES (?,?)";
        SQLiteStatement statement = articlesDB.compileStatement(sql);
        statement.bindString(1,articleTitle);
        statement.bindString(2,articleUrl);

        statement.execute();

    }






            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

      @Override
      protected void onPostExecute(String s) {
          super.onPostExecute(s);

          updateListView();
      }
  }
}