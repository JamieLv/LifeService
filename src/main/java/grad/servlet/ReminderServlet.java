package grad.servlet;

import grad.database.Database;
import grad.database.Member_Info;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jamie on 4/29/16.
 */
public class ReminderServlet implements ServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                List<Member_Info> member_infoList = Database.getAllMember_Info();
                try {
                    for (Member_Info member_info : member_infoList) {
//                        if (Database.getBook_StatebyBorrower(member_info.getMember_ID()) != null
//                                && Database.Borrower_RemindNeed(member_info.getMember_fromUserName()) != 1) {
//                                BorrowReminder.BorrowReminderTemplate(member_info.getMember_fromUserName());
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1, TimeUnit.DAYS);
    }
}
