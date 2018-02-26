import java.sql.Connection;
import java.sql.DriverManager;

/**
 * I'm not sure if this will work but this is for the connection ot the database
 * DEFINITELY REVISE THIS comments suck
 * @author Ali Oztas
 *
 */
public class DatabaseConnection {
	
	public static Connection createConnection() {
		
		Connection con = null;
		String url = "jdbc:postgresql://mod-msc-sw1.cs.bham.ac.uk:5432/mumbai"; // url of the db
		String username = "mumbai"; 
		String password = "s7e5n1p3tj";
		
	
	try {
	try {
		Class.forName("org.postgresql.Driver"); // try to find the driver
	}
	catch(ClassNotFoundException e) {
		e.printStackTrace();
	}
	con = DriverManager.getConnection(url, username, password); //our connection will be initialised wwith "con" variable with the appropriate inputs 
	System.out.println("Connection: " +con); //CONNECTIOOOON
	}
	catch(Exception e) {
		e.printStackTrace();
	}
	return con;
}
	
}