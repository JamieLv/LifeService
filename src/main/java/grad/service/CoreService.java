package grad.service;

import grad.message.resp.Article;
import grad.message.resp.NewsMessage;
import grad.message.resp.TextMessage;
import grad.util.MessageUtil;


import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * Created by Jamie on 4/11/16.
 */
public class CoreService {

    public static String getGreeting() {
        Calendar cal = Calendar.getInstance();
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



    /**
     * 处理微信发来的请求
     *
     * @param request
     * @return
     */
    public static String processRequest(HttpServletRequest request) {
        String respMessage = null;
        //Database db = new Database();
        String currentTime;
        //获得当前时间
        //格式如下： 20160411-11:12:43
        Calendar calendar = Calendar.getInstance(); //获取当天日期
        SimpleDateFormat sdf = new SimpleDateFormat("20yyMMdd-HH:mm:ss");
        currentTime = sdf.format(calendar.getTime());

        try {
            // 默认返回的文本消息内容
            String respContent = "请求处理异常，请稍候尝试！";

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

            //创建图文信息
            NewsMessage newsMessage = new NewsMessage();
            newsMessage.setToUserName(fromUserName);
            newsMessage.setFromUserName(toUserName);
            newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
            newsMessage.setCreateTime(new Date().getTime());
            newsMessage.setFuncFlag(0);


            // 文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                //拿到用户发来的信息 去除用户回复信息的前后空格
                String content = requestMap.get("Content").trim();

                List<Article> articleList = new ArrayList<Article>();

                respContent = getGreeting() + "，尊敬的读者" + emoji(0x1F604)
                        + "\n您的留言我们已经收到，并在24小时内回复您\n"
                        + "我们的微信正在不断完善，您的支持给了我们更多的动力！";

            } // 图片消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
                respContent = "我喜欢你发的图片！";

            } // 地理位置消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
                respContent = "你发的是地理位置哦！";

            } // 链接消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
                respContent = "你发送的是链接消息哦！";

            } // 音频消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
                respContent = "你发送的是音频消息哦！";

            } // 事件推送
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                // 事件类型
                String eventType = requestMap.get("Event");
                // 订阅
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
                    respContent
                            = getGreeting() + "，尊敬的读者:)\n"
                            + "感谢关注图书馆\n"
                            + "赶紧戳一戳下方按钮，来和我们互动吧！";

                } // 取消订阅
                else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {

                } else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) { // 事件KEY值，与创建自定义菜单时指定的KEY值对应
                    String eventKey = requestMap.get("EventKey");

                    if (eventKey.equals("11")) {
                        respContent = "11！";
                    } else if (eventKey.equals("12")) {
                        respContent = "12！";
                    } else if (eventKey.equals("13")) {
                        respContent = "13！";
                    } else if (eventKey.equals("14")) {
                        respContent = "14！";
                    } else if (eventKey.equals("21")) {
                        respContent = "21！";
                    } else if (eventKey.equals("22")) {
                        respContent = "22！";
                    } else if (eventKey.equals("31")) {
                        respContent = "31！";
                    } else if (eventKey.equals("32")) {
                        respContent = "32！";
                    } else if (eventKey.equals("34")) {
                        respContent = "34！";
                    }

                } else if (eventType.equals(MessageUtil.EVENT_TYPE_SCANCODE_WAITMSG)) {
                    String scanResult = requestMap.get("ScanResult");
                    respContent = getGreeting() + scanResult;

                }
            }

            textMessage.setContent(respContent);
            respMessage = MessageUtil.textMessageToXml(textMessage);

        }
         catch (Exception e) {
            e.printStackTrace();
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
