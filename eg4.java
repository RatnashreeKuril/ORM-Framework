import java.sql.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.*;
class TableInfo
{
public String name;
public Class c;
public int columnCount;
}
class DataException extends Exception
{
DataException(String message)
{
super(message);
}
}
class DataManager
{
private Connection connection;
private static DataManager dataManager=null;
private String queryString;
private Class c;
private List<Object> conditions;
private List<String> ds;
private DataManager()
{
this.connection=null;
this.queryString="";
this.c=null;
this.conditions=null;
populateDS();
}
private void populateDS()
{

}
public static DataManager getDataManager()
{
if(dataManager==null) dataManager=new DataManager();
return dataManager;
}
public void begin() throws DataException
{
try
{
Class.forName("com.mysql.cj.jdbc.Driver");
connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/tmschool","tmschooluser","tmschooluser");
connection.setAutoCommit(false);
}catch(Exception exception)
{
throw new DataException(exception.getMessage());
}
}
public void end() throws DataException
{
try
{
connection.commit();
connection.close();
}catch(Exception exception)
{
throw new DataException(exception.getMessage());
}
}
public int save(Object obj) throws DataException
{
try
{
Class c=obj.getClass();
boolean isTable=c.isAnnotationPresent(Table.class);
if(!(isTable)) throw new DataException("Specified object does not represent any table");
Column column;
Annotation an;
int code=0;
List<ColumnInfo> columnInfoList=new ArrayList<ColumnInfo>();
ColumnInfo columnInfo;
ForeignKey foreignKey;
String str;
an=c.getAnnotation(Table.class);
Table table=(Table)an;
String tableName=table.name();
System.out.println("Class name : "+c.getName());
System.out.println("Table name : "+tableName);
Field []fields=c.getDeclaredFields();
for(Field field:fields)
{
if(field.isAnnotationPresent(Column.class)&& field.isAnnotationPresent(AutoIncrement.class)==false)
{
an=field.getAnnotation(Column.class);
column=(Column)an;
columnInfo=new ColumnInfo();
columnInfo.name=field.getName();
columnInfo.columnNameInTable=column.name();
if(field.isAnnotationPresent(ForeignKey.class))
{
columnInfo.isForeignKey=true;
an=field.getAnnotation(ForeignKey.class);
foreignKey=(ForeignKey)an;
columnInfo.parentTable=foreignKey.parent();
columnInfo.parentColumn=foreignKey.column();
}
columnInfoList.add(columnInfo);
}
}
/*
for(ColumnInfo ci:columnInfoList)
{
System.out.println("Column name : "+ci.name);
System.out.println("Column name in table : "+ci.columnNameInTable);
}
*/
String statement="insert into "+tableName+"(";
int i=0;
for(ColumnInfo ci:columnInfoList)
{
i++;
statement+=ci.columnNameInTable;
if(i<columnInfoList.size()) statement+=',';
}
statement+=")";
statement+=" value(";
for(int e=0;e<columnInfoList.size()-1;e++) statement+="?,";
statement+="?)";
System.out.println("Statement : "+statement);
PreparedStatement preparedStatement=connection.prepareStatement(statement,Statement.RETURN_GENERATED_KEYS);
Object value;
String getterName;
String tmp;
Method getter;
int e=1;
PreparedStatement preparedStatementForForeignKey;
for(ColumnInfo ci:columnInfoList)
{
tmp=ci.name.substring(0,1).toUpperCase()+ci.name.substring(1);
getterName="get"+tmp;
//System.out.println("Getter name : "+getterName);
getter=c.getMethod(getterName);
value=getter.invoke(obj);
//System.out.println("Value : "+value);
if(ci.isForeignKey)
{
preparedStatementForForeignKey=connection.prepareStatement("select * from "+ci.parentTable+" where "+ci.parentColumn+"=?");
if(value instanceof Integer) 
{
preparedStatementForForeignKey.setInt(1,(Integer)value);
}
ResultSet resultSet=preparedStatementForForeignKey.executeQuery();
if(resultSet.next()==false)
{
resultSet.close();
preparedStatement.close();
throw new DataException("Invalid course code : "+code);
}
else
{
System.out.println("Code is correct : "+resultSet.getInt("code"));
}
resultSet.close();
preparedStatementForForeignKey.close();
}
if(value instanceof String)
{
preparedStatement.setString(e,(String)value);
//System.out.println(value+" is String");
}
if(value instanceof Integer)
{
preparedStatement.setInt(e,(Integer)value);
}
if(value instanceof java.util.Date)
{
java.util.Date utilDate=(java.util.Date)value;
java.sql.Date sqlDate=new java.sql.Date(utilDate.getYear(),utilDate.getMonth(),utilDate.getDate());
preparedStatement.setDate(e,sqlDate);
}
if(value instanceof Boolean)
{
preparedStatement.setBoolean(e,(Boolean)value);
}
//preparedStatement.setString(e,value);
e++;
}
preparedStatement.executeUpdate();
ResultSet resultSet=preparedStatement.getGeneratedKeys();
if(resultSet.next())
{
code=resultSet.getInt(1);
System.out.println("Code : "+code);
}
return code;
}catch(Exception exception)
{
throw new DataException(exception.getMessage());
}
}
public void update(Object obj) throws DataException
{
try
{
Class c=obj.getClass();
boolean isTable=c.isAnnotationPresent(Table.class);
Column column;
Annotation an;
int code=0;
List<ColumnInfo> columnInfoList=new ArrayList<ColumnInfo>();
ColumnInfo columnInfo;
ForeignKey foreignKey;
String str;
if(isTable)
{
an=c.getAnnotation(Table.class);
Table table=(Table)an;
String tableName=table.name();
System.out.println("Class name : "+c.getName());
System.out.println("Table name : "+tableName);
Field []fields=c.getDeclaredFields();
String tmp;
String getterName;
Method getter;
String primaryKeyName="";
ResultSet resultSet;
Object primaryKeyValue=null;
PreparedStatement preparedStatement;
for(Field field:fields)
{
if(field.isAnnotationPresent(Column.class))
{
an=field.getAnnotation(Column.class);
column=(Column)an;
columnInfo=new ColumnInfo();
columnInfo.name=field.getName();
tmp=columnInfo.name.substring(0,1).toUpperCase()+columnInfo.name.substring(1);
getterName="get"+tmp;
//System.out.println("Getter name : "+getterName);
getter=c.getMethod(getterName);
columnInfo.value=getter.invoke(obj);
//System.out.println("Value : "+value);
columnInfo.columnNameInTable=column.name();
if(field.isAnnotationPresent(PrimaryKey.class))
{
columnInfo.isPrimaryKey=true;
primaryKeyName=columnInfo.columnNameInTable;
primaryKeyValue=columnInfo.value;
preparedStatement=connection.prepareStatement("select * from "+tableName+" where "+columnInfo.columnNameInTable+"=?");
if(primaryKeyValue instanceof Integer)
{
preparedStatement.setInt(1,(Integer)primaryKeyValue);
}
resultSet=preparedStatement.executeQuery();
if(resultSet.next()==false)
{
resultSet.close();
preparedStatement.close();
throw new DataException("Invalid "+columnInfo.name+" : "+columnInfo.value);
}
resultSet.close();
preparedStatement.close();
}
if(field.isAnnotationPresent(ForeignKey.class))
{
columnInfo.isForeignKey=true;
an=field.getAnnotation(ForeignKey.class);
foreignKey=(ForeignKey)an;
columnInfo.parentTable=foreignKey.parent();
columnInfo.parentColumn=foreignKey.column();
preparedStatement=connection.prepareStatement("select * from "+columnInfo.parentTable+" where "+columnInfo.parentColumn+"=?");
if(columnInfo.value instanceof Integer)
{
preparedStatement.setInt(1,(Integer)columnInfo.value);
}
resultSet=preparedStatement.executeQuery();
if(resultSet.next()==false)
{
resultSet.close();
preparedStatement.close();
throw new DataException("Invalid "+columnInfo.name+" : "+columnInfo.value);
}
resultSet.close();
preparedStatement.close();
}
columnInfoList.add(columnInfo);
}
}
/*
for(ColumnInfo ci:columnInfoList)
{
System.out.println("Column name : "+ci.name);
System.out.println("Column name in table : "+ci.columnNameInTable);
System.out.println("Column value : "+ci.value);
}
*/
int i=1;
String statement="update "+tableName+" set ";
// title=?
for(ColumnInfo ci:columnInfoList)
{
if(ci.isPrimaryKey) continue;
i++;
statement+=ci.columnNameInTable;
statement+="=?";
if(i<columnInfoList.size()) statement+=",";
}
//  where code=? ";
statement+=" where "+primaryKeyName+"=?";
System.out.println("Statement : "+statement);
preparedStatement=connection.prepareStatement(statement);
Object value;
int e=1;
for(ColumnInfo ci:columnInfoList)
{
System.out.println("Table column name : "+ci.columnNameInTable);
System.out.println("Column value : "+ci.value);
if(ci.isPrimaryKey)
{
continue;
}
if(ci.value instanceof String)
{
preparedStatement.setString(e,(String)ci.value);
//System.out.println(value+" is String");
}
if(ci.value instanceof Integer)
{
preparedStatement.setInt(e,(Integer)ci.value);
}
if(ci.value instanceof java.util.Date)
{
java.util.Date utilDate=(java.util.Date)ci.value;
java.sql.Date sqlDate=new java.sql.Date(utilDate.getYear(),utilDate.getMonth(),utilDate.getDate());
preparedStatement.setDate(e,sqlDate);
}
if(ci.value instanceof Boolean)
{
preparedStatement.setBoolean(e,(Boolean)ci.value);
}
//preparedStatement.setString(e,ci.value);
e++;
}
if(primaryKeyValue instanceof Integer)
{
preparedStatement.setInt(columnInfoList.size(),(Integer)primaryKeyValue);
}
preparedStatement.executeUpdate();
} // isTable
}catch(Exception exception)
{
throw new DataException(exception.getMessage());
}
}
public void delete(Class c,Object value) throws DataException
{
try
{
boolean isTable=c.isAnnotationPresent(Table.class);
Column column;
Annotation an;
int code=0;
List<ColumnInfo> columnInfoList=new ArrayList<ColumnInfo>();
ColumnInfo columnInfo;
ForeignKey foreignKey;
String str;
PreparedStatement preparedStatement;
ResultSet resultSet;
DatabaseMetaData metaData;
String foreignKeyTableName;
String foreignKeyColumnName;
if(isTable)
{
an=c.getAnnotation(Table.class);
Table table=(Table)an;
String tableName=table.name();
//System.out.println("Class name : "+c.getName());
//System.out.println("Table name : "+tableName);
Field []fields=c.getDeclaredFields();
String primaryKeyName="";
String primaryKeyFieldName="";
Map<String,String> foreignKeyInfo=new HashMap<>();
for(Field field:fields)
{
if(field.isAnnotationPresent(Column.class) && field.isAnnotationPresent(PrimaryKey.class))
{
an=field.getAnnotation(Column.class);
column=(Column)an;
primaryKeyName=column.name();
primaryKeyFieldName=field.getName();
}
}

preparedStatement=connection.prepareStatement("select * from "+tableName+" where "+primaryKeyName+"=?");
if(value instanceof Integer)
{
preparedStatement.setInt(1,(Integer)value);
}
resultSet=preparedStatement.executeQuery();
if(resultSet.next()==false)
{
resultSet.close();
preparedStatement.close();
throw new DataException("Invalid "+primaryKeyFieldName+" : "+value);
}
resultSet.close();
preparedStatement.close();
metaData=connection.getMetaData();
resultSet=metaData.getExportedKeys(null,null,tableName);
while(resultSet.next())
{
foreignKeyTableName=resultSet.getString("FKTABLE_NAME");
foreignKeyColumnName=resultSet.getString("FKCOLUMN_NAME");
foreignKeyInfo.put(foreignKeyTableName,foreignKeyColumnName);
}
resultSet.close();
for(Map.Entry<String,String> entry:foreignKeyInfo.entrySet())
{
foreignKeyTableName=entry.getKey();
foreignKeyColumnName=entry.getValue();
preparedStatement=connection.prepareStatement("select * from "+foreignKeyTableName+" where "+foreignKeyColumnName+"=?");
if(value instanceof Integer)
{
preparedStatement.setInt(1,(Integer)value);
}
resultSet=preparedStatement.executeQuery();
if(resultSet.next())
{
resultSet.close();
preparedStatement.close();
throw new DataException("Cannot delete "+tableName+" with "+primaryKeyFieldName+" : "+value+" as it is allocated to "+foreignKeyTableName);
}
resultSet.close();
preparedStatement.close();
}
preparedStatement=connection.prepareStatement("delete from "+tableName+" where "+primaryKeyName+"=?");
if(value instanceof Integer)
{
preparedStatement.setInt(1,(Integer)value);
}
preparedStatement.executeUpdate();
preparedStatement.close();
} // isTable
}catch(Exception exception)
{
throw new DataException(exception.getMessage());
}
}
public DataManager query(Class c) throws DataException
{
if(connection==null) throw new DataException("Connection is not established");
if(c==null) throw new DataException("Class reference is required");
if(c.isAnnotationPresent(Table.class)==false) throw new DataException("Specified class does not represent a table");
Annotation an=c.getAnnotation(Table.class);
Table table=(Table)an;
String tableName=table.name();
this.queryString="select * from "+tableName+" ";
this.c=c;
return this;
}
public DataManager where(String clause) throws DataException
{
if(connection==null) throw new DataException("Connection is not established");
if(this.c==null) throw new DataException("Class reference is required");
if(this.queryString.length()==0) throw new DataException("Invalid query");
Field []fields=this.c.getDeclaredFields();
Annotation an;
Column column;
String name;
boolean found=false;
for(Field field:fields)
{
if(field.isAnnotationPresent(Column.class))
{
an=field.getAnnotation(Column.class);
column=(Column)an;
name=column.name();
if(clause.equalsIgnoreCase(name))
{
found=true;
break;
}
}
}
if(found==false) throw new DataException("Invalid column name is specified in where clause");
this.queryString+="where "+clause;
return this;
}
public DataManager gt(int num) throws DataException
{
if(connection==null) throw new DataException("Connection is not established");
if(this.c==null) throw new DataException("Class reference is required");
if(this.queryString.length()==0) throw new DataException("Invalid query");
this.queryString+=">?";
if(this.conditions==null) conditions=new ArrayList<Object>();
this.conditions.add(num);
return this;
}
public DataManager lt(int num) throws DataException
{
if(connection==null) throw new DataException("Connection is not established");
if(this.c==null) throw new DataException("Class reference is required");
if(this.queryString.length()==0) throw new DataException("Invalid query");
this.queryString+="<?";
if(this.conditions==null) conditions=new ArrayList<Object>();
this.conditions.add(num);
return this;
}
public DataManager ge(int num) throws DataException
{
if(connection==null) throw new DataException("Connection is not established");
if(this.c==null) throw new DataException("Class reference is required");
if(this.queryString.length()==0) throw new DataException("Invalid query");
this.queryString+=">=?";
if(this.conditions==null) conditions=new ArrayList<Object>();
this.conditions.add(num);
return this;
}
public DataManager le(int num) throws DataException
{
if(connection==null) throw new DataException("Connection is not established");
if(this.c==null) throw new DataException("Class reference is required");
if(this.queryString.length()==0) throw new DataException("Invalid query");
this.queryString+="<=?";
if(this.conditions==null) conditions=new ArrayList<Object>();
this.conditions.add(num);
return this;
}
public DataManager eq(Object val) throws DataException
{
if(connection==null) throw new DataException("Connection is not established");
if(this.c==null) throw new DataException("Class reference is required");
if(this.queryString.length()==0) throw new DataException("Invalid query");
this.queryString+="=?";
if(this.conditions==null) conditions=new ArrayList<Object>();
this.conditions.add(val);
return this;
}

public DataManager ne(Object val) throws DataException
{
if(connection==null) throw new DataException("Connection is not established");
if(this.c==null) throw new DataException("Class reference is required");
if(this.queryString.length()==0) throw new DataException("Invalid query");
this.queryString+="<>?";
if(this.conditions==null) conditions=new ArrayList<Object>();
this.conditions.add(val);
return this;
}

public DataManager and(String clause) throws DataException
{
if(connection==null) throw new DataException("Connection is not established");
if(this.c==null) throw new DataException("Class reference is required");
if(this.queryString.length()==0) throw new DataException("Invalid query");
Field []fields=this.c.getDeclaredFields();
Annotation an;
Column column;
String name;
boolean found=false;
for(Field field:fields)
{
if(field.isAnnotationPresent(Column.class))
{
an=field.getAnnotation(Column.class);
column=(Column)an;
name=column.name();
if(clause.equalsIgnoreCase(name))
{
found=true;
break;
}
}
}
if(found==false) throw new DataException("Invalid column name is specified in where clause");
this.queryString+=" and "+clause;
return this;
}
public DataManager or(String clause) throws DataException
{
if(connection==null) throw new DataException("Connection is not established");
if(this.c==null) throw new DataException("Class reference is required");
if(this.queryString.length()==0) throw new DataException("Invalid query");
Field []fields=this.c.getDeclaredFields();
Annotation an;
Column column;
String name;
boolean found=false;
for(Field field:fields)
{
if(field.isAnnotationPresent(Column.class))
{
an=field.getAnnotation(Column.class);
column=(Column)an;
name=column.name();
if(clause.equalsIgnoreCase(name))
{
found=true;
break;
}
}
}
if(found==false) throw new DataException("Invalid column name is specified in where clause");

this.queryString+=" or "+clause;
return this;
}


public Object fire() throws DataException
{
List list=new LinkedList<>();
if(connection==null) throw new DataException("Connection is not established");
try
{
System.out.println("Query string : "+this.queryString);
if(this.queryString==null || this.queryString.length()==0) throw new DataException("Create query    to fire sql statement");
PreparedStatement preparedStatement;
preparedStatement=connection.prepareStatement(this.queryString);
if(this.conditions!=null)
{
int e=1;
for(Object value:this.conditions)
{
if(value instanceof Integer)
{
preparedStatement.setInt(e,(Integer)value);
}
if(value instanceof String)
{
preparedStatement.setString(e,(String)value);
}
e++;
}
}
ResultSet resultSet;
resultSet=preparedStatement.executeQuery();
Object obj;
Field []fields=c.getDeclaredFields();
Column column;
String fieldName;
String tmp;
String setterName;
Method method;
Class parameters[];
Object arguments[];
Annotation an;
Object value=null;
String tableColumnName;
Class fieldClass;

while(resultSet.next())
{
obj=this.c.newInstance();
for(Field field:fields)
{
if(field.isAnnotationPresent(Column.class))
{
an=field.getAnnotation(Column.class);
column=(Column)an;
tableColumnName=column.name();
fieldClass=field.getType();
System.out.println("Field class : "+fieldClass);
fieldName=field.getName();
tmp=fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
setterName="set"+tmp;
parameters=new Class[1];
parameters[0]=fieldClass;
method=c.getMethod(setterName,parameters);
if(fieldClass==Integer.class || fieldClass==int.class)
{
value=resultSet.getInt(tableColumnName);
}
if(fieldClass==String.class)
{
value=resultSet.getString(tableColumnName);
}
if(fieldClass==java.util.Date.class)
{
java.sql.Date sqlDate=resultSet.getDate(tableColumnName);
java.util.Date utilDate=new java.util.Date(sqlDate.getYear(),sqlDate.getMonth(),sqlDate.getDate());
value=utilDate;
}
arguments=new Object[1];
arguments[0]=value;
try
{
method.invoke(obj,value);
}catch(InvocationTargetException ite)
{
System.out.println("ite.getCause() : "+ite.getCause());
throw new DataException(ite.getCause().getMessage());
}
} // if condition for column annotation on field
} // loop for fields
list.add(obj);
}// loop for resultSet

resultSet.close();
preparedStatement.close();
this.queryString="";
this.c=null;
this.conditions=null;
}catch(Exception exception)
{
throw new DataException(exception.getMessage());
}
return list;
}

}
class eg4psp
{
public static void main(String gg[])
{
DataManager dm=DataManager.getDataManager();
List<Student> students;
try
{
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
