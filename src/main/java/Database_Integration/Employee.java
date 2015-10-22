package Database_Integration;
// Employee class will be an object to hold basic information about the Employee data returned from the database

// This class holds the instance variables for Employee object along with initiating them in the constructor
// and all the getters and setters for them as well

import java.io.File;

public class Employee {
    private int EmployeeID;
    private String LastName;
    private String FirstName;
    private String Title;
    private String Address;
    private String City;
    private String HomePhone;
    private File Photo;

    public Employee(int EmployeeID, String LastName, String FirstName, String Title, String Address,
                    String City, String HomePhone, File Photo) {
        super();
        this.EmployeeID = EmployeeID;
        this.LastName = LastName;
        this.FirstName = FirstName;
        this.Title = Title;
        this.Address = Address;
        this.City = City;
        this.HomePhone = HomePhone;
        this.Photo = Photo;                  // The BinaryStream for the Photo of a SQL image type is first returned as an InputStream using the resultSet.getBinaryStream method, and then a byte[] array is used to read the InputStream and write to a FileOutputStream that outputs to a File
    }

    // This is the constructor for the method that does not convert Photo as a file
    public Employee(int EmployeeID, String LastName, String FirstName, String Title, String Address,
                    String City, String HomePhone) {
        super();
        this.EmployeeID = EmployeeID;
        this.LastName = LastName;
        this.FirstName = FirstName;
        this.Title = Title;
        this.Address = Address;
        this.City = City;
        this.HomePhone = HomePhone;
    }
    public Employee(int EmployeeID, String LastName, String FirstName, String Title) {           // this is another method for when only these parameters are entered by the user
        super();
        this.EmployeeID = EmployeeID;
        this.LastName = LastName;
        this.FirstName = FirstName;
        this.Title = Title;
    }
    public Employee(int EmployeeID, String LastName, String FirstName) {
        super();
        this.EmployeeID = EmployeeID;
        this.LastName = LastName;
        this.FirstName = FirstName;
    }

    public String getAddress() {          // everything from here down are getters and setters for the variables
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public int getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(int employeeID) {
        EmployeeID = employeeID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getHomePhone() {
        return HomePhone;
    }

    public void setHomePhone(String homePhone) {
        HomePhone = homePhone;
    }

    public File getPhoto() {
        return Photo;
    }

    public void setPhoto(File photo) {
        Photo = photo;
    }
}
