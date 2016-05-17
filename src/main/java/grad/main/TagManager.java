package grad.main;

import grad.pojo.AccessToken;
import grad.util.WeixinUtil;
import java.text.ParseException;

import static grad.util.WeixinUtil.getUserTagID;

/**
 * Created by Jamie on 4/28/16.
 */
public class TagManager {

    public static void main(String[] args) throws ParseException {
        String appId = "wx973b5122c345d995";
        String appSecret = "1f35fa93bbd21317802a0ebafdb4db70";
        AccessToken at = WeixinUtil.getAccessToken(appId, appSecret);

        if (at != null) {
            String op = "get";
            if (op.equals("add")){WeixinUtil.createTag(at.getToken());}
            if (op.equals("get")){WeixinUtil.getAllTag(at.getToken());}
            if (op.equals("del")){WeixinUtil.deleteTag(at.getToken());}
            if (op.equals("up")){WeixinUtil.updateTag(at.getToken());}
//            System.out.println(getUserTagID(at.getToken(), "oog9Zv85WcOoYRCuOPwYb6KIVgHI"));
        }
    }

    public static boolean batchtagging(String fromUserName, String request){
        String appId = "wx973b5122c345d995";
        String appSecret = "1f35fa93bbd21317802a0ebafdb4db70";
        AccessToken at = WeixinUtil.getAccessToken(appId, appSecret);

        if (at != null) {
            if (request.equals("Member")){
                WeixinUtil.batchTag(at.getToken(), fromUserName);
            }
        }
        return true;
    }

    public static boolean batchuntagging(String fromUserName, String request){
        String appId = "wx973b5122c345d995";
        String appSecret = "1f35fa93bbd21317802a0ebafdb4db70";
        AccessToken at = WeixinUtil.getAccessToken(appId, appSecret);

        if (at != null) {
            if (request.equals("Member")){
                WeixinUtil.removeTag(at.getToken(), fromUserName);
            }
        }
        return true;
    }


}
