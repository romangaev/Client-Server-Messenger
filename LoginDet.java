/**
 * Just a general constructor for the username and password
 * I don't think that this should have the other features because this is enough
 * To improve this we might use Maps and whatever we can talk to Nishan about this
 * DEFINITELY REVISE THIS comments suck
 * @author Ali Oztas
 *
 */
public class LoginDet {

	private String username;
	private char[] password;
	
	public LoginDet(String username, char[] password) {
		this.username = username;
		this.password = password;
	}
	
	public String getUserName() {
		return username;
	}
	public char[] getPassword() {
		return password;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(char[] password) {
		this.password = password;
	}

}
