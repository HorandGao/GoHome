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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static pw.horand.gohome.LoginActivity.InputStreamTOString;

public class myBooking extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView lv_order;
    private List<Map<String,Object>> list_order;
    private orderAsyncTask orderTask = null;

    private View mProgressView;
    private View mtrainListFormView;

    private String seatPrice="0";
    private String seatType="1";
    private int personNum=0;
    private String trainNum;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mtrainListFormView = findViewById(R.id.trainList_form);
        mProgressView = findViewById(R.id.trainList_progress);

        globalData userInfo = (globalData)getApplication();
        ((TextView) findViewById(R.id.login_mail)).setText(userInfo.getEmail().toString());

        lv_order = (ListView)findViewById(R.id.lv_trainList);
        showProgress(true);
        String urlPath = null;
        try {
            urlPath = userInfo.apiURL + "/getPerson.action?name=" + URLEncoder.encode(userInfo.getEmail().toString(), "UTF-8");

            orderTask = new orderAsyncTask(urlPath);
            orderTask.execute((Void) null);
        } catch (Exception e) {
            e.printStackTrace();
        }



        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        ((TextView)findViewById(R.id.text_trainNum)).setText(intent.getStringExtra("trainNum"));
        trainNum = intent.getStringExtra("trainNum");
        ((TextView)findViewById(R.id.text_highSeat)).setText(intent.getStringExtra("seatType1")+" "+intent.getStringExtra("leftTicket1")+"张");
        ((TextView)findViewById(R.id.text_midSeat)).setText(intent.getStringExtra("seatType2")+" "+intent.getStringExtra("leftTicket2")+"张");
        ((TextView)findViewById(R.id.text_lowSeat)).setText(intent.getStringExtra("seatType3")+" "+intent.getStringExtra("leftTicket3")+"张");
        ((TextView)findViewById(R.id.text_src)).setText(intent.getStringExtra("train_src"));
        ((TextView)findViewById(R.id.text_des)).setText(intent.getStringExtra("train_des"));
        ((TextView)findViewById(R.id.text_start)).setText(intent.getStringExtra("train_goTime"));
        ((TextView)findViewById(R.id.text_end)).setText(intent.getStringExtra("train_doneTime"));
        ((TextView)findViewById(R.id.btn_lowSeat)).setText(intent.getStringExtra("seatType3")+" ￥"+intent.getStringExtra("seatPrice3"));
        ((TextView)findViewById(R.id.btn_midSeat)).setText(intent.getStringExtra("seatType2")+" ￥"+intent.getStringExtra("seatPrice2"));
        ((TextView)findViewById(R.id.btn_highSeat)).setText(intent.getStringExtra("seatType1")+" ￥"+intent.getStringExtra("seatPrice1"));


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
        getMenuInflater().inflate(R.menu.my_booking, menu);
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
                return;
            }else{

                queueSimpleAdapter orderAdapter = new queueSimpleAdapter(myBooking.this,list_order,R.layout.person_list);
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

            ((CheckBox) view.findViewById(R.id.personName)).setText(mData.get(position).get("name").toString());
            ((TextView) view.findViewById(R.id.personIDcard)).setText(mData.get(position).get("idcard").toString());

            ((CheckBox) view.findViewById(R.id.personName)).setOnCheckedChangeListener(listener);
            return view;
        }
    }


    public void selectSeatType(View view){
        ((Button)findViewById(R.id.btn_highSeat)).setBackgroundColor(0xFFa9a9a9);
        ((Button)findViewById(R.id.btn_midSeat)).setBackgroundColor(0xFFa9a9a9);
        ((Button)findViewById(R.id.btn_lowSeat)).setBackgroundColor(0xFFa9a9a9);
        view.setBackgroundColor(0xFFCE6262);
        seatPrice =((TextView) view).getText().toString();
        seatPrice = seatPrice.split("￥")[1];
        int total = personNum * Integer.parseInt(seatPrice);
        ((TextView)findViewById(R.id.textTotalPrice)).setText("￥"+total);
        if(view.getTag().equals("high")){
            seatType = "1";
        }else if(view.getTag().equals("mid")){
            seatType = "2";
        }else if(view.getTag().equals("low")){
            seatType = "3";
        }
    }

    public void submitOrderClick(View view){
        if(seatPrice.equals("0")){
            Toast.makeText(getApplicationContext(), "请选择座位类型" , Toast.LENGTH_LONG).show();
            return;
        }else if(personNum==0){
            Toast.makeText(getApplicationContext(), "请至少选择1名乘客" , Toast.LENGTH_LONG).show();
            return;
        }else if(personNum>3){
            Toast.makeText(getApplicationContext(), "一次至多选择3名乘客" , Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(myBooking.this,resultSubmitOrder.class);

        String str_idCard = "";
        for(int i=0 ; i< lv_order.getChildCount();i++){
            LinearLayout ll = (LinearLayout)lv_order.getChildAt(i);
            if(((CheckBox)ll.findViewById(R.id.personName)).isChecked()){
                str_idCard =str_idCard + "a" + ((TextView) ll.findViewById(R.id.personIDcard)).getText().toString();
            }
        }

        Log.i("aaaa","userId");
        Log.i("aaaa","trainNum");
        Log.i("aaaa","date");
        Log.i("aaaa","idcards");
        Log.i("aaaa","seatPrice");
        Log.i("aaaa","seatType");
        Log.i("aaaa","name1 name2 name3");
        //提交订单参数
//        trainNum;
//        date;
//        seatType;
//        seatPrice;
//        personNum;
//        personIDcard;



        //reverse to transform data!!!
       // startActivity(intent);

    }

    CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            CheckBox box = (CheckBox) buttonView;

            if(isChecked){
                personNum++;
            }else{
                personNum--;
            }
             int total = personNum * Integer.parseInt(seatPrice);
            ((TextView)findViewById(R.id.textTotalPrice)).setText("￥" + total);

            Toast.makeText(getApplicationContext(),
                    "获取的值:" + isChecked + "xxxxx" + box.getText(),
                    Toast.LENGTH_LONG).show();

        }
    };

}
