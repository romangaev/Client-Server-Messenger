import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * Just a template login with comparison from the database
 * DEFINITELY REVISE THIS comments suck
 * @author Ali Oztas
 *
 */
public class LoginDB {

	public String accessUser(LoginDet logindet) {
		
		String userName = logindet.getUserName();
		String password = logindet.getPassword();
	Connection connection = null; // These will be used afterwards so they are initialised null or empty for now.
	Statement statement = null;
	ResultSet resultSet = null;
	String userNameDB = "";
	String passwordDB = "";
	
	
	try {
		connection = DatabaseConnection.createConnection(); // creates the connection
		statement = connection.createStatement();
		String query = "SELECT username, password FROM users"; // changing this is enough to change the query
		resultSet = statement.executeQuery(query); // executing the query 
	
	while(resultSet.next()) //until next row exists
	userNameDB = resultSet.getString("username"); // get the username which is the feature named "username"
	passwordDB = resultSet.getString("password");
	
	if(userName.equals(userNameDB) && password.equals(passwordDB)) {
		return "SUCCESS"; // after the gui this will pop up the chat page 
	}
	}
	catch(SQLException e) {
		e.printStackTrace();
	
	}
	return "Invalid user"; // If it doesn't succeed it should be wrong so...
}
}
