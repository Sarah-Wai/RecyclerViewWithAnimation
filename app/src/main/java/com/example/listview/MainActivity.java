package com.example.listview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener {
    private RecyclerView recyclerView = null;
    private Adapter adapter = null;
    private ArrayList<Model> models;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildResources();


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new Adapter(models, MainActivity.this);
        recyclerView.setAdapter(adapter);
        setupRecyclerViewAnimator();


        FloatingActionButton floatingActionButton = findViewById(R.id.btnAdd);
        if (floatingActionButton != null) {
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddItemDialog(MainActivity.this,v);
                }
            });
        }
    }

    private void showAddItemDialog(Context context,View v) {
        final EditText taskEditText = new EditText(context);
        taskEditText.setHint("    e.g. https://google.com");
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Add Website Link")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String urlStr = String.valueOf(taskEditText.getText());
                        getWebAsync(urlStr,0);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void buildResources() {
        if (models == null) {
            models = new ArrayList<>();
            Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.ic_user);

            Model model = new Model();
            model.setTitle("CNA");
            model.setURL("https://www.channelnewsasia.com");
            model.setImage(icon);

            Model model1 = new Model();
            model1.setURL("https://sg.yahoo.com");
            model1.setTitle("Yahoo");
            model1.setImage(icon);

            Model model2 = new Model();
            model2.setURL("https://www.google.com");
            model2.setTitle("Google");
            model2.setImage(icon);


            models.add(model);
            models.add(model1);
            models.add(model2);
        }
    }

    @Override
    public void onClick(View v) {

    }

    private void updateList() {

        try {
            for (int i = 0; i < models.size(); i++) {
                if (models.get(i).isSelect()) {
                    //temp.add(models.get(i));
                    adapter.removeItem(i);
                }
            }

        } catch (Exception e) {

        }
//        models = temp;
//        if (models.size() == 0) {
//            recyclerView.setVisibility(View.GONE);
//        }
//        adapter.setModel(models);
//        adapter.notifyItemRangeChanged(0, models.size());
        //adapter.notifyDataSetChanged();
    }

    private void setupRecyclerViewAnimator() {
        recyclerView.setItemAnimator(new MyItemAnimator());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort:
                adapter.sortData();
                Toast.makeText(this.getApplicationContext(), "Sorted", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_delete:
                  updateList();
                Toast.makeText(this.getApplicationContext(), "Delete", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //TO Get Image and Title
    static String  TAG = "WebRequest";
    void getWebAsync(String urlString,int itemAdapterPosition) {

        AsyncHttpClient androidClient = new AsyncHttpClient();

        androidClient.get(urlString, new TextHttpResponseHandler() {


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "responseString: " + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseToken) {
                Log.e(TAG, "Client token: " + responseToken);
                Model newModel=handleHtml(urlString,responseToken);
                adapter.addItem(newModel);
            }
        });
    }

    Model handleHtml(String urlString,String htmlCode) {
        Model model = new Model();
        WebModel webModel = extractAllText(htmlCode);

        Log.e("PARSED_MODEL", "Model: " + webModel.toString());

        if (webModel.getTilte() != null) {
            model.setTitle(webModel.getTilte());
            model.setURL(urlString);

        }
        Bitmap bitmap = null;
        if (webModel.getMainIamgeURL() != null) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {
                URL url = new URL(webModel.getMainIamgeURL());
                 bitmap=BitmapFactory.decodeStream((InputStream)url.getContent());
            } catch (IOException e) {
                //Log.e(TAG, e.getMessage());
            }
            model.setImage(bitmap);

        }
        return model;
    }

    public WebModel extractAllText(String htmlText){
        Source source = new Source(htmlText);
        String strData = "";

        WebModel webModel = new WebModel();

        List<Element> elements;
        elements = source.getAllElements("title");

        String title = elements.get(0).getContent().toString();
        webModel.setTilte(title);

        elements = source.getAllElements("meta");
        for(Element element : elements )
        {
            final String id = element.getAttributeValue("property"); // Get Attribute 'id'
            if( id != null && id.equals("og:image")){
                strData = element.getAttributeValue("content");
                webModel.setMainIamgeURL(strData);
                break;
            }
        }
        return webModel;
    }

    public class WebModel {
        String tilte;
        String mainIamgeURL;

        public String getTilte() {
            return tilte;
        }

        public String getMainIamgeURL() {
            return mainIamgeURL;
        }

        public void setTilte(String tilte) {
            this.tilte = tilte;
        }

        public void setMainIamgeURL(String mainIamgeURL) {
            this.mainIamgeURL = mainIamgeURL;
        }
    }

}
