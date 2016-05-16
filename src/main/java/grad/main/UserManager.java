package grad.main;

import grad.pojo.AccessToken;
import grad.util.WeixinUtil;
import net.sf.json.JSONObject;

import java.text.ParseException;
import java.util.List;

/**
 * Created by Jamie on 5/5/16.
 */
public class UserManager {

    public static void main(String[] args) throws ParseException {
        String appId = "wx973b5122c345d995";
        String appSecret = "1f35fa93bbd21317802a0ebafdb4db70";
        AccessToken at = WeixinUtil.getAccessToken(appId, appSecret);

        List<JSONObject> User_InfoList = WeixinUtil.getUserInfo("oog9Zv85WcOoYRCuOPwYb6KIVgHI", at.getToken());

        for (JSONObject User_Info : User_InfoList) {
            System.out.println(User_Info.get("nickname"));
        }
    }

    public static JSONObject getUser_Info(String fromUserName){
        String appId = "wx973b5122c345d995";
        String appSecret = "1f35fa93bbd21317802a0ebafdb4db70";
        AccessToken at = WeixinUtil.getAccessToken(appId, appSecret);

        List<JSONObject> User_InfoList = WeixinUtil.getUserInfo(fromUserName, at.getToken());

        for (JSONObject User_Info : User_InfoList) {
            System.out.println(User_Info.get("nickname"));
            return User_Info;
        }

        return null;
    }

}