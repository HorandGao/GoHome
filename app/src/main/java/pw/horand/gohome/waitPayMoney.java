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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static pw.horand.gohome.LoginActivity.InputStreamTOString;

public class waitPayMoney extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView lv_order;
    private List<Map<String,Object>> list_order;
    private orderAsyncTask orderTask = null;

    private View mProgressView;
    private View mAllOrderFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_pay_money);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAllOrderFormView = findViewById(R.id.payOrder_form);
        mProgressView = findViewById(R.id.payOrder_progress);

        globalData userInfo = (globalData)getApplication();
        ((TextView) findViewById(R.id.login_mail)).setText(userInfo.getEmail().toString());

        lv_order = (ListView)findViewById(R.id.lv_order);

        showProgress(true);
        orderTask = new orderAsyncTask(userInfo.apiURL+"/waitPayOrder.action?name="+userInfo.getEmail().toString());
        orderTask.execute((Void) null);

        lv_order.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(waitPayMoney.this, myOrderDetail.class);
                intent.putExtra("bookingDate", list_order.get(position).get("orderDate").toString());
                intent.putExtra("srcDate", list_order.get(position).get("srcDate").toString());
                intent.putExtra("desDate", list_order.get(position).get("desDate").toString());
                intent.putExtra("srcStation", list_order.get(position).get("srcStation").toString());
                intent.putExtra("desStation", list_order.get(position).get("desStation").toString());
                intent.putExtra("orderNum", list_order.get(position).get("orderNum").toString());
                intent.putExtra("trainNum", list_order.get(position).get("trainNum").toString());
                intent.putExtra("name", list_order.get(position).get("realName").toString());
                intent.putExtra("idCard", list_order.get(position).get("idCard").toString());
                intent.putExtra("phoneNum", list_order.get(position).get("phoneNum").toString());
                intent.putExtra("orderType", list_order.get(position).get("orderType").toString());
                startActivity(intent);

            }

        });


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
        getMenuInflater().inflate(R.menu.wait_pay_money, menu);
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

            mAllOrderFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mAllOrderFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAllOrderFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mAllOrderFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
                TextView noneData = new TextView(waitPayMoney.this);
                noneData.setText("无相关数据");
                ((RelativeLayout)findViewById(R.id.rl_waitOrder)).addView(noneData);
            }else{
                queueSimpleAdapter orderAdapter = new queueSimpleAdapter(waitPayMoney.this,list_order,R.layout.order_item);
                lv_order.setAdapter(orderAdapter);
            }
        }
        protected void onCancelled() {
            orderTask = null;
            showProgress(false);
        }
    }
    class queueSimpleAdapter extends SimpleAdapter{

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
            TextView srcDate = (TextView) view.findViewById(R.id.srcDate);
            srcDate.setText(mData.get(position).get("srcDate").toString());
            ((TextView) view.findViewById(R.id.srcStation)).setText(mData.get(position).get("srcStation").toString());
            ((TextView) view.findViewById(R.id.desStation)).setText(mData.get(position).get("desStation").toString());
            ((TextView) view.findViewById(R.id.trainNum)).setText(mData.get(position).get("trainNum").toString());

            String str_orderType = mData.get(position).get("orderType").toString();
            if(str_orderType.equals("0")){
                ((TextView) view.findViewById(R.id.orderType)).setText("排队中");
            }else if(str_orderType.equals("1")){
                ((TextView) view.findViewById(R.id.orderType)).setText("待付款");
            }else{
                ((TextView) view.findViewById(R.id.orderType)).setText("已完成");
            }


            return view;
        }
    }
}
