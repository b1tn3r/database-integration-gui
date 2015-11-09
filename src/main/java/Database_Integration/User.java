package Database_Integration;
// This is a placeholder object for all the information out of the dbo.Users table

public class User {
    private int UserID;
    private String Username;
    private String Password;
    private String LastName;
    private String FirstName;


    protected User(int userID, String username, String password, String lastName, String firstName) {
        UserID = userID;
        Username = username;
        Password = password;
        LastName = lastName;
        FirstName = firstName;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }
}
