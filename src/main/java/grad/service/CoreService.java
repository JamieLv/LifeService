package grad.service;

import grad.database.*;
import grad.main.TagManager;
import grad.main.UserManager;
import grad.message.resp.Article;
import grad.message.resp.NewsMessage;
import grad.message.resp.TextMessage;
import grad.pojo.CommonButton;
import grad.tools.*;
import grad.util.MessageUtil;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static grad.main.ArticleManager.*;

/**
 *
 * Created by Jamie on 4/11/16.
 */
public class CoreService {

    public static String getGreeting() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour >= 6 && hour < 8) {
            greeting = "早上好";
        } else if (hour >= 8 && hour < 11) {
            greeting = "上午好";
        } else if (hour >= 11 && hour < 13) {
            greeting = "中午好";
        } else if (hour >= 13 && hour < 18) {
            greeting = "下午好";
        } else {
            greeting = "晚上好";
        }
        return greeting;
    }

    public static String Register(String fromUserName, String[] keywords) throws IOException {
        String respContent = "请求处理异常，请稍候尝试！";
        try {

            if (keywords.length == 4) {
                try {
                    int tag = Database.MemberExist(fromUserName);
                    if (tag == 0) {  // 第一次注册
                        // String Member_Name, String Member_Gender, int Member_Age, String Member_Mobile, String Member_RegisterTime, String Member_fromUserName, Boolean Member_Verification
                        if (Database.getMember_InfobyMember_Mobile(keywords[3]) == null) {
                            Member_Info new_member_info = new Member_Info(
                                    keywords[0], keywords[1], Integer.parseInt(keywords[2]), keywords[3], Database.getDate(0), fromUserName, false);
                            Database.Add(new_member_info);
                            System.out.println(new_member_info);

                            int yzm = Database.getMember_Info(fromUserName).getMember_ID();
                            SendMsg_webchinese sendMsg = new SendMsg_webchinese();
                            sendMsg.send(keywords[3], yzm);
                            respContent = "尊敬的用户，请输入您收到的短信验证码，仿照格式: yzm 1";
                        } else {
                            respContent = "尊敬的用户，该手机号已被注册。如有疑问请直接回复，我们将尽快回复，谢谢配合。";
                        }
                    } else if (tag == 1) { // 用户已登记，手机验证未通过
                        Member_Info new_member_info = new Member_Info(
                                keywords[0], keywords[1], Integer.parseInt(keywords[2]), keywords[3], Database.getDate(0), fromUserName, false);
                        Database.UpdateMemberInfo(fromUserName, new_member_info);
                        int yzm = Database.getMember_Info(fromUserName).getMember_ID();
                        SendMsg_webchinese sendMsg = new SendMsg_webchinese();
                        sendMsg.send(keywords[3], yzm);
                        respContent = "尊敬的用户，请输入您收到的短信验证码，仿照格式: \"yzm 1\"";
                    } else if (tag == 2) { // 已登记，手机验证通过
                        respContent = "尊敬的用户，您已完成注册，请点击\"登录\"按钮进行登录，出现读者证后请稍等10分钟，之后可进行正常操作，谢谢配合。";
                    }
                } catch (Exception e) { // 格式有误
                    respContent = "尊敬的用户，您输入的信息有误，请核对后重新输入！仿照格式: \"张三 男 20 13112345678\"。\n" +
                            "我们将发送验证短信至您填写的手机号，所以请务必填写正确的手机号，谢谢配合。";
                }
            } else if (keywords[0].equals("yzm")) { // yzm 1
                if (keywords.length == 2) {
                    String str_yzm = keywords[1];
                    int i_yzm = Integer.parseInt(str_yzm); // 获取验证码并转换成原型
                    int tag = Database.MemberExist(fromUserName);
                    if (tag == 0) {
                        respContent = "尊敬的用户，您还没有输入您的基本信息吧！\n"
                                + "请严格按照这个格式进行回复： \n"
                                + "姓名 性别 年龄 手机号\n"
                                + "60秒内将会收到有验证码的短信。\n"
                                + "到时请将验证码回复给微信平台，谢谢配合。";
                    } else if (tag == 1) {
                        if (i_yzm == Database.getMember_Info(fromUserName).getMember_ID()) {
                            Database.UpdateMember_Verification(fromUserName, true);
                            respContent = "恭喜您验证成功！请点击\"登录\"按钮进行登录，出现读者证后请稍等10分钟，之后可进行正常操作，谢谢配合。";
                        } else {
                            respContent = "尊敬的用户，验证码输入有误，请仔细核对！\n"
                                    + "或者再次按照以下格式进行回复： \n"
                                    + "姓名 性别 年龄 手机号\n"
                                    + "60秒内将会收到有验证码的短信。\n"
                                    + "到时请将验证码回复给微信平台，谢谢配合。";
                        }
                    } else { // 验证已通过
                        respContent = "尊敬的用户，您已完成注册，请点击\"登录\"按钮进行登录，出现读者证后请稍等10分钟，之后可进行正常操作，谢谢配合。";
                    }
                } else {
                    respContent = "验证码格式错误，请仿照格式: \"yzm 1\"回复，谢谢配合。";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return respContent;
    }


    public static String processRequest(HttpServletRequest request) {
        String respMessage = null;
        Database db = new Database();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8")); //获取当天日期

        try {
            // 默认返回的文本消息内容
            String respContent = "请求处理异常，请稍候尝试！";

            // 处理微信发来请求
            // xml请求解析
            Map<String, String> requestMap = MessageUtil.parseXml(request);

            // 发送方帐号（open_id）
            String fromUserName = requestMap.get("FromUserName");
            // 公众帐号
            String toUserName = requestMap.get("ToUserName");
            // 消息类型
            String msgType = requestMap.get("MsgType");
            // 时间
            String createTime = requestMap.get("CreateTime");

            // 默认回复文本消息
            TextMessage textMessage = new TextMessage();
            textMessage.setToUserName(fromUserName);
            textMessage.setFromUserName(toUserName);
            textMessage.setCreateTime(new Date().getTime());
            textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
            textMessage.setFuncFlag(0);

            // 创建图文信息
            NewsMessage newsMessage = new NewsMessage();
            newsMessage.setToUserName(fromUserName);
            newsMessage.setFromUserName(toUserName);
            newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
            newsMessage.setCreateTime(new Date().getTime());
            newsMessage.setFuncFlag(0);

            List<Article> articleList;

            Subscriber_Info subscriber_info = db.getSubscriber_Info(fromUserName);
            Member_Info member_info = db.getMember_Info(fromUserName);

            // 文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                //拿到用户发来的信息 去除用户回复信息的前后空格
                String content = requestMap.get("Content").trim();
                String[] keywords = content.trim().split(" ");
                switch (subscriber_info.getSubscriber_Function()) {
                    case "register":
                        respContent = Register(fromUserName, keywords);
                        break;

                    case "location":
                        db.UpdateSubscriber_Function(subscriber_info.getSubscriber_ID(), "General");
                        String Location_X = member_info.getLocation_X();
                        String Location_Y = member_info.getLocation_Y();

                        articleList = NearbyInfo(Location_X, Location_Y, content);
                        newsMessage.setArticleCount(articleList.size());
                        newsMessage.setArticles(articleList);
                        respMessage = MessageUtil.newsMessageToXml(newsMessage);

                        return respMessage;

                    case "supervisor":
                        if (content.startsWith("DeleteUser") || content.startsWith("deleteuser")){
                            Member_Info del_member_info = db.getMember_InfobyMember_ID(Integer.parseInt(keywords[1]));
                            db.Del(del_member_info);
                            respContent = "用户\n" + del_member_info.getMember_ID() + " " + del_member_info.getMember_Name() + " " + del_member_info.getMember_Mobile() + "\n删除成功";
                        } else if (content.equals("Help") || content.equals("help") || content.equals("H") || content.equals("h")){
                            respContent = "使用帮助\n" +
                                    "删除读者：DeleteUser 读者ID";
                        } else { respContent = "回复Help进行查询功能。"; }
                        break;

                    default:
                        if (content.equals(931014) /** && worker_info.getWorker_Duty().equals("超级管理员")*/) {
                            db.UpdateSubscriber_Function(subscriber_info.getSubscriber_ID(), "supervisor");
                            respContent = subscriber_info.getSubscriber_Function().equals("supervisor") ? "超级管理员模式关闭" : "超级管理员模式开启";

                        } else {
                            respContent = getGreeting() + "，尊敬的用户" + emoji(0x1F604)
                                    + "\n您的留言我们已经收到，并在24小时内回复您。";
                        }
                }

            } // 图片消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
                respContent = "我喜欢你发的图片！";

            } // 地理位置消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {

                String Location_X = requestMap.get("Location_X");
                String Location_Y = requestMap.get("Location_Y");

                db.UpdateMember_Location(member_info.getMember_ID(), Location_X, Location_Y);
                db.UpdateSubscriber_Function(subscriber_info.getSubscriber_ID(), "location");

                respContent = "请回复您要搜索的关键字";
            } // 链接消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
                respContent = "你发送的是链接消息哦！";

            } // 音频消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
                respContent = "你发送的是音频消息哦！";

                String voice = requestMap.get("Recognition").trim();
                String text = requestMap.get("MsgType");

                String creat_time = requestMap.get("CreateTime");


                respContent = voice + "\n" + text + "\n" + creat_time;

            } // 事件推送
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                // 事件类型
                String eventType = requestMap.get("Event");
                // 订阅
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
                    JSONObject User_Info = UserManager.getUser_Info(fromUserName);
                    if (db.getSubscriber_Info(fromUserName) == null) {
                        String Subscriber_Sex = "未知";
                        if ((int) User_Info.get("sex") == 1) {
                            Subscriber_Sex = "男";
                        } else if ((int) User_Info.get("sex") == 2) {
                            Subscriber_Sex = "女";
                        }

                        Subscriber_Info new_subscriber_info = new Subscriber_Info(
                                1, (String) User_Info.get("openid"), (String) User_Info.get("nickname"), Subscriber_Sex, (String) User_Info.get("language"),
                                (String) User_Info.get("city"), (String) User_Info.get("province"), (String) User_Info.get("country"), (String) User_Info.get("headimgurl"), "Subscribe");
                        db.Add(new_subscriber_info);
                    } else {
                        db.UpdateSubscriber_Info(fromUserName);
                    }

                    articleList = SubscribeGreeting(fromUserName);
                    newsMessage.setArticleCount(articleList.size());
                    newsMessage.setArticles(articleList);
                    respMessage = MessageUtil.newsMessageToXml(newsMessage);

                    return respMessage;

                } // 取消订阅
                else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
                    db.UpdateUnSubscriber_Info(fromUserName);
                    System.out.println(subscriber_info.getNickname() + "退订");

                } else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) { // 事件KEY值，与创建自定义菜单时指定的KEY值对应
                    String eventKey = requestMap.get("EventKey");

                    int tag = 0; // 既不是读者也不是职工
                    if (member_info != null) {tag=1;}

                    if (eventKey.equals(CommonButton.KEY_REGISTER)){
                        int Subscriber_ID = subscriber_info.getSubscriber_ID();
                        db.UpdateSubscriber_Function(Subscriber_ID, "register");

                        respContent = "请输入\"姓名 性别 年龄 手机号\"注册，谢谢。";

                    } else if (eventKey.equals(CommonButton.KEY_LOGIN)) {
                        int Subscriber_ID = subscriber_info.getSubscriber_ID();
                        db.UpdateSubscriber_Function(Subscriber_ID, "login");
                        switch (tag){
                            case 0:
                                db.UpdateSubscriber_Function(Subscriber_ID, "login");
                                respContent = "请点击\"注册\"按钮进行注册，谢谢。";
                                break;
                            case 1:
                                if(member_info.getMember_Verification() == false){ // 用户已登记，手机验证未通过
                                respContent = "尊敬的用户，您的手机号还未绑定，请点击\"注册\"按钮完成注册，谢谢配合。";
                                } else { // 成功
                                    db.UpdateSubscriber_Function(Subscriber_ID, "login");
                                    TagManager.batchtagging(fromUserName, "Member");
                                    MemberService.MemberTemplate(member_info);
                                    return "";
                                }
                                break;

                            default:
                                respContent = "按键功能出错，我们正在抢救。";
                        }

                    } else if (eventKey.equals(CommonButton.KEY_MEMBERSHIP)) {
                        MemberService.MemberTemplate(member_info);
                        return "";
                    } else if (eventKey.equals(CommonButton.KEY_LOG_OFF)) {
                        TagManager.batchuntagging(fromUserName, "Member");
                        respContent = "退出成功";
                    }
                } else if (eventType.equals(MessageUtil.EVENT_TYPE_SCANCODE_WAITMSG)) {

                }
            }

            textMessage.setContent(respContent);
            respMessage = MessageUtil.textMessageToXml(textMessage);
        }
         catch (Exception e) {
             e.printStackTrace();
             return "";
        }
        return respMessage;
    }

    /**
     * emoji表情转换(hex -> utf-16)
     *
     * @param hexEmoji
     * @return
     */
    public static String emoji(int hexEmoji) {
        return String.valueOf(Character.toChars(hexEmoji));
    }

}