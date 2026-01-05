package healthcare.model;
import java.time.LocalDate;

public class Clinician {
private String clinicianId;
private String firstName;
private String lastName;
private String title;
private String speciality;
private String gmcNumber;
private String phoneNumber;
private String email;
private String workplaceId;
private String workplaceType;
private String employmentStatus;
private LocalDate startDate;	
	
public Clinician(String clinicianId,String firstName,String lastName,String title,String speciality,String gmcNumber,
String phoneNumber,String email,String workplaceId,String workplaceType,String employmentStatus,LocalDate startDate) 
{this.clinicianId = clinicianId;
this.firstName = firstName;
this.lastName = lastName;
this.title = title;
this.speciality = speciality;
this.gmcNumber = gmcNumber;
this.phoneNumber = phoneNumber;
this.email = email;
this.workplaceId = workplaceId;
this.workplaceType = workplaceType;
this.employmentStatus = employmentStatus;
this.startDate = startDate;}	
	
// getters
	
public String getClinicianId() 
{return clinicianId;}

public String getFirstName() 
{return firstName;}

public String getLastName() 
{return lastName;}

public String getTitle() 
{return title;}

public String getSpeciality()
{return speciality;}
 
public String getGmcNumber() 
{return gmcNumber;}

public String getPhoneNumber() 
{return phoneNumber;}

public String getEmail() 
{return email;}

public String getWorkplaceId() 
{return workplaceId;}

public String getWorkplaceType() 
{return workplaceType;}
 
public String getEmploymentStatus() 
{return employmentStatus;}
 
public LocalDate getStartDate() 
{return startDate;}
	
//seter for update 	
public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}
public void setEmail(String email){ this.email = email;}


public String toCSV() {
    return String.join(",",clinicianId,firstName,lastName,title,speciality,gmcNumber,phoneNumber,email,workplaceId,
    workplaceType,employmentStatus,startDate.toString());}

@Override
public String toString(){return title + " " + firstName + " " + lastName + " (" + speciality + ")";
}
	
}
