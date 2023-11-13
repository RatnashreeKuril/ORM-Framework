import com.thinking.machines.orm.dataManager.*;
import java.util.*;
class eg6psp
{
public static void main(String gg[])
{

List<Student> students;
DataManager dm=null;
try
{
dm=DataManager.getDataManager();
/*
dm.begin();
students=(List<Student>)dm.query(Student.class).where("roll_number").ne(104).and("roll_number").ne(105).fire();
for(Student s:students)
{
System.out.println("Roll number : "+s.getRollNumber());
System.out.println("First name : "+s.getFirstName());
System.out.println("Last name : "+s.getLastName());
System.out.println("Aadhar card number : "+s.getAadharCardNumber());
System.out.println("Course code : "+s.getCourseCode());
System.out.println("Gender : "+s.getGender());
System.out.println("Date of birth : "+s.getDateOfBirth());
System.out.println("*******************************************************");
}
dm.end();
*/
}catch(DataException dataException)
{
System.out.println(dataException);
try
{
dm.end();
}catch(DataException de)
{
System.out.println(de);
}
}
}
}
