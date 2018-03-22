public class User {
    private String login;
    private String legalName;
    private String password;

    /**
     *
     * @param login Username of the client
     * @param password Password of the client
     * @param legalName Legal name of the client
     */
    public User(String login, String password, String legalName) {
        this.login = login;
        this.legalName = legalName;
        this.password = password;
    }

    /**
     * Getter for login(username)
     * @return Login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Setter for login
     * @param login Username to be set
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Getter for Legal name
     * @return Legal name
     */
    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    /**
     * Getter for password
     * @return Password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for password
     * @param password Password to be set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Overriding the toString method to view the user information
     * @return User's information
     */
    @Override
    public String toString() {
        return "login: " + login + " password: " + password + " legal name:" + legalName;
    }
}