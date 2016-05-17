/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grad.main;

import grad.pojo.*;
import grad.util.WeixinUtil;

import java.text.ParseException;


/**
 * 菜单管理器类
 *
 * Created by Jamie on 4/12/16.
 */
public class MenuManager {

    public static void main(String[] args) throws ParseException {

        String appId = "wx973b5122c345d995";
        String appSecret = "1f35fa93bbd21317802a0ebafdb4db70";


        // 调用接口获取access_token
        AccessToken at = WeixinUtil.getAccessToken(appId, appSecret);

        if (at != null) {
            // 调用接口创建菜单
            int create = WeixinUtil.createMenu(getMenuEn(), at.getToken());
            int addEn = WeixinUtil.AddConditionalMenu(getMenu(), at.getToken());

            int addMemberEn = WeixinUtil.AddConditionalMenu(getMembemrMenuEn(), at.getToken());
            int addMember = WeixinUtil.AddConditionalMenu(getMemberMenu(), at.getToken());

            // 判断菜单创建结果
            if (create == 0) {
                System.out.println("菜单创建成功！\n" + at.getToken());
            } else {
                System.out.println("菜单创建失败，错误码：" + create);
            }
        }
    }

    /**
     * 组装菜单数据
     *
     * @return
     */
    // 英文 默认
    private static Menu getMenuEn() {
        int l = 1;

        CommonButton btn11 = new CommonButton();
        btn11.setName(l == 0 ? "注册" : "Register");
        btn11.setType("click");
        btn11.setKey(CommonButton.KEY_REGISTER);

        CommonButton btn12 = new CommonButton();
        btn12.setName(l == 0 ? "登录" : "Login");
        btn12.setType("click");
        btn12.setKey(CommonButton.KEY_LOGIN);

        CommonButton btn2 = new CommonButton();
        btn2.setName(l == 0 ? "官网" : "Official Website");
        btn2.setType("view");
        btn2.setUrl("http://sse.tongji.edu.cn/zh");

        ComplexButton mainBtn1 = new ComplexButton();
        mainBtn1.setName(l == 0 ? "个人用户" : "User");
        mainBtn1.setSub_button(new Button[]{btn11, btn12});

        Menu menu = new Menu();
        menu.setButton(new Button[]{mainBtn1, btn2});

        return menu;
    }
    // 中文 默认
    private static Menu getMenu() {
        int l = 0;

        CommonButton btn11 = new CommonButton();
        btn11.setName(l == 0 ? "注册" : "Register");
        btn11.setType("click");
        btn11.setKey(CommonButton.KEY_REGISTER);

        CommonButton btn12 = new CommonButton();
        btn12.setName(l == 0 ? "登录" : "Login");
        btn12.setType("click");
        btn12.setKey(CommonButton.KEY_LOGIN);

        CommonButton btn2 = new CommonButton();
        btn2.setName(l == 0 ? "官网" : "Official Website");
        btn2.setType("view");
        btn2.setUrl("http://sse.tongji.edu.cn/zh");

        ComplexButton mainBtn1 = new ComplexButton();
        mainBtn1.setName(l == 0 ? "个人用户" : "User");
        mainBtn1.setSub_button(new Button[]{btn11, btn12});

        Menu menu = new Menu();
        menu.setButton(new Button[]{mainBtn1, btn2});
        menu.setMatchrule(new MatchRule("zh_CN"));

        return menu;
    }

    // 英文 登录
    private static Menu getMembemrMenuEn() {
        int l = 1;

        CommonButton btn11 = new CommonButton();
        btn11.setName(l == 0 ? "会员卡" : "Member");
        btn11.setType("click");
        btn11.setKey(CommonButton.KEY_MEMBERSHIP);

        CommonButton btn12 = new CommonButton();
        btn12.setName(l == 0 ? "退出登录" : "Log off");
        btn12.setType("click");
        btn12.setKey(CommonButton.KEY_LOG_OFF);

        CommonButton btn2 = new CommonButton();
        btn2.setName(l == 0 ? "附近" : "Nearby");
        btn2.setType("location_select");
        btn2.setKey(CommonButton.KEY_NEARBY);

        CommonButton btn3 = new CommonButton();
        btn3.setName(l == 0 ? "官网" : "Official Site");
        btn3.setType("view");
        btn3.setUrl("http://sse.tongji.edu.cn/zh");

        ComplexButton mainBtn1 = new ComplexButton();
        mainBtn1.setName(l == 0 ? "个人服务" : "User");
        mainBtn1.setSub_button(new Button[]{btn11, btn12});

        Menu menu = new Menu();
        menu.setButton(new Button[]{mainBtn1, btn2, btn3});
        menu.setMatchrule(new MatchRule(100));

        return menu;
    }
    // 中文 读者
    private static Menu getMemberMenu() {
        int l = 0;

        CommonButton btn11 = new CommonButton();
        btn11.setName(l == 0 ? "会员卡" : "Member");
        btn11.setType("click");
        btn11.setKey(CommonButton.KEY_MEMBERSHIP);

        CommonButton btn12 = new CommonButton();
        btn12.setName(l == 0 ? "退出登录" : "Log off");
        btn12.setType("click");
        btn12.setKey(CommonButton.KEY_LOG_OFF);

        CommonButton btn2 = new CommonButton();
        btn2.setName(l == 0 ? "附近" : "Nearby");
        btn2.setType("location_select");
        btn2.setKey(CommonButton.KEY_NEARBY);

        CommonButton btn3 = new CommonButton();
        btn3.setName(l == 0 ? "官网" : "Official Site");
        btn3.setType("view");
        btn3.setUrl("http://sse.tongji.edu.cn/zh");

        ComplexButton mainBtn1 = new ComplexButton();
        mainBtn1.setName(l == 0 ? "个人服务" : "User");
        mainBtn1.setSub_button(new Button[]{btn11, btn12});

        Menu menu = new Menu();
        menu.setButton(new Button[]{mainBtn1, btn2, btn3});
        menu.setMatchrule(new MatchRule(100, "zh_CN"));

        return menu;
    }

}
