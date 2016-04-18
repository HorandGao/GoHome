package pw.horand.gohome;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import c.b.BP;
import commonClass.signCode;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private SignUpTask mSignUpTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    protected static final int CHANGE_UI=1;
    protected static final int ERROR =2;
    MyHandler handler;
    private EditText sign_tel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    try {
                        attemptLogin();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        handler = new MyHandler();


        BP.init(LoginActivity.this, "7cdf7edff1cfe65e9937f0f1e2a8e4f0");


        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    attemptLogin();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Button mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.layout_sign_up);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() throws IOException, JSONException {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask("http://115.28.158.46",email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private void attemptSignUp(){
        String pattern_phone = "^(13[0-9]|15[01]|153|15[6-9]|180|18[23]|18[5-9])\\d{8}$";
        String pattern_mail = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        String name = ((EditText)findViewById(R.id.name)).getText().toString();
        String pwd = ((EditText)findViewById(R.id.PWD)).getText().toString();
        String code = ((EditText)findViewById(R.id.signCode)).getText().toString();
        String repeatPwd=((EditText)findViewById(R.id.repeatPWD)).getText().toString();
        String idcard = ((EditText)findViewById(R.id.sign_idcard)).getText().toString();
        String phone = ((EditText)findViewById(R.id.tel)).getText().toString();
        String realName = ((EditText)findViewById(R.id.realName)).getText().toString();

        if(name.equals("")){
            Toast.makeText(LoginActivity.this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(realName.equals("")){
            Toast.makeText(LoginActivity.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(phone.equals("")){
            Toast.makeText(LoginActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(code.equals("")){
            Toast.makeText(LoginActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(idcard.equals("")){
            Toast.makeText(LoginActivity.this, "证件号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if(pwd.equals("")){
            Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!name.matches(pattern_mail)){
            Toast.makeText(LoginActivity.this, "邮箱格式不对", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!phone.matches(pattern_phone)){
            Toast.makeText(LoginActivity.this, "手机号格式不对", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!pwd.equals(repeatPwd)){
            Toast.makeText(LoginActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        showProgress(true);
        mSignUpTask = new SignUpTask(name,pwd,idcard,phone,realName,code);
        mSignUpTask.execute((Void) null);
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }




    public class SignUpTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mIdcard;
        private final String mPhone;
        private final String mRealName;
        private final String mCode;
        private String mMessage="请求超时";

        SignUpTask(String name, String password ,String idcard,String phone ,String realName,String code) {
            mEmail = name;
            mPassword = password;
            mIdcard = idcard;
            mPhone = phone;
            mRealName = realName;
            mCode = code;
        }

        @Override
        protected Boolean doInBackground(Void... params){
            // TODO: attempt authentication against a network service.
            boolean temp = false;
            try {
                // Simulate network access.
                //Thread.sleep(2000);
                //String path = "http://10.202.24.55:8080/addUser.action?code="+mCode+"&name="+mEmail+"&pwd="+mPassword+"&idcard="+mIdcard+"&phone="+mPhone+"&realName="+URLEncoder.encode(mRealName,"UTF-8");
                String path = "http://115.28.158.46/addUser.action?code="+mCode+"&name="+mEmail+"&pwd="+mPassword+"&idcard="+mIdcard+"&phone="+mPhone+"&realName="+URLEncoder.encode(mRealName,"UTF-8");
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                conn.connect();
                if(conn.getResponseCode() == 200){
                    InputStream is = conn.getInputStream();
                    String test = InputStreamTOString(is, "UTF-8");
                    JSONObject a = new JSONObject(test);
                    mMessage = a.getString("msg");
                    if(a.getString("success").equals("1")) {
                        temp = true;
                    }else{
                        temp= false;
                    }
                }else{
                    temp = false;
                }
                conn.disconnect();
            }catch (Exception e){
                return false;
            }
            return temp;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (success) {
                Toast.makeText(LoginActivity.this, "注册成功，跳转到登录界面 ", Toast.LENGTH_SHORT).show();
                cancelSignUpClick(new View(LoginActivity.this));
            } else {
                Toast.makeText(LoginActivity.this, "注册失败："+mMessage, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private String mPhone;
        private String mRealName;
        private String mIdcard;
        private final String mPath;

        UserLoginTask(String path ,String email, String password) {
            mEmail = email;
            mPassword = password;
            mPath = path;
        }

        @Override
        protected Boolean doInBackground(Void... params){
            // TODO: attempt authentication against a network service.
            boolean temp = false;
            try {
                // Simulate network access.
                //Thread.sleep(2000);
                String path = mPath+"/test?name="+mEmail+"&password="+mPassword;

                //String path = "http://115.28.158.46/test?name="+mEmail+"&password="+mPassword;
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                conn.connect();
                if(conn.getResponseCode() == 200){
                    InputStream is = conn.getInputStream();
                    String test = InputStreamTOString(is, "UTF-8");
                    JSONArray jA = new JSONArray(test);
                    JSONObject a = jA.getJSONObject(0);
                    if(a.getString("rowcount").equals("1")) {
                        mPhone = a.getString("phoneNum");
                        mIdcard = a.getString("idCard");
                        mRealName = a.getString("realName");
                        temp = true;
                    }else{
                        temp= false;
                    }
                }else{
                    temp = false;
                }
                conn.disconnect();
            }catch (Exception e){

                return false;
            }
            return temp;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {

                globalData userInfo = (globalData)getApplication();
                userInfo.setEmail(mEmail);
                userInfo.setIdcard(mIdcard);
                userInfo.setPhone(mPhone);
                userInfo.setRealName(mRealName);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("reverse", "");
                startActivity(intent);

            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }

    public static String InputStreamTOString(InputStream in,String encoding) throws Exception{

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int count = -1;
        while((count = in.read(data,0,1024)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(),encoding);
    }


    public void goSignUpClick(View v){
        attemptSignUp();
    }



    public void cancelSignUpClick(View v){
        setContentView(R.layout.activity_login);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    try {
                        attemptLogin();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    attemptLogin();
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        });

        Button mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.layout_sign_up);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    public void getVerifyCodeClick(View v){
        String pattern = "^(13[0-9]|15[01]|153|15[6-9]|180|18[23]|18[5-9])\\d{8}$";
        sign_tel = (EditText)findViewById(R.id.tel);
        final String phone = sign_tel.getText().toString().trim();
        if(!phone.matches(pattern)){
            Toast.makeText(LoginActivity.this, "手机号格式不对", Toast.LENGTH_SHORT).show();
            return ;
        }
        final String type = "注册";
            //子线程中网络请求
        new Thread() {
            public void run(){
                  if(signCode.getCode(phone,type)){
                      Message msg = new Message();
                      msg.what = CHANGE_UI;
                      handler.sendMessage(msg);
                  }else {
                      Message msg = new Message();
                      msg.what = ERROR;
                      handler.sendMessage(msg);
                  }
            };
        }.start();
    }

    class MyHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if(msg.what == 1){
                Toast.makeText(LoginActivity.this, "验证码获取成功", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(LoginActivity.this, "验证码获取失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
