package Database_Integration;
// This is an object holder class for queried data from dbo.AuditHistory table so it can be placed in a table/observable list etc.

import java.sql.Timestamp;

public class AuditHistory {
    private int AuditID;
    private int UserID;
    private int EmployeeID;
    private String Action;
    private String ActionDateTime;
    private String UserName;
    private String Password;

    public AuditHistory(int AuditID, int UserID, int EmployeeID, String Action, String ActionDateTime) {
        this.AuditID = AuditID;
        this.UserID = UserID;
        this.EmployeeID = EmployeeID;
        this.Action = Action;
        this.ActionDateTime = ActionDateTime;
    }

    public AuditHistory(int UserID, int EmployeeID, String Action, String ActionDateTime) {
        this.UserID = UserID;
        this.EmployeeID = EmployeeID;
        this.Action = Action;
        this.ActionDateTime = ActionDateTime;
    }

    public AuditHistory(int AuditID, int UserID, int EmployeeID, String Action, String ActionDateTime, String UserName, String Password) {
        this.AuditID = AuditID;
        this.UserID = UserID;
        this.EmployeeID = EmployeeID;
        this.Action = Action;
        this.ActionDateTime = ActionDateTime;
        this.UserName = UserName;
        this.Password = Password;
    }
    public AuditHistory(int AuditID, int EmployeeID, String Action, String ActionDateTime, String UserName, String Password) {
        this.AuditID = AuditID;
        this.EmployeeID = EmployeeID;
        this.Action = Action;
        this.ActionDateTime = ActionDateTime;
        this.UserName = UserName;
        this.Password = Password;
    }


    public AuditHistory(String Action, String ActionDateTime, String UserName, String Password) {
        this.Action = Action;
        this.ActionDateTime = ActionDateTime;
        this.UserName = UserName;
        this.Password = Password;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getAuditID() {
        return AuditID;
    }

    public void setAuditID(int auditID) {
        AuditID = auditID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public int getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(int employeeID) {
        EmployeeID = employeeID;
    }

    public String getActionDateTime() {
        return ActionDateTime;
    }

    public void setActionDateTime(String actionDateTime) {
        ActionDateTime = actionDateTime;
    }
}
