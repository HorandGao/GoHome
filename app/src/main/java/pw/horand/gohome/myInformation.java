package pw.horand.gohome;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

import commonClass.CustomDialog;

import static pw.horand.gohome.LoginActivity.InputStreamTOString;

public class myInformation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView lv_infoPerson;
    private List<Map<String, Object>> list_person;
    private personAsyncTask personTask = null;

    private View mProgressView;
    private View mInfoPersonFormView;
    CustomDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mInfoPersonFormView = findViewById(R.id.infoPerson_form);
        mProgressView = findViewById(R.id.infoPerson_progress);

        globalData userInfo = (globalData) getApplication();
        ((TextView) findViewById(R.id.login_mail)).setText(userInfo.getEmail().toString());
        ((TextView) findViewById(R.id.info_email)).setText(userInfo.getEmail().toString());
        ((TextView) findViewById(R.id.info_name)).setText(userInfo.getRealName().toString());
        ((TextView) findViewById(R.id.info_idcard)).setText(userInfo.getIdcard().toString());
        ((TextView) findViewById(R.id.info_phone)).setText(userInfo.getPhone().toString());

        lv_infoPerson = (ListView) findViewById(R.id.lv_infoPerson);


        showProgress(true);
        personTask = new personAsyncTask(userInfo.apiURL + "/getPerson.action?name=" + userInfo.getEmail().toString());
        personTask.execute((Void) null);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

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
        getMenuInflater().inflate(R.menu.my_information, menu);
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

            Intent intent = new Intent(this, MainActivity.class);
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
            Intent intent = new Intent(this, myOrder.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, waitPayMoney.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(this, myQueue.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, myInformation.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //PERSON参数
    //  http://localhost:8080/updatePerson.action?name=111@111&oldIdcard=4222984432234&newIdcard=4222011111111&realName=SMNNA
    //  http://localhost:8080/deletePerson.action?name=111@111&idcard=4222984432234
    //  http://localhost:8080/addPerson.action?name=2@2&idcard=42229844321345&realName=test

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mInfoPersonFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mInfoPersonFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mInfoPersonFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mInfoPersonFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public String httpGetOrder(String path) {
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
        } catch (Exception e) {

        }
        return null;
    }

    public List<Map<String, Object>> jsonToListMap(JSONArray ls) throws JSONException {

        List<Map<String, Object>> item_temp = new ArrayList<Map<String, Object>>();
        if (ls.length() > 0) {
            for (int i = 0; i < ls.length(); i++) {
                JSONObject jsonObject2 = ls.getJSONObject(i);
                Map<String, Object> map = new HashMap<String, Object>();
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

    private class personAsyncTask extends AsyncTask<Void, Integer, Boolean> {

        private String pathUrl;

        personAsyncTask(String path) {
            pathUrl = path;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final String[] str_order = new String[1];
            str_order[0] = httpGetOrder(pathUrl);

            try {
                JSONArray json_order = new JSONArray(str_order[0]);
                list_person = jsonToListMap(json_order);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(Boolean result) {
            showProgress(false);
            if (list_person.size() == 0) {
                TextView noneData = new TextView(myInformation.this);
                noneData.setText("无相关数据");
                ((RelativeLayout) findViewById(R.id.rl_allOrder)).addView(noneData);
            } else {
                queueSimpleAdapter orderAdapter = new queueSimpleAdapter(myInformation.this, list_person, R.layout.info_person);
                lv_infoPerson.setAdapter(orderAdapter);
            }

        }

        protected void onCancelled() {
            personTask = null;
            showProgress(false);
        }

    }

    class queueSimpleAdapter extends SimpleAdapter {

        /**
         * Constructor
         *
         * @param context  The context where the View associated with this SimpleAdapter is running
         * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
         * Maps contain the data for each row, and should include all the entries specified in
         * "from"
         * @param resource Resource identifier of a view layout that defines the views for this list
         * item. The layout file should include at least those named views defined in "to"
         */

        private int mResource;
        private List<? extends Map<String, ?>> mData;

        public queueSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource) {
            super(context, data, resource, null, null);
            this.mResource = resource;
            this.mData = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup group) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(mResource, null);
            ((TextView) view.findViewById(R.id.info_personName)).setText(mData.get(position).get("name").toString());
            ((TextView) view.findViewById(R.id.info_personIDcard)).setText(mData.get(position).get("idcard").toString());
            return view;
        }
    }

    public void addPerson(View v) {

        dialog = new CustomDialog(this, R.style.customDialog, R.layout.customdialog,
                new CustomDialog.LeaveMyDialogListener() {
                    @Override
                    public void onClick(View view) {
                        switch(view.getId()){
                            case R.id.myok:
                                saveClickFun();
                                break;
                            case R.id.mycancel:
                                cancelClickFun();
                                break;
                            default:
                                break;
                        }
                    }
                });
        dialog.show();
    }
    class MyHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if(msg.what == 1){
                Toast.makeText(myInformation.this, "验证码获取成功", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(myInformation.this, "验证码获取失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void saveClickFun(){
        final String personName = ((TextView) dialog.findViewById(R.id.edit_personName)).getText().toString();
        final String personIdcard = ((TextView) dialog.findViewById(R.id.edit_personIdcard)).getText().toString();
        globalData gd = (globalData) getApplication();
        final String addPersonUrl;
        try {
            addPersonUrl = gd.apiURL+"/addPerson.action?name="+gd.getEmail()+"&realName="+ URLEncoder.encode(personName, "UTF-8")+"&idcard="+personIdcard;
            new Thread() {
                public void run(){
                    httpGetOrder(addPersonUrl);
                }
            }.start();
            dialog.dismiss();
            Toast.makeText(myInformation.this, "提交完成", Toast.LENGTH_SHORT).show();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        Toast.makeText(getApplicationContext(), personName.getText()+"  "+personIdcard.getText(), Toast.LENGTH_LONG).show();

    }
    public void cancelClickFun(){
        dialog.dismiss();
    }
}