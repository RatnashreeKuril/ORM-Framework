@Table(name="student")
public class Student implements java.io.Serializable
{
@PrimaryKey
@Column(name="roll_number")
private int rollNumber;
@Column(name="first_name")
private java.lang.String firstName;
@Column(name="last_name")
private java.lang.String lastName;
@Column(name="aadhar_card_number")
private java.lang.String aadharCardNumber;
@ForeignKey(parent="course",column="code")
@Column(name="course_code")
private int courseCode;
@Column(name="gender")
private java.lang.String gender;
@Column(name="date_of_birth")
private java.util.Date dateOfBirth;
public Student()
{
this.rollNumber=0;
this.firstName="";
this.lastName="";
this.aadharCardNumber="";
this.courseCode=0;
this.gender="";
this.dateOfBirth=null;
}
public void setRollNumber(int rollNumber)
{
this.rollNumber=rollNumber;
}
public int getRollNumber()
{
return this.rollNumber;
}
public void setFirstName(java.lang.String firstName)
{
this.firstName=firstName;
}
public java.lang.String getFirstName()
{
return this.firstName;
}
public void setLastName(java.lang.String lastName)
{
this.lastName=lastName;
}
public java.lang.String getLastName()
{
return this.lastName;
}
public void setAadharCardNumber(java.lang.String aadharCardNumber)
{
this.aadharCardNumber=aadharCardNumber;
}
public java.lang.String getAadharCardNumber()
{
return this.aadharCardNumber;
}
public void setCourseCode(int courseCode)
{
this.courseCode=courseCode;
}
public int getCourseCode()
{
return this.courseCode;
}
public void setGender(java.lang.String gender)
{
this.gender=gender;
}
public java.lang.String getGender()
{
return this.gender;
}
public void setDateOfBirth(java.util.Date dateOfBirth)
{
this.dateOfBirth=dateOfBirth;
}
public java.util.Date getDateOfBirth()
{
return this.dateOfBirth;
}
}
