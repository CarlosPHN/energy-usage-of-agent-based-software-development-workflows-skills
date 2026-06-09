import java.sql.*;
import java.io.*;
import java.util.*;

public class OrderProcessor {

    public List<String> processOrders(List<Integer> orderIds) throws SQLException {
        List<String> result = new ArrayList<String>();
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/db", "user", "pass");
        String sql = "";
        for (int i = 0; i < orderIds.size(); i++) {
            sql += "SELECT * FROM orders WHERE id = " + orderIds.get(i) + ";";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                result.add(rs.getString("name") + "," + rs.getString("total"));
            }
            rs.close();
            stmt.close();
        }
        conn.close();
        return result;
    }

    public String readConfig() throws IOException {
        FileInputStream fis = new FileInputStream("/etc/app/config.properties");
        int b;
        String content = "";
        while ((b = fis.read()) != -1) {
            content += (char) b;
        }
        fis.close();
        return content;
    }

    public List<Integer> findDuplicates(List<Integer> numbers) {
        List<Integer> dupes = new ArrayList<Integer>();
        for (int i = 0; i < numbers.size(); i++) {
            for (int j = 0; j < numbers.size(); j++) {
                if (i != j && numbers.get(i).equals(numbers.get(j))) {
                    dupes.add(numbers.get(i));
                }
            }
        }
        return dupes;
    }
}
