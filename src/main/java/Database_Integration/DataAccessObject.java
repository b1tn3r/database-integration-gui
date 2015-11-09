package Database_Integration;
/* The data access object is used to to create the connection to the database, in which the connection
URL, username, password and any other connection properties are placed in a .properties file
because the connection information is usually not hardcoded in the source code
 */
import java.sql.*;
import java.util.*;
import java.io.*;

public class DataAccessObject {
    private static Connection conn;
    private static Properties props;
    private static String connectionURL;
    private static String path;
    private static String propertiesFile = "/connection.properties";           // when connection.properties is accessed the getResource method is used to get the file from the resources dir and then the path is taken from it

    // Constructor will Connect to Database
    private void connectFirst() throws ClassNotFoundException, SQLException, IOException {
        if(this.conn != null) {
            this.conn.close();        // closes any old connection when connect() is called
        }
        /*
        // ___________ Get connection properties _____________________
       this.props = new Properties();                           // a properties object is made that is used to retrieve the connection properties
        props.load(new FileInputStream(getClass().getResource(propertiesFile).getPath()));     // connection.properties file is read with FileInputStream and loaded to the Properties object

        String driver = props.getProperty("microsoftDriver");
        if(connectionURL == null) {
            this.connectionURL = props.getProperty("connectionURL_WinAuth");      // when the program starts at the login screen, the WindowsAuthentication connectionURL is used so the Users data can be retrieved from the database for login to another user and connection
        }
        */
        String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";          // the connection.properties file would not work in the Jar file, so these were just uploaded to the DataAccess Object
        if(connectionURL == null) {
            connectionURL = "jdbc:sqlserver://localhost:1433;databaseName=NWTraders;username=UsernameAccess;password=PublicPassword";
        }

        // ____________ Connect to Database ___________________________
        Class.forName(driver);
        conn = DriverManager.getConnection(connectionURL);
        conn.setAutoCommit(false);
    }

    protected void connect() throws ClassNotFoundException, SQLException, IOException {
        if(this.conn != null) {
            this.conn.close();        // closes any old connection when connect() is called
        }

        conn = DriverManager.getConnection(connectionURL);
        conn.setAutoCommit(false);
    }

    protected User changeProperty(User user, String password) throws ClassNotFoundException, SQLException, IOException {         // this method changes the connectionURL for all connection made to the database to change to a specified user in the Login screen

        this.connectionURL = "jdbc:sqlserver://localhost:1433;databaseName=NWTraders;username=" + user.getUsername() + ";password=" + password;      // the connectionURL is stored as a static instance of the class so it will be used by all connections after this change is made.. it changes the connection username and password to that of the user wanting to log in

        return user;                       // the userID of the user is returned to be used in the business layer so the records of the user are inserted into dbo.AuditHistory
    }

    protected void garbageCollectProperties() {
        this.connectionURL = null;                // makes the connectionURL that is holding the current Username and Password null, so then the non-referenced String can be garbage collected
        System.gc();
    }

    protected void commit(Connection connection, String commit) throws SQLException {
        if(commit.equals("yes")) {
            connection.commit();
        } else {
            connection.rollback();
        }
    }

    // This will be the method to add an employee to the database after all the fields in the form have been filled out
    protected String addEmployee(Employee employee, String sql, String queryEmployeeID, String auditSql, int userID) throws SQLException, ClassNotFoundException, IOException {
        Statement queryStatement = null;
        PreparedStatement prepStatement = null;

        try {
            /*    ______________ THIS WOULD BE INSERTED AS EmployeeID IF IT WASN'T AUTO INCREMENT_________________  // This should be used if you would want EmployeeID to not still account for any deleted rows since if 902 was deleted, the next row Inserted would still be 903 in the DBMS
            queryStatement = conn.createStatement();
            ResultSet resultSet = queryStatement.executeQuery("SELECT COUNT(*) FROM HumanResources.Employees");    // the total number of rows is queried to get the number that should be +1 for the next rows ID num
            int insertID = 0;
            while(resultSet.next()) {
                insertID = resultSet.getInt(1) + 1;                                             // the id that will be inserted for the new row will be the num of total rows in the database + 1
            }
            */
            connect();
            prepStatement = conn.prepareStatement(sql);                                             // insert statement added that adds all the columns

            prepStatement.setString(1, employee.getLastName());
            prepStatement.setString(2, employee.getFirstName());
            prepStatement.setString(3, employee.getTitle());
            prepStatement.setString(4, employee.getAddress());
            prepStatement.setString(5, employee.getCity());
            prepStatement.setString(6, employee.getHomePhone());    // all the parameters set except for the Photo file

            File file = employee.getPhoto();
            FileInputStream input = new FileInputStream(file);    // the File from Employee is wrapped around a FileInputStream so it can be placed in the setBinaryStream()
            prepStatement.setBinaryStream(7, input);             // file is formatted to a binary stream so it can be placed in the Insert statement

            prepStatement.executeUpdate();        // Insert statement executed

            BusinessLayer bl = new BusinessLayer();
            String answer = bl.saveG(conn, employee);

            prepStatement = conn.prepareStatement(queryEmployeeID);         // a query is made to return the EmployeeID of the new row Inserted above.. where only the first row is queried for EmployeeID that is in ORDER BY DESC so the last row is first
            ResultSet resultSet = prepStatement.executeQuery();
            int id = 0;
            while(resultSet.next()) {
                id = resultSet.getInt("EmployeeID");                     // the EmployeeID is found for the INSERTED row so it can be used for the INSERT in AuditHistory
            }
            System.out.println(id);

            prepStatement = conn.prepareStatement(auditSql);

            prepStatement.setInt(1, userID);                      // inserts the audit history of the inserted Employee into the dbo.AuditHistory table
            prepStatement.setInt(2, id);
            prepStatement.setString(3, "Added Employee: " + employee.getFirstName() + " " +
                                    employee.getLastName() + ", " + employee.getTitle());
            prepStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

            prepStatement.executeUpdate();

            conn.commit();             // commit the changes to the AuditHistory table after the Added Record action has been inserted into the table

            return answer;

        } finally {
            close(queryStatement);
            close(prepStatement);
        }
    }

    protected String updateEmployee(String sql, String newValue, int id, int column, String auditSql, int userID) throws ClassNotFoundException, SQLException, IOException {
        PreparedStatement prepStatement = null;

        try {
            connect();
            prepStatement = conn.prepareStatement(sql);                     // UPDATE statement created where row updated will be determined by the EmployeeID found in the selected table row

            if(column == 7) {
                File file = new File(newValue);                           // if the photo column is being updated it will be separated from the other sql statements by still using the column num
                FileInputStream input = new FileInputStream(file);
                prepStatement.setBinaryStream(1, input);                       // the binary stream is set for the updated value, while the EmployeeID is used for the WHERE clause
                prepStatement.setInt(2, id);
            } else {
                prepStatement.setString(1, newValue);                        // the rest of the columns just use a string as a newValue and can all be associated similarly
                prepStatement.setInt(2, id);
            }

            prepStatement.executeUpdate();

            prepStatement = conn.prepareStatement(auditSql);
            prepStatement.setInt(1, userID);                                 // both userID and EmployeeID are inserted into AuditHistory as foreign keys to link to Employees and Users, while AuditID is auto incremented
            prepStatement.setInt(2, id);
            prepStatement.setString(3, "Updated employee value: " + newValue);               // the action for the audit row states that it is an update and shows the value that is updated
            prepStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));         // the current date and time is posted onto the audit row

            prepStatement.executeUpdate();

            BusinessLayer bl = new BusinessLayer();
            String answer = bl.saveUpdateG(conn, column, newValue);                    // alert box called to ask if the user wants to save the changes, in which column and newValue are sent through to give an in-depth message on the alert box

            return answer;
        } finally {
            close(prepStatement);
        }
    }

    // A method to retrieve all Rows from Employees table that returns a List<Employee>
    protected ResultSet getAllEmployees(String sql) throws ClassNotFoundException, SQLException, IOException {
        connect();
        List<Employee> list = new ArrayList<>();       // a list with type Employee object is created
        Statement statement = null;
        ResultSet resultSet = null;


        statement = conn.createStatement();
        resultSet = statement.executeQuery(sql);         // All the fields used in the Employee object are queried except for file, because this would print out 900 files in the temp dir

        return resultSet;      // the list of Employee objects is returned by the method
    }

    // A method to search for any Employee row data that matches a lastName String entered by the user.. returns a list with type Employee to be used to output to the interface
    protected ResultSet searchEmployees(String search, String sql) throws ClassNotFoundException, SQLException, IOException {
        connect();
        List<Employee> list = new ArrayList<>();
        PreparedStatement prepStatement = null;
        ResultSet resultSet = null;


        prepStatement = conn.prepareStatement(sql);                 // the query will search for rows that have the last or first names match the parameter sent into the method

        prepStatement.setObject(1, search);
        prepStatement.setObject(2, search);                                // uses the %search% as the placeholders in the query
        resultSet = prepStatement.executeQuery();

        return resultSet;
    }

    protected Connection deleteFromDatabase(String sql, int employeeID) throws SQLException, ClassNotFoundException, IOException {
        PreparedStatement prepStatement = null;

        prepStatement = conn.prepareStatement(sql);

        prepStatement.setInt(1, employeeID);
        prepStatement.setInt(2, employeeID);
        prepStatement.executeUpdate();

        prepStatement.executeUpdate();

        if(prepStatement != null) {
            prepStatement.close();
        }

        return conn;
    }

    protected ResultSet getAuditHistory(String sql, int employeeID) throws SQLException, ClassNotFoundException, IOException {
        PreparedStatement prepStatement = null;
        ResultSet resultSet = null;

        connect();
        prepStatement = conn.prepareStatement(sql);

        prepStatement.setInt(1, employeeID);            // sets the employeeID of the audit history rows to retrieve

        resultSet = prepStatement.executeQuery();

        return resultSet;
    }

    protected void updateAuditHistory(String sql, String newValue, int auditID) throws SQLException, IOException, ClassNotFoundException {
        PreparedStatement prepStatement = null;

        try {
            connect();
            prepStatement = conn.prepareStatement(sql);

            prepStatement.setObject(1, newValue);
            prepStatement.setInt(2, auditID);

            prepStatement.executeUpdate();                // UPDATE statement executed to update the AuditHistory tale to the "newValue"

            conn.commit();

        } finally {
            close(prepStatement);
        }
    }

    protected String deleteAuditHistory(String sql, int auditID) throws SQLException, IOException, ClassNotFoundException {
        PreparedStatement prepStatement = null;

        connect();
        prepStatement = conn.prepareStatement(sql);

        prepStatement.setInt(1, auditID);

        prepStatement.executeUpdate();

        BusinessLayer bl = new BusinessLayer();
        String answer = bl.saveDeletionG(conn, 1);

        close(conn, prepStatement);

        return answer;
    }

    protected ResultSet getLoginUsers(String sql) throws SQLException, IOException, ClassNotFoundException {
        PreparedStatement prepStatement = null;

        connectFirst();
        prepStatement = conn.prepareStatement(sql);

        ResultSet resultSet = prepStatement.executeQuery();

        return resultSet;
    }

    protected ResultSet getHash(String sql) throws SQLException, IOException, ClassNotFoundException {
        Statement statement = null;

        statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        sql = null;                    // sql is made null so it can be garbage collected later

        return resultSet;
    }

    protected void updatePassword(String sql) throws SQLException, IOException, ClassNotFoundException {
        Statement statement = null;

        statement = conn.createStatement();
        statement.executeUpdate(sql);

        conn.commit();

        sql = null;
        if(statement != null) {
            statement.close();
        }
    }

    // This is a method to close any connections, statements, or resultSets
    private static void close(Connection conn, Statement statement, ResultSet resultSet) throws SQLException {
        if(resultSet != null) {
            resultSet.close();
        }
        if(statement != null) {
            statement.close();
        }
        if(conn != null) {
            conn.close();
        }
    }
    // close method when only the statement and resultSet are closed.. it calls close method above with null Connection param
    private static void close(Statement statement, ResultSet resultSet) throws SQLException {
        close(null, statement, resultSet);
    }
    private static void close(Connection conn, Statement statement) throws SQLException {
        close(conn, statement, null);
    }
    private static void close(Statement statement) throws SQLException {
        close(null, statement, null);
    }
    protected void close() throws SQLException {
        if(conn != null) {
            conn.close();
        }
    }

    protected void setPathVariable(String path) {
        this.path = path;                         // sets path variable for the DAO so it can be used anywhere needed in dao
    }
}
