package commonClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2016/4/8.
 */
public class httpGetAndToJson {
    public static String httpGetOrder(String path){
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
    public static List<Map<String,Object>> jsonToListMap(JSONArray ls) throws JSONException {

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
    public static String InputStreamTOString(InputStream in,String encoding) throws Exception{

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int count = -1;
        while((count = in.read(data,0,1024)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(),encoding);
    }
}
