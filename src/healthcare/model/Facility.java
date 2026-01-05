package healthcare.model;

public class Facility {
private String facilityid;
private String facilityName;
private String facilityType;
private String address;
private String postcode;
private String phoneNumber;
private String email;
private String openingHours;
private String managerName;
private int capacity;
private String specialitiesOffered;

public Facility(String facilityId,String facilityName,String facilityType,String address,String postcode,
String phoneNumber,String email,String openingHours, String managerName, int capacity, String specialitiesOffered)

{this.facilityid = facilityId;
this.facilityName = facilityName;
this.facilityType = facilityType;
this.address = address;
this.postcode = postcode;
this.phoneNumber = phoneNumber;
this.email = email;
this.openingHours = openingHours;
this.managerName = managerName;
this.capacity = capacity;
this.specialitiesOffered = specialitiesOffered;}

public String getFacilityId() 
{return facilityid;}

public String getFacilityName() 
{return facilityName;}

public String getFacilityType() 
{return facilityType;}

public String getAddress() 
{return address;}

public String getPostcode() 
{return postcode;}

public String getPhoneNumber() 
{return phoneNumber;}

public String getEmail() 
{return email;}

public String getOpeningHours()
{return openingHours;}

public String getManagerName() 
{return managerName;}

public int getCapacity() 
{return capacity;}

public String getSpecialitiesOffered() 
{return specialitiesOffered;}

//setters
public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}
public void setEmail(String email){this.email = email;}

@Override
public String toString(){
return facilityName+"("+facilityType+")";}


}
