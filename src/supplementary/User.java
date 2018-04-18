package supplementary;

/**
 * @author Roman Gaev
 * supplementary.User class represents user information for server needs
 * <p>
 * version 18.04.2018
 */
public class User {
    private String login;
    private String legalName;
    private String password;

    public User(String login, String password, String legalName) {
        this.login = login;
        this.legalName = legalName;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "login: " + login + " password: " + password + " legal name:" + legalName;
    }
}
