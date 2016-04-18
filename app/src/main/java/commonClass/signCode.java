package commonClass;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import pw.horand.gohome.globalData;

/**
 * Created by Administrator on 2016/4/8.
 */
public class signCode {
    public static Boolean getCode(String phone,String type){

        boolean result =  false;
        String temp = null;
        try {
            temp = httpGetAndToJson.httpGetOrder("http://10.202.24.55:8080/getVerifyCode.action?phone=" + phone + "&type=" + URLEncoder.encode(type, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonData = new JSONObject(temp);
            if(jsonData.getString("success").equals("1")) {
                result = true;
            }else{
                result = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
