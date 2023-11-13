import com.google.gson.*;
import java.io.*;
import java.sql.*;
import java.lang.reflect.*;
import java.util.*;
class ColumnInfo
{
public String name;
public boolean isAutoIncrement;
public boolean isPrimaryKey;
public boolean isForeignKey;
public String columnClass;
public String columnNameInTable;
public String parentTable;
public String parentColumn;
public Object value;
}
class eg2psp
{
public static void main(String gg[])
{
try
{
FileReader fileReader=new FileReader("conf.json");
Gson gson=new Gson();
DBInfo dbInfo=gson.fromJson(fileReader,DBInfo.class);

Class.forName(dbInfo.jdbcDriver);
Connection connection=DriverManager.getConnection(dbInfo.connectionUrl,dbInfo.userName,dbInfo.password);
String packageName=dbInfo.packageName;
String jarName=dbInfo.jarName;
DatabaseMetaData metaData=connection.getMetaData();
String table[]={"TABLE"};
ResultSet rs=metaData.getTables(null,null,null,table);
String tableName;
ResultSet columns;
ResultSet primaryKeys;
ResultSet foreignKeys;
File file;
RandomAccessFile randomAccessFile;
String fileName,tmp,str;
String className,classFileName;
List<String> list;
String columnName;
boolean isPrimaryKey;
boolean isAutoIncrement;
String columnDataType;
List<ColumnInfo> columnInfoList;
ColumnInfo columnInfo;
String columnType;
String columnClass;
String filePath=packageName.replace(".",File.separator);
System.out.println(filePath);
File dir=new File(filePath);
int x;
if(!(dir.exists()))
{
System.out.println("Is package created : "+dir.mkdirs());
}
while(rs.next())
{
System.out.println(rs.getString(3));
tableName=rs.getString(3);
className=tableName.substring(0,1).toUpperCase()+tableName.substring(1);
fileName=className+".java";
System.out.println("File name : "+fileName);
list=new ArrayList<>();
list.add("package "+packageName+";");
list.add("import com.thinking.machines.orm.annotations.*;");
list.add("@Table(name=\""+tableName+"\")");
list.add("public class "+className+" implements java.io.Serializable");
list.add("{");
columnInfoList=new ArrayList<>();
columns=metaData.getColumns(null,null,tableName,null);
while(columns.next())
{
/*
System.out.println("Column name : "+columns.getString("column_name"));
System.out.println("Column size : "+columns.getString("column_size"));
System.out.println("Data Type: "+columns.getString("data_type"));
System.out.println("Is nullable: "+columns.getString("is_nullable"));
System.out.println("Is auto-increment: "+columns.getString("is_autoincrement"));
*/
columnName=columns.getString("column_name");
columnType=getJDBCType(columns.getInt("data_type"));
columnClass=getColumnClass(columnType);
isAutoIncrement=false;
if(columns.getString("is_autoincrement").equals("NO")) isAutoIncrement=false;
else if(columns.getString("is_autoincrement").equals("YES")) isAutoIncrement=true;
columnInfo=new ColumnInfo();
columnInfo.name=toCamelCase(columnName);
columnInfo.isAutoIncrement=isAutoIncrement;
columnInfo.columnClass=columnClass;
columnInfo.columnNameInTable=columnName;
columnInfoList.add(columnInfo);
}
columns.close();

columns=metaData.getPrimaryKeys(null,null,tableName);
while(columns.next())
{
/*
System.out.println("Table cat : "+columns.getString(1));
System.out.println("Table schem : "+columns.getString(2));
System.out.println("Table name : "+columns.getString(3));
System.out.println("Column name : "+columns.getString(4));
System.out.println("Key seq : "+columns.getString(5));
System.out.println("PK_NAME : "+columns.getString(6));
*/
columnName=columns.getString("column_name");
for(ColumnInfo ci:columnInfoList)
{
if(ci.columnNameInTable.equals(columnName))
{
ci.isPrimaryKey=true;
}
}
}
columns.close();


columns=metaData.getImportedKeys(null,null,tableName);
while(columns.next())
{
/*
System.out.println("PK table name : "+columns.getString("pktable_name"));
System.out.println("Fk table name : "+columns.getString("fktable_name"));
System.out.println("PK column name : "+columns.getString("pkcolumn_name"));
System.out.println("FK column name : "+columns.getString("fkcolumn_name"));
*/
columnName=columns.getString("fkcolumn_name");
for(ColumnInfo ci:columnInfoList)
{
if(ci.columnNameInTable.equals(columnName))
{
ci.isForeignKey=true;
ci.parentTable=columns.getString("pktable_name");
ci.parentColumn=columns.getString("pkcolumn_name");
}
}

}
columns.close();

for(ColumnInfo ci:columnInfoList)
{
/*
System.out.println("Column name : "+ci.name);
System.out.println("Is auto increment : "+ci.isAutoIncrement);
System.out.println("Is primary key : "+ci.isPrimaryKey);
System.out.println("Column class : "+ci.columnClass);
*/
if(ci.isAutoIncrement)
{
list.add("@AutoIncrement");
}
if(ci.isPrimaryKey)
{
list.add("@PrimaryKey");
}
if(ci.isForeignKey)
{
list.add("@ForeignKey(parent=\""+ci.parentTable+"\",column=\""+ci.parentColumn+"\")");
}
list.add("@Column(name=\""+ci.columnNameInTable+"\")");
list.add("private "+ci.columnClass+" "+ci.name+";");

}

// constructor 
list.add("public "+className+"()");
list.add("{");

for(ColumnInfo ci:columnInfoList)
{
/*
System.out.println("Column name : "+ci.name);
System.out.println("Is auto increment : "+ci.isAutoIncrement);
System.out.println("Is primary key : "+ci.isPrimaryKey);
System.out.println("Column class : "+ci.columnClass);
*/
list.add("this."+ci.name+"="+getDefaultValue(ci.columnClass)+";");
}

list.add("}");
// constructor 


// setter getter

for(ColumnInfo ci:columnInfoList)
{
tmp=ci.name.substring(0,1).toUpperCase()+ci.name.substring(1);
list.add("public void set"+tmp+"("+ci.columnClass+" "+ci.name+")");
list.add("{");
list.add("this."+ci.name+"="+ci.name+";");
list.add("}");

list.add("public "+ci.columnClass+" get"+tmp+"()");
list.add("{");
list.add("return this."+ci.name+";");
list.add("}");
}
// setter getter



list.add("}");

// writing in file
System.out.println("File name with class path : "+filePath+File.separator+fileName);
file=new File("src"+File.separator+filePath+File.separator+fileName);
if(file.exists()) file.delete();
randomAccessFile=new RandomAccessFile(file,"rw");
for(String s:list)
{
randomAccessFile.writeBytes(s+"\r\n");

}
randomAccessFile.close();
// writing in file


} // loop for table ends here 
rs.close();
connection.close();
// creating jar file
Runtime runtime=Runtime.getRuntime();


System.out.println("javac -d classes -classpath lib"+File.separator+"orm.jar;. src"+File.separator+filePath+File.separator+"*.java");
Process process;
process=runtime.exec("javac -d classes -classpath lib"+File.separator+"orm.jar;. src"+File.separator+filePath+File.separator+"*.java");
int exitCode=process.waitFor();
if(exitCode==0) System.out.println("Java file complied successfully");
else System.out.println("Unable to compile java files");
x=packageName.indexOf(".");
tmp=packageName.substring(0,x);

System.out.println("Jar file creation : "+"jar -cvf dist"+File.separator+jarName+" -C classes"+File.separator+" .");

process=runtime.exec("jar -cvf dist"+File.separator+jarName+" -C classes"+File.separator+" .");
exitCode=process.waitFor();

if(exitCode==0)
{
System.out.println("Jar file created");
}
else
{
System.out.println("Error in creating jar file");
}

// creating jar file

System.out.println("Done");
}catch(Exception e)
{
System.out.println(e);
}
}
private static String toCamelCase(String name)
{
StringBuffer sb=new StringBuffer();
int sp=0;
int ep=name.indexOf('_');
if(ep==-1) ep=name.length();
sb.append(name.substring(sp,ep));
while(ep<name.length())
{
sp=ep+1;
ep=name.indexOf('_',sp);
if(ep==-1) ep=name.length();
sb.append(name.substring(sp,sp+1).toUpperCase()+name.substring(sp+1,ep));
}
String result=sb.toString();
System.out.println("Field name : "+result);
return result;

}
public static String getJDBCType(int value)
{
JDBCType jdbcType=JDBCType.valueOf(value);
return jdbcType.getName();
}
public static String getColumnClass(String columnType)
{
if(columnType.equals("DATE"))
{
return "java.util.Date";
}
if(columnType.equals("DECIMAL"))
{
return "java.util.BigDecimal";
}
if(columnType.equals("DOUBLE"))
{
return "double";
}
if(columnType.equals("BOOLEAN"))
{
return "boolean";
}
if(columnType.equals("CHAR"))
{
return "java.lang.String";
}
if(columnType.equals("FLOAT"))
{
return "float";
}
if(columnType.equals("INTEGER"))
{
return "int";
}
return "";
}
private static String getDefaultValue(String className)
{
if(className.equals("int")) return "0";
if(className.equals("boolean")) return "false";
if(className.equals("java.lang.String")) return "\"\"";
return "null";
}
}