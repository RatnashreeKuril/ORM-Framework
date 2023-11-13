import com.thinking.machines.orm.pojoCopier.*;
class eg1psp
{
public static void main(String gg[])
{
Student s1=new Student();
s1.setRollNo(101);
s1.setFirstName("Teena");
s1.setLastName("Sharma");
s1.setAadharCardNumber("234243434");
s1.setGender("F");
Student s2=new Student();
try
{
PojoCopier.copy(s2,s1);
System.out.println(s2.getRollNumber());
System.out.println(s2.getFirstName());
System.out.println(s2.getLastName());
System.out.println(s2.getAadharCardNumber());
System.out.println(s2.getGender());
}catch(Exception exception)
{
System.out.println(exception);
}
}
}