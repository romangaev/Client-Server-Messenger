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
		char[] password = logindet.getPassword();
	Connection connection = null; // These will be used afterwards so they are initialised null or empty for now.
	Statement statement = null;
	ResultSet resultSet = null;
	String usernameDB = "";
	String passwordDB = "";
	
	
	//create an extra table which has user ID and cookie thingie.
	try {
		connection = DatabaseConnection.createConnection(); // creates the connection
		statement = connection.createStatement();
		String username = "Just someone";
		String getInfo = "SELECT username, password, online FROM users WHERE username = "+username; // changing this is enough to change the query
		resultSet = statement.executeQuery(getInfo); // executing the query 
		
		//ResultSet onStat = statement.executeQuery(getInfo);
		
		/*
		 *If such username does not exist, we can't have anything  
		 */
		if(!resultSet.next()) {
			System.out.println("Such username does not exist!");
		}
		else {
			int onlineStatus = resultSet.getInt("online");	 // 
			usernameDB = resultSet.getString("username");
			passwordDB = resultSet.getString("password");
			if(onlineStatus == 1) {
				resultSet.updateInt("online", 0);
			}
			if(onlineStatus == 0) {
				if(LoginPage.passwordText.getPassword().equals(passwordDB.toCharArray())) {
					LoginPage.chatPage();
					// open the chat
			}
		}
		}
		// DB should have sth to check if the user is online or not IT SHOULD BE INITIALISED AS FALSE ALWAYS!!!!!
		// WRITE A CODE TO CHECK IF THIS SHIT EXISTS THE USERNAME
		// THEN GET THE APPROPRIATE USERNAME
		
		//check if the username exists, if user exists just get the password and control it.
//	while(resultSet.next()) //until next row exists
//	userNameDB = resultSet.getString("username"); // get the username which is the feature named "username"
//	passwordDB = resultSet.getString("password");
//	
//	if(userName.equals(userNameDB) && password.equals(passwordDB)) {
//		return "SUCCESS"; // after the gui this will pop up the chat page 
//	}
	}
	catch(SQLException e) {
		e.printStackTrace();
	
	}
	return "Invalid user"; // If it doesn't succeed it should be wrong so...
}
}
