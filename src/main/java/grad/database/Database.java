package grad.database;

import grad.main.UserManager;
import grad.util.HibernateUtil;
import net.sf.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.Session;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Jamie on 4/15/16.
 */
public class Database {

    public static String getDate(int addDays) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.add(Calendar.DATE, addDays);
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat SDF = new SimpleDateFormat(pattern);
        String str_calendar = SDF.format(calendar.getTime());
        return str_calendar;
    }

    /**
     *
     * 判断是否存在
     */

    // 判断会员是否存在
    public static int MemberExist(String Member_fromUserName){
        int exist = 0; // 没有存在在名单中

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery("from Member_Info");
        List<Member_Info> member_infoList = query.list();
        session.getTransaction().commit();

        for(Member_Info member_info: member_infoList) {
            if(member_info.getMember_fromUserName().equals(Member_fromUserName) && member_info.getMember_Verification()){
                exist = 2; // 存在于名单中且通过验证
            } else if(member_info.getMember_fromUserName().equals(Member_fromUserName)) {
                exist = 1; // 存在于名单中
            }
        }
        return  exist;
    }

    /**
     *
     * 增
     */

    // 增加一条数据
    public static boolean Add(Object obj){

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save(obj);
        session.getTransaction().commit();

        return true;
    }

    /**
     *
     * 删
     */

    // 删除一条数据
    public static boolean Del(Object obj){

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.delete(obj);
        session.getTransaction().commit();

        return true;
    }

    /**
     *
     * 改
     */

    // 更新手机号
    public static boolean UpdateReaderInfo(String Member_fromUserName, Member_Info new_member_info){
        int Member_ID = getMember_Info(Member_fromUserName).getMember_ID();
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Member_Info member_info = (Member_Info) session.get(Member_Info.class, Member_ID);
        member_info.setMember_Name(new_member_info.getMember_Name());
        member_info.setMember_Gender(new_member_info.getMember_Gender());
        member_info.setMember_Age(new_member_info.getMember_Age());
        member_info.setMember_Mobile(new_member_info.getMember_Mobile());
        member_info.setMember_RegisterTime(new_member_info.getMember_RegisterTime());
        session.getTransaction().commit();

        return true;
    }

    // 更新读者验证状态
    public static boolean UpdateMember_Verification(String Member_fromUserName, boolean verify){
        int Member_ID = getMember_Info(Member_fromUserName).getMember_ID();
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Member_Info member_info = session.get(Member_Info.class, Member_ID);
        member_info.setMember_Verification(verify);
        session.getTransaction().commit();

        return true;
    }

    // 更新用户信息
    public static boolean UpdateSubscriber_Info(String fromUserName){
        int Subscriber_ID = getSubscriber_Info(fromUserName).getSubscriber_ID();
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Subscriber_Info subscriber_info = session.get(Subscriber_Info.class, Subscriber_ID);

        JSONObject User_Info = UserManager.getUser_Info(fromUserName);
        String Subscriber_Sex = "未知";
        if((int)User_Info.get("sex") == 1)  {Subscriber_Sex = "男";}
        else if ((int)User_Info.get("sex") == 2) {Subscriber_Sex = "女";}

        subscriber_info.setOpenID((String)User_Info.get("openid"));
        subscriber_info.setNickname((String)User_Info.get("nickname"));
        subscriber_info.setSubscriber_Sex(Subscriber_Sex);
        subscriber_info.setSubscriber_Language((String)User_Info.get("language"));
        subscriber_info.setSubscriber_City((String)User_Info.get("city"));
        subscriber_info.setSubscriber_Province((String)User_Info.get("province"));
        subscriber_info.setSubscriber_Country((String)User_Info.get("country"));
        subscriber_info.setSubscriber_HeadImgURL((String)User_Info.get("headimgurl"));
        session.getTransaction().commit();

        return true;
    }

    // 更新退订用户信息
    public static boolean UpdateUnSubscriber_Info(String fromUserName) {
        int Subscriber_ID = getSubscriber_Info(fromUserName).getSubscriber_ID();
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Subscriber_Info subscriber_info = session.get(Subscriber_Info.class, Subscriber_ID);
        subscriber_info.setSubscribe(0);
        session.getTransaction().commit();

        return true;
    }

    // 更改用户使用状态
    public static boolean UpdateSubscriber_Function(int Subscriber_ID, String Subscriber_Function){

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Subscriber_Info subscriber_info = session.get(Subscriber_Info.class, Subscriber_ID);
        if (subscriber_info.getSubscriber_Function().equals(Subscriber_Function)) {
            subscriber_info.setSubscriber_Function("General");
        } else { subscriber_info.setSubscriber_Function(Subscriber_Function); }
        session.getTransaction().commit();

        return true;
    }

    // 更新用户的坐标位置
    public static boolean UpdateMember_Location(int Member_ID, String Location_X, String Location_Y){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Member_Info member_info = session.get(Member_Info.class, Member_ID);
        member_info.setLocation_X(Location_X);
        member_info.setLocation_Y(Location_Y);

        return true;
    }


    /**
      *
      * 查
      */

    // 查用户
    public static Subscriber_Info getSubscriber_Info(String Subscriber_fromUserName){

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery(String.format("from Subscriber_Info where openid = '%s'", Subscriber_fromUserName));
        Subscriber_Info subscriber_info = null;
        if (query.list().size() > 0) {
            subscriber_info = (Subscriber_Info) query.list().get(0);
        }
        session.getTransaction().commit();

        return subscriber_info;
    }

    // 查会员
    public static Member_Info getMember_Info(String Member_fromUserName){

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery(String.format("from Member_Info where Member_fromUserName = '%s'", Member_fromUserName));
        Member_Info member_info = null;
        if (query.list().size() > 0) {
            member_info = (Member_Info) query.list().get(0);
        }
        session.getTransaction().commit();

        return member_info;
    }

    public static Member_Info getMember_InfobyMember_ID(int Member_ID){

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery(String.format("from Member_Info where Member_ID = '%s'", Member_ID));
        Member_Info member_info = null;
        if (query.list().size() > 0) {
            member_info = (Member_Info) query.list().get(0);
        }
        session.getTransaction().commit();

        return member_info;
    }

    public static Member_Info getMember_InfobyMember_Mobile(String Member_Mobile) {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery(String.format("from Member_Info where Member_Mobile = '%s'", Member_Mobile));
        Member_Info member_info = null;
        if (query.list().size() > 0) {
            member_info = (Member_Info) query.list().get(0);
        }
        session.getTransaction().commit();

        return member_info;
    }


        public static List<Member_Info> getAllMember_Info(){

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery("from Member_Info");
        List<Member_Info> member_infoList = query.list();
        session.getTransaction().commit();

        return member_infoList;
    }

}