import com.google.gson.*;
import java.io.*;
import java.sql.*;

class eg3psp
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
DatabaseMetaData metaData=connection.getMetaData();

String table[]={"TABLE"};
ResultSet rs=metaData.getTables(null,null,null,table);
String tableName;
ResultSet columns;
ResultSet primaryKeys;
ResultSet foreignKeys;
String columnType;
String columnClass;
while(rs.next())
{
System.out.println(rs.getString(3));
tableName=rs.getString(3);
columns=metaData.getColumns(null,null,tableName,null);

while(columns.next())
{
System.out.println("Column name : "+columns.getString("column_name"));
/*
System.out.println("Column size : "+columns.getString("column_size"));
System.out.println("Data Type : "+columns.getString("data_type"));
System.out.println("Is nullable : "+columns.getString("is_nullable"));
System.out.println("Is auto-increment: "+columns.getString("is_autoincrement"));
*/
columnType=getJDBCType(columns.getInt("data_type"));
columnClass=getColumnClass(columnType);
System.out.println(columnClass);
}
columns.close();
foreignKeys=metaData.getImportedKeys(null,null,tableName);
while(foreignKeys.next())
{
System.out.println("PK table name : "+foreignKeys.getString("pktable_name"));
System.out.println("Fk table name : "+foreignKeys.getString("fktable_name"));
System.out.println("PK column name : "+foreignKeys.getString("pkcolumn_name"));
System.out.println("FK column name : "+foreignKeys.getString("fkcolumn_name"));
}
foreignKeys.close();
}
rs.close();


connection.close();
System.out.println("Done");
}catch(Exception e)
{
System.out.println(e);
}
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

}