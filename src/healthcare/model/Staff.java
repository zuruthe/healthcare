package healthcare.model;
import java.time.LocalDate;

public class Staff {
	private String staffId;
    private String firstName;
    private String lastName;
    private String role;
    private String department;
    private String facilityId;
    private String phoneNumber;
    private String email;
    private String employmentStatus;
    private LocalDate startDate;
    private String lineManager;
    private String accessLevel;

 public Staff(String staffId,String firstName,String lastName, String role,String department,String facilityId,String phoneNumber,String email,
 String employmentStatus,LocalDate startDate,String lineManager,String accessLevel) 
 { this.staffId = staffId;
this.firstName = firstName;
this.lastName = lastName;
this.role = role;
this.department = department;
this.facilityId = facilityId;
this.phoneNumber = phoneNumber;
this.email = email;
this.employmentStatus = employmentStatus;
this.startDate = startDate;
this.lineManager = lineManager;
this.accessLevel = accessLevel;}
   
 public String getStaffId() 
 {return staffId;}
 public String getFirstName() 
 {return firstName;}
 public String getLastName() 
 {return lastName;}
 public String getRole() 
 {return role;}
  public String getDepartment() 
  {return department;}
  public String getFacilityId() 
  {return facilityId;}
     
 public String getPhoneNumber() 
 {return phoneNumber;}
     
 public String getEmail() 
 {return email;}
     
 public String getEmploymentStatus() 
 {return employmentStatus;}
     
 public LocalDate getStartDate() 
 {return startDate;}
 public String getLineManager() 
  {return lineManager;}
  public String getAccessLevel() 
  {return accessLevel;}    
    
  //setter to updaTE
public void setPhoneNumber(String phoneNumber) {this.phoneNumber=phoneNumber;}
public void setEmail(String email){this.email = email;}
    
@Override
public String toString() 
{return firstName + " " +lastName+ "("+role+")";}    
    
}
