import java.sql.*;
import java.util.Properties;

/**
 * Initially created by Roman Gaev
 * 25.02.2018
 * ATTENTION: in order to use JDBC you need to download and include a postgres driver to your classpath(google that for your IDE)
 * <p>
 * May the force be with you.
 */
public class DatabaseConnection {
    public static void main(String[] args) {
        try {
            //Establishing the driver and connection
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://mod-msc-sw1.cs.bham.ac.uk:5432/mumbai";
            Properties props = new Properties();
            props.setProperty("user", "mumbai");
            props.setProperty("password", "s7e5n1p3tj");
            props.setProperty("ssl", "false");
            Connection con = DriverManager.getConnection(url, props);

            //Creating a statement - statement object operates all queries
            Statement stmt = con.createStatement();

            //executing SQL query
            stmt.executeQuery("SELECT\n" +
                    "    table_schema || '.' || table_name\n" +
                    "FROM\n" +
                    "    information_schema.tables\n" +
                    "WHERE\n" +
                    "    table_type = 'BASE TABLE'\n" +
                    "AND\n" +
                    "    table_schema NOT IN ('pg_catalog', 'information_schema');");
            ResultSet rs = stmt.executeQuery("SELECT * FROM users;");

            //going through the result set - every next set is a row from database
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4));
            }
            con.close();

            //catching all exceptions in appropriate order
        } catch (ClassNotFoundException e) {
            System.err.println("Database driver not found");
            e.printStackTrace();

        } catch (SQLException e) {
            System.err.println("SQL Exception");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Driver could not be registered");
            e.printStackTrace();
        }


    }

}