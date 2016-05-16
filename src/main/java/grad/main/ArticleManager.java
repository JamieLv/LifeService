package grad.main;

import grad.database.Database;
import grad.database.Subscriber_Info;
import grad.message.resp.Article;
import grad.servlet.BaiduMapAPI;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static grad.service.CoreService.getGreeting;

/**
 * Created by Jamie on 5/8/16.
 */
public class ArticleManager {

    public static List<Article> SubscribeGreeting(String fromUserName) {
        List<Article> articleList = new ArrayList<>();
        Subscriber_Info subscriber_info = Database.getSubscriber_Info(fromUserName);

        Article articleSubscriberGreeting = new Article();
        articleSubscriberGreeting.setTitle(subscriber_info.getNickname() + "，" + getGreeting());
        articleList.add(articleSubscriberGreeting);

        Article articleSubscriberInfo = new Article();
        articleSubscriberInfo.setTitle("性别：" + subscriber_info.getSubscriber_Sex() +
                "\n所在地：" + subscriber_info.getSubscriber_Country() + " " + subscriber_info.getSubscriber_Province() + " " + subscriber_info.getSubscriber_City());
        articleSubscriberInfo.setPicUrl(subscriber_info.getSubscriber_HeadImgURL());
        articleList.add(articleSubscriberInfo);

        return articleList;
    }


    public static List<Article> NearbyInfo(String Location_X, String Location_Y, String request) throws IOException {
        List<Article> articleList = new ArrayList<>();
        String region = BaiduMapAPI.testPost(Location_X, Location_Y).get("city");

        Article articleNearbyLibrary = new Article();
        articleNearbyLibrary.setTitle("附近的" + request);
        articleList.add(articleNearbyLibrary);
        List<JSONObject> resultsList = BaiduMapAPI.getNearbyInfo(Location_X, Location_Y, request);
        for (JSONObject NearbyInfo: resultsList){
            Article articleLibraryInfo = new Article();
            articleLibraryInfo.setTitle("名字：" + NearbyInfo.get("name") +
                    "\n地址：" + NearbyInfo.get("address"));
            if (NearbyInfo.get("telephone") != null){
                articleLibraryInfo.setTitle(articleLibraryInfo.getTitle() +
                        "\n电话：" + NearbyInfo.get("telephone"));
            }
            //http://api.map.baidu.com/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&destination=大雁塔&mode=driving&region=西安&output=html //调起百度PC或Web地图，展示“西安市”从（lat:34.264642646862,lng:108.95108518068 ）“我家”到“大雁塔”的驾车路线。
            articleLibraryInfo.setUrl("http://api.map.baidu.com/direction?" +
                    "origin=" + Location_X + "," + Location_Y +
                    "&destination=" + NearbyInfo.get("name") +
                    "&region=" + region +
                    "&mode=walking&output=html");
            articleList.add(articleLibraryInfo);
        }

        return articleList;
    }

}
