package pw.horand.gohome;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

public class myOrderDetail extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private List<Map<String,Object>> list_order;
    private orderAsyncTask orderTask = null;

    private View mProgressView;
    private View mOrderDetailFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mOrderDetailFormView = findViewById(R.id.orderDetail_form);
        mProgressView = findViewById(R.id.orderDetail_progress);

        globalData userInfo = (globalData)getApplication();
        ((TextView) findViewById(R.id.login_mail)).setText(userInfo.getEmail().toString());



        Intent intent = getIntent();
        ((TextView)(findViewById(R.id.bookingDate))).setText("下单时间："+intent.getStringExtra("bookingDate"));
        ((TextView)(findViewById(R.id.trainNum))).setText("车  次  号："+intent.getStringExtra("trainNum"));
        ((TextView)(findViewById(R.id.orderNum))).setText("订  单  号：E"+intent.getStringExtra("orderNum"));
        ((TextView)(findViewById(R.id.srcDate))).setText("出发时间："+intent.getStringExtra("srcDate"));
        ((TextView)(findViewById(R.id.desDate))).setText("到达时间："+intent.getStringExtra("desDate"));
        ((TextView)(findViewById(R.id.srcStation))).setText("出  发  站："+intent.getStringExtra("srcStation"));
        ((TextView)(findViewById(R.id.desStation))).setText("终  到  站："+intent.getStringExtra("desStation"));
        ((TextView)(findViewById(R.id.orderUser))).setText("乘  坐  人："+intent.getStringExtra("name"));
        ((TextView)(findViewById(R.id.idCard))).setText("证  件  号："+intent.getStringExtra("idCard"));
        ((TextView)(findViewById(R.id.phoneNum))).setText("手  机  号：" + intent.getStringExtra("phoneNum"));
        String str_orderType = intent.getStringExtra("orderType");
        if(str_orderType.equals("0")){
            //str_orderType = "排队中(第" +intent.getStringExtra("leftPersion").toString()+"位)";

            str_orderType = "排队中";
            ((Button)findViewById(R.id.btn_goPay)).setVisibility(View.GONE);
        }else if(str_orderType.equals("1")){
            str_orderType = "待付款";
            ((Button)findViewById(R.id.btn_cancelOrder)).setVisibility(View.GONE);
        }else {
            str_orderType = "已完成";
            ((Button)findViewById(R.id.btn_goPay)).setVisibility(View.GONE);
            ((Button)findViewById(R.id.btn_cancelOrder)).setVisibility(View.GONE);
        }
        ((TextView)(findViewById(R.id.orderType))).setText("订单状态：" + str_orderType);

        orderTask = new orderAsyncTask(userInfo.apiURL+"/cancelOrder.action?orderNum="+intent.getStringExtra("orderNum"));



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

            mOrderDetailFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mOrderDetailFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mOrderDetailFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mOrderDetailFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
    private class orderAsyncTask extends AsyncTask<Void ,Integer,Boolean>{

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
                JSONObject a = new JSONObject(str_order[0]);
                if(a.getString("success").equals("1")) {
                    return true;
                }else{
                    return false;
                }
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
            orderTask = null;
            showProgress(false);

            if(result){
                Toast.makeText(myOrderDetail.this, "提交成功,即将跳转到我的订单界面", Toast.LENGTH_SHORT).show();
                Intent intent  = new Intent(myOrderDetail.this,myOrder.class);
                startActivity(intent);
            }else{
                Toast.makeText(myOrderDetail.this, "提交失败", Toast.LENGTH_SHORT).show();
            }

        }
        protected void onCancelled() {
            orderTask = null;
            showProgress(false);
        }
    }

    public void cancelOrderClick(View view){
        Intent intent = getIntent();
        String orderNum = intent.getStringExtra("orderNum");
        new  AlertDialog.Builder(myOrderDetail.this)
                .setTitle("取消订单" )
                .setMessage("您确定要取消该订单吗？该操作不可回退！" )
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog , int which){
                        showProgress(true);
                        orderTask.execute((Void) null);
                    }
                })
                .setNegativeButton("否", null)
                .show();
    }
    public void goPayClick(View view){

    }
}
