package bf.tool.dmv;

import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Baofeng Xue at 2015/11/17 21:21.
 */
public class DateUtil {

    private static Date endDate;

    public static void main(String[] args) throws Exception {
        ;
        List<String> saturdays = getSaturdays();
        for (String saturday : saturdays) {
            System.out.println(saturday);
        }
    }

    private static Date getEndDate() {
        if (endDate != null) return endDate;
        Date date = new Date();
        endDate = DateUtils.addDays(date, 90);
        return endDate;
    }

    static List<String> getSaturdays() {
        List<String> res = new ArrayList<>();
        Date first = nextSaturday();
        res.add(format(first));

        while (first.getTime() < getEndDate().getTime()) {
            first = DateUtils.addDays(first, 7);
            res.add(format(first));
        }

        res.remove(res.size() - 1);
        return res;
    }

    private static Date nextSaturday() {
        Calendar calendar = Calendar.getInstance();
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        while (i != Calendar.SATURDAY) {
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
            i = calendar.get(Calendar.DAY_OF_WEEK);
        }
        return calendar.getTime();
    }

    private static String format(Date date) {
        DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.US);
        return format.format(date);
    }
}
