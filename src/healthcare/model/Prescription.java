package healthcare.model;

import java.time.LocalDate;

public class Prescription {
private String prescriptionId;
private String patientId;
private String clinicianId;
private String appointmentId;
private LocalDate prescriptionDate;
private String medicationName;
private String dosage;
private String frequency;
private int durationDays;
private int quantity;
private String instructions;
private String pharmacyName;
private String status;
private LocalDate issueDate;
private LocalDate collectionDate;

public Prescription(String prescriptionId,String patientId,String clinicianId,String appointmentId,LocalDate prescriptionDate,String medicationName,
String dosage,String frequency,int durationDays,int quantity, String instructions,String pharmacyName,String status, LocalDate issueDate,LocalDate collectionDate) 
{
this.prescriptionId = prescriptionId;
this.patientId = patientId;
this.clinicianId = clinicianId;
this.appointmentId = appointmentId;
this.prescriptionDate = prescriptionDate;
this.medicationName = medicationName;
this.dosage = dosage;
this.frequency = frequency;
this.durationDays = durationDays;
this.quantity = quantity;
this.instructions = instructions;
this.pharmacyName = pharmacyName;
this.status = status;
this.issueDate = issueDate;
this.collectionDate = collectionDate;
}

public String getPrescriptionId()
{return prescriptionId;}

   public String getPatientId() 
	{ return patientId;}
	
   public String getClinicianId() 
	{return clinicianId; }
   public String getAppointmentId() 
	{ return appointmentId; }
   public LocalDate getPrescriptionDate()
	{return prescriptionDate;}
	
   public String getMedicationName() 
	{return medicationName;}
	
   public String getDosage()
	{return dosage;}
	
   public String getFrequency() 
	{return frequency;}
	
   public int getDurationDays() 
	{return durationDays;}
	
   public int getQuantity() 
	{return quantity;}
   public String getInstructions() 
	{return instructions;}
   public String getPharmacyName()
	{return pharmacyName;}
   public String getStatus() 
	{return status;}
   public LocalDate getIssueDate() 
	{return issueDate;}
	
   public LocalDate getCollectionDate() 
	{return collectionDate;}


//SETTER TO CHANGE STATUS AND COLLETDATE

   public void setStatus(String status) {this.status = status;}
   public void setCollectionDate(LocalDate collectionDate){this.collectionDate=collectionDate;}

   @Override
   public String toString()
   {return prescriptionId + " - " +medicationName +"for" +patientId;}


}
