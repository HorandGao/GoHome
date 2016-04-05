package pw.horand.gohome;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static pw.horand.gohome.LoginActivity.InputStreamTOString;

public class myTrainList extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView src;
    private TextView des;
    private TextView date;

    private ListView lv_order;
    private List<Map<String,Object>> list_order;
    private orderAsyncTask orderTask = null;
    String str_date;
    private View mProgressView;
    private View mtrainListFormView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_train_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        src = (TextView) findViewById(R.id.src);
        des = (TextView) findViewById(R.id.des);
        date = (TextView) findViewById(R.id.date);


        Intent intent = getIntent();

        String str_src = intent.getStringExtra("str_src");
        String str_des = intent.getStringExtra("str_des");
        str_date = intent.getStringExtra("str_date");
        src.setText(str_src);
        des.setText(str_des);
        date.setText(str_date);


        mtrainListFormView = findViewById(R.id.trainList_form);
        mProgressView = findViewById(R.id.trainList_progress);

        globalData userInfo = (globalData)getApplication();
        ((TextView) findViewById(R.id.login_mail)).setText(userInfo.getEmail().toString());

        lv_order = (ListView)findViewById(R.id.lv_trainList);

        lv_order.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(myTrainList.this, myBooking.class);

                intent.putExtra("date",str_date);
                intent.putExtra("trainNum", list_order.get(position).get("trainNum").toString());
                intent.putExtra("seatType1", list_order.get(position).get("seatType1").toString());
                intent.putExtra("train_src", list_order.get(position).get("train_src").toString());
                intent.putExtra("train_des", list_order.get(position).get("train_des").toString());
                intent.putExtra("seatType2", list_order.get(position).get("seatType2").toString());
                intent.putExtra("train_goTime", list_order.get(position).get("train_goTime").toString());
                intent.putExtra("train_doneTime", list_order.get(position).get("train_doneTime").toString());
                intent.putExtra("seatType3", list_order.get(position).get("seatType3").toString());
                intent.putExtra("seatPrice1", list_order.get(position).get("seatPrice1").toString());
                intent.putExtra("seatPrice2", list_order.get(position).get("seatPrice2").toString());
                intent.putExtra("seatPrice3", list_order.get(position).get("seatPrice3").toString());

                intent.putExtra("leftTicket1", list_order.get(position).get("leftTicket1").toString());
                intent.putExtra("leftTicket2", list_order.get(position).get("leftTicket2").toString());
                intent.putExtra("leftTicket3", list_order.get(position).get("leftTicket3").toString());

                startActivity(intent);

            }

        });

        showProgress(true);
        String urlPath = null;
        try {
            urlPath = userInfo.apiURL + "/getTrainList.action?src=" + URLEncoder.encode(str_src, "UTF-8") + "&des=" + URLEncoder.encode(str_des, "UTF-8") + "&date=" + str_date;

            orderTask = new orderAsyncTask(urlPath);
            orderTask.execute((Void) null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_train_list, menu);
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

            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            Intent intent = new Intent(this,myOrder.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this,waitPayMoney.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(this,myQueue.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(this,myInformation.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mtrainListFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mtrainListFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mtrainListFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mtrainListFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public String httpGetOrder(String path){
        try {
            //String path = "http://10.202.73.41:8080/getOrder.action";
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                String test = InputStreamTOString(is, "UTF-8");
                //JSONObject json_Order = new JSONObject(test);
                return test;
            }
        }catch (Exception e){

        }
        return null;
    }
    public List<Map<String,Object>> jsonToListMap(JSONArray ls) throws JSONException {

        List<Map<String, Object>> item_temp = new ArrayList<Map<String, Object>>();
        if (ls.length()>0) {
            for (int i=0;i<ls.length();i++) {
                JSONObject jsonObject2 = ls.getJSONObject(i);
                Map<String ,Object> map = new HashMap<String, Object>();
                Iterator<String> iterator = jsonObject2.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    Object value = jsonObject2.get(key);
                    map.put(key, value);
                }
                item_temp.add(map);
            }
        }

        return item_temp;
    }
    private class orderAsyncTask extends AsyncTask<Void ,Integer,Boolean> {

        private String pathUrl;
        orderAsyncTask(String path){
            pathUrl = path;
        }
        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final String[] str_order = new String[1];
            str_order[0] =  httpGetOrder(pathUrl);
            try{
                JSONArray json_order = new JSONArray(str_order[0]);
                list_order = jsonToListMap(json_order);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }
        @Override
        protected void onProgressUpdate(Integer... values){

        }
        @Override
        protected void onPostExecute(Boolean result){
//            SimpleAdapter orderAdapter;
//            orderAdapter = new SimpleAdapter(waitPayMoney.this,list_order,R.layout.order_item,
//                    new String[]{"srcStation","desStation","srcDate","trainNum","orderType"},
//                    new int[]{R.id.srcStation,R.id.desStation,R.id.srcDate,R.id.trainNum,R.id.orderType});
            showProgress(false);
            if(list_order.size()==0){
                TextView noneData = new TextView(myTrainList.this);
                noneData.setText("无相关数据");
                ((LinearLayout)findViewById(R.id.ll_temp)).addView(noneData);
            }else{

                queueSimpleAdapter orderAdapter = new queueSimpleAdapter(myTrainList.this,list_order,R.layout.train_list_item);
                lv_order.setAdapter(orderAdapter);
            }
        }
        protected void onCancelled() {
            orderTask = null;
            showProgress(false);
        }

    }
    class queueSimpleAdapter extends SimpleAdapter {

        /**
         * Constructor
         *
         * @param context  The context where the View associated with this SimpleAdapter is running
         * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
         *                 Maps contain the data for each row, and should include all the entries specified in
         *                 "from"
         * @param resource Resource identifier of a view layout that defines the views for this list
         *                 item. The layout file should include at least those named views defined in "to"
         */

        private int mResource;
        private List<? extends Map<String, ?>> mData;

        public queueSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource) {
            super(context, data, resource, null,null);
            this.mResource = resource;
            this.mData = data;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup group) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(mResource, null);
            TextView trainNum = (TextView) view.findViewById(R.id.trainList_trainNum);
            trainNum.setText(mData.get(position).get("trainNum").toString());
            ((TextView) view.findViewById(R.id.trainList_text_src)).setText(mData.get(position).get("train_src").toString());
            ((TextView) view.findViewById(R.id.trainList_text_des)).setText(mData.get(position).get("train_des").toString());
            String str_goTime = mData.get(position).get("train_goTime").toString();
            String str_doneTime = mData.get(position).get("train_doneTime").toString();
            ((TextView) view.findViewById(R.id.trainList_text_start)).setText(str_goTime.substring(0, 5));
            ((TextView) view.findViewById(R.id.trainList_text_end)).setText(str_doneTime.substring(0, 5));
            ((TextView) view.findViewById(R.id.trainList_text_highSeat))
                    .setText(mData.get(position).get("seatType1").toString() + " "+mData.get(position).get("leftTicket1").toString());
            ((TextView) view.findViewById(R.id.trainList_text_midSeat))
                    .setText(mData.get(position).get("seatType2").toString() + " "+mData.get(position).get("leftTicket2").toString());
            ((TextView) view.findViewById(R.id.trainList_text_lowSeat))
                    .setText(mData.get(position).get("seatType3").toString() + " "+mData.get(position).get("leftTicket3").toString());


            return view;
        }
    }

}
