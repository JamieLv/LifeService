package grad.servlet;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;


/**
 * Created by Jamie on 4/22/16.
 */
public class BaiduMapAPI {
    private static String ak = "HAWvDthwbrBM8itvGawZ2E99EQTIg5qf";

    public static Map<String, String> testPost(String x, String y) throws IOException {
        URL url = new URL("http://api.map.baidu.com/geocoder?" + ak +
//                "HAWvDthwbrBM8itvGawZ2E99EQTIg5qf" +
                "&callback=renderReverse&location=" + x
                + "," + y + "&output=json");
        URLConnection connection = url.openConnection();
        /**
         * 然后把连接设为输出模式。URLConnection通常作为输入来使用，比如下载一个Web页。
         * 通过把URLConnection设为输出，你可以把数据向你个Web页传送。下面是如何做：
         */
        connection.setDoOutput(true);
        OutputStreamWriter out = new OutputStreamWriter(connection
                .getOutputStream(), "utf-8");
//        remember to clean up
        out.flush();
        out.close();
//        一旦发送成功，用以下方法就可以得到服务器的回应：
        String res;
        InputStream l_urlStream;
        l_urlStream = connection.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                l_urlStream,"UTF-8"));
        StringBuilder sb = new StringBuilder("");
        while ((res = in.readLine()) != null) {
            sb.append(res.trim());
        }
        String str = sb.toString();


        System.out.println(str);
        Map<String,String> map = null;
        if(StringUtils.isNotEmpty(str)) {
            int addStart = str.indexOf("city\":");
            int addEnd = str.indexOf("\",\"direction");
            if(addStart > 0 && addEnd > 0) {
                String city = str.substring(addStart+7, addEnd);
                map = new HashMap<String,String>();
                map.put("city", city);
                return map;
            }
        }
        return null;
    }

    public static List<JSONObject> getNearbyInfo(String Location_X, String Location_Y, String request) throws IOException {
        //http://api.map.baidu.com/place/v2/search?query=%E5%9B%BE%E4%B9%A6%E9%A6%86&output=json&ak=HAWvDthwbrBM8itvGawZ2E99EQTIg5qf&page_size=10&page_num=0&scope=1&location=31.207432,121.419617&radius=2000
        URL url = new URL("http://api.map.baidu.com/place/v2/search?query=" + request + "&output=json" +
                "&ak=" + ak +
                "&page_size=5&page_num=0&scope=2&" +
//                "location=39.915,116.404&radius=2000");
                "location=" + Location_X + "," + Location_Y + "&radius=5000");
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        OutputStreamWriter out = new OutputStreamWriter(connection
                .getOutputStream(), "utf-8");
//        remember to clean up
        out.flush();
        out.close();
//        一旦发送成功，用以下方法就可以得到服务器的回应：
        String res;
        InputStream l_urlStream;
        l_urlStream = connection.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                l_urlStream, "UTF-8"));
        StringBuilder sb = new StringBuilder("");
        while ((res = in.readLine()) != null) {
            sb.append(res.trim());
        }
        String str = sb.toString();

        JSONObject jsonObject = JSONObject.fromObject(str);
        JSONArray resultsList = jsonObject.getJSONArray("results");
        System.out.println(resultsList);
        List<JSONObject> Nearby_Info = new ArrayList<>();
        for (Object jsonObj : resultsList) {
            Nearby_Info.add(JSONObject.fromObject(jsonObj));
        }
//        System.out.println(Library_Info.get(1).get("address"));
        return Nearby_Info;
    }

//
//    public static void main(String[] args) throws IOException {
//        List<JSONObject> resultsList = getNearbyInfo("1", "1");
//        for (JSONObject tmpObj: resultsList){
//            System.out.println(tmpObj.get("name"));
//        }
//        System.out.println(testPost("39.915", "116.404").get("city"));
//    }

}
