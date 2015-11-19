package bf.tool.dmv;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Baofeng Xue at 2015/11/17 21:21.
 */
public class Main {

    org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);

    private static List<String> saturdays;
    private Map<String, String> cookies = new HashMap<>();

    public static void main(String[] args) throws Exception {
        Main appointment = new Main();
        appointment.app(args);
    }

    private void app(String[] args) throws Exception {
        String[] data = new String[]{
                "604", "579", "644", "504", "556",
                "599", "593", "548", "516", "631",
                "632", "503", "624", "523", "592"
        };
        FormData formData = new FormData(args).invoke();
        String[] birthSplit = formData.getBirthSplit();
        String dl = formData.getDl();
        String[] nameSplit = formData.getNameSplit();
        String[] phoneSplit = formData.getPhoneSplit();

        for (String i : data) {

            Connection connection = Jsoup.connect("https://www.dmv.ca.gov/wasapp/foa/findDriveTest.do")
                    .data("birthDay", birthSplit[2])
                    .data("birthMonth", birthSplit[1])
                    .data("birthYear", birthSplit[0])
                    .data("dlNumber", dl)
                    .data("firstName", nameSplit[0])
                    .data("lastName", nameSplit[1])
                    .data("numberItems", "1")
                    .data("officeId", i)
                    .data("requestedTask", "DT")
                    .data("resetCheckFields", "true")
                    .data("telArea", phoneSplit[0])
                    .data("telPrefix", phoneSplit[1])
                    .data("telSuffix", phoneSplit[2]);

            setConnection(connection);
            Connection.Response res = connection.execute();

            afterPost(res, "z:/findDriveTest.htm");
            Document doc = res.parse();
            Elements e = doc.select("#app_content table tbody tr td address");
            String location = e.html();
            logger.info("Checking Location:{},{}", i, location.split("<br>")[0]);

            String text = doc.select("#app_content table tbody tr td p.alert").html();
            if (!text.contains("The first available appointment for this office is on")) {
                logger.error("Something is wrong:" + text);
                continue;
            }

            if (saturdays == null)
                saturdays = DateUtil.getSaturdays();

            for (String date : saturdays) {
                System.out.print(".");
                logger.debug("checking:{}", date);
                connection = Jsoup.connect("https://www.dmv.ca.gov/wasapp/foa/checkDriveTest.do")
                        .data("checkAvail", "Check for Availability")
                        .data("formattedRequestedDate", date)
                        .data("requestedTime", "");
                setConnection(connection);
                res = connection.execute();
                afterPost(res, "z:/checkDriveTest.htm");
                Elements select = res.parse().select("#app_content p.alert");
                String ok = select.html();
                if (ok.contains("Sorry") || ok.contains("System Unavailable") || ok.contains("not available")) continue;
                logger.info(location);
                logger.info(date);
                makeAppointment();
            }
            System.out.println();
        }
    }

    private void setConnection(Connection connection) {
        connection.cookies(cookies)
                .method(Connection.Method.POST)
                .timeout(Integer.MAX_VALUE);
    }

    private void makeAppointment() throws IOException {
        Connection.Response res;
        Connection connection = Jsoup.connect("https://www.dmv.ca.gov/wasapp/foa/reviewDriveTest.do");
        setConnection(connection);
        res = connection.execute();
        afterPost(res, "z:/reviewDriveTest.htm");
        connection = Jsoup.connect("https://www.dmv.ca.gov/wasapp/foa/confirmDriveTest.do");
        setConnection(connection);
        res = connection.execute();
        afterPost(res, "z:/confirmDriveTest.htm");
        System.exit(0);
    }

    private void afterPost(Connection.Response res, String path) throws IOException {
        Map<String, String> cookies = res.cookies();
        if (cookies != null) {
            Set<String> strings = cookies.keySet();
            for (String string : strings) {
                this.cookies.put(string, cookies.get(string));
            }
        }
        Document doc = res.parse();
        String title = doc.title();
        if (title.contains("Error")) {
            logger.error(title);
        }
        writeToFile(doc.html(), path);
    }

    private void writeToFile(String content, String path) {
        try {
            try (PrintStream out = new PrintStream(new FileOutputStream(path))) {
                out.print(content);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
