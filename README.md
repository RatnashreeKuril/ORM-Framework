# ORM Framework

ORM stands for Object-Relational Mapping, which simplifies mapping Java objects to database entities. The POJO Generator Tool automatically creates Plain Old Java Object (POJO) classes based on database table structures, reducing manual coding.

## Pojo Generator Tool

This tool automatically generates Plain Old Java Object (POJO) classes based on the structure of database tables. It simplifies the process of creating Java objects that map to database entities, reducing the need for manual coding.

To generate pojo classes using this tool follow the below instructions :

1. Download the orm.jar file of this repository.
2. In the working folder create a file named as conf.json
3. Write the following json in conf.json
```
{
"jdbcDriver" : "com.mysql.cj.jdbc.Driver",
"connectionUrl" : "jdbc:mysql://localhost:3306/databse_name",
"userName" : "username",
"password" : "password",
"packageName" : "com.test.pojo",
"jarName" : "pojo.jar",
}

```
4. In the working folder execute the PojoGenerator tool.
5. This tool will create a jar file inside the dist folder of the working directory with the name specified against “jarName” in conf.json.

## Support for In-memory database (Cache)

You need to specify the names of tables that you want to be cacheable in 'conf.json' against the 'cacheable' property, in the form of an array.
```
{
"jdbcDriver" : "com.mysql.cj.jdbc.Driver",
"connectionUrl" : "jdbc:mysql://localhost:3306/schooldb",
"userName" : "schooldbuser",
"password" : "schooldbuser",
"packageName" : "com.thinking.machines.test.student",
"jarName" : "pojo.jar",
"cacheable" : ["student","faculty"]
}

```
The framework will maintain an in memory datastructure for this tables.

## CRUD Operations

### DataManager

To get the object of this class one needs to call the static method

`public static DataManager getDataManager() throws DataException`

of DataManager class. This will return an object of DataManager class.

This framework provides the functionality to perform basic CURD operations in database without writing a single line of sql.

Refer the following code snippets to get a glimpse of how this framework works.

1. **Add**
```
import com.thinking.machines.orm.dataManager.*;
import com.thinking.machines.orm.exceptions.*;
import com.thinking.machines.test.student.*;
import java.util.*;
class AddStudent
{
public static void main(String gg[])
{
DataManager dm=null;
try
{
dm=DataManager.getDataManager();
dm.begin();
Student student=new Student();
student.setName("Ravi");
student.setSchoolRegNo(3);
student.setAadharCardNumber("463928713");
student.setGender("M");
int rollNumber=dm.save(student);
dm.end();
System.out.println("Student added with roll number : "+rollNumber);
}catch(DataException dataException)
{
System.out.println(dataException.getMessage());
try
{
dm.end();
}catch(Exception e)
{

}
}
}
}

```
2. **Update**
```
import com.thinking.machines.orm.dataManager.*;
import com.thinking.machines.orm.exceptions.*;
import com.thinking.machines.test.student.*;
import java.util.*;
class UpdateStudent
{
public static void main(String gg[])
{
DataManager dm=null;
try
{
dm=DataManager.getDataManager();
dm.begin();
Student student=new Student();
student.setRollNumber(1);
student.setName("Tarun");
student.setSchoolRegNo(3);
student.setAadharCardNumber("7678687886");
student.setGender("M");
dm.update(student);
dm.end();
System.out.println("Student updated");
}catch(DataException dataException)
{
System.out.println(dataException.getMessage());
try
{
dm.end();
}catch(Exception e)
{

}
}
}
}

```
3. **Delete**
```
import com.thinking.machines.orm.dataManager.*;
import com.thinking.machines.orm.exceptions.*;
import com.thinking.machines.test.student.*;
import java.util.*;
class DeleteStudent
{
public static void main(String gg[])
{
DataManager dm=null;
try
{
dm=DataManager.getDataManager();
dm.begin();
dm.delete(Student.class,3);
dm.end();
System.out.println("Student deleted");
}catch(DataException dataException)
{
System.out.println(dataException.getMessage());
try
{
dm.end();
}catch(Exception e)
{

}
}
}
}

```
4. **Retrieve**
```
import com.thinking.machines.orm.dataManager.*;
import com.thinking.machines.orm.exceptions.*;
import com.thinking.machines.test.student.*;
import java.util.*;
class GetAllSchool
{
public static void main(String gg[])
{
DataManager dm=null;
try
{
dm=DataManager.getDataManager();
dm.begin();
List<School> schools=(List<School>)dm.query(School.class).fire();
dm.end();
for(School school:schools)
{
System.out.println(school.getRegNo());
System.out.println(school.getName());
System.out.println(school.getCity());
System.out.println(school.getState());
}
}catch(DataException dataException)
{
System.out.println(dataException.getMessage());
try
{
dm.end();
}catch(Exception e)
{

}
}
}
}

```
**Note :** Place a call to *fire()* for firing the query.

### Applying where clause :

**where(String column_name) :** This function takes the column name as argument. Use this function for applying where clause.

`List<School> schools=(List<School>)dm.query(School.class).where(“reg_no”).eq(12).fire();`

This process is known as method chaining where we call multiple methods one after other on the same object. Use this technique for applying conditions on the query.

**eq(int num) :** equal to

**gt(int num) :** greater than

**lt(int num) :** less than

**ge(int num) :** greater than equal to

**le(int num) :** less than equal to

**ne(Object val) :** not equal to

**and(“column_name”) :** for using and operator

**or(“column_name”) :** for using or operator
