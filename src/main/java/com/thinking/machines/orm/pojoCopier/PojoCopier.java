package com.thinking.machines.orm.pojoCopier;
import com.thinking.machines.orm.exceptions.*;
import java.lang.reflect.*;
public class PojoCopier
{
public static void copy(Object target,Object source) throws DataException
{
try
{
if(source==null) throw new DataException("Source object required");
if(target==null) throw new DataException("Target object required");
Class sourceClass=source.getClass();
Class targetClass=target.getClass();
if(sourceClass!=targetClass) throw new DataException("Types of source and target objects does not match");
if(sourceClass==int.class || sourceClass==Integer.class) throw new DataException("Method cannot be applied on primitive data types");
if(sourceClass==byte.class || sourceClass==Byte.class) throw new DataException("Method cannot be applied on primitive data types");
if(sourceClass==short.class || sourceClass==Short.class) throw new DataException("Method cannot be applied on primitive data types");
if(sourceClass==long.class || sourceClass==Long.class) throw new DataException("Method cannot be applied on primitive data types");
if(sourceClass==double.class || sourceClass==Double.class) throw new DataException("Method cannot be applied on primitive data types");
if(sourceClass==float.class || sourceClass==Float.class) throw new DataException("Method cannot be applied on primitive data types");
if(sourceClass==char.class || sourceClass==Character.class) throw new DataException("Method cannot be applied on primitive data types");
if(sourceClass==String.class) throw new DataException("Method cannot be applied on String");
Field fields[]=sourceClass.getDeclaredFields();
String fieldName,tmp,getterName,setterName;
Method getter,setter;
Object value,arguments[];
Class returnType,parameters[];

for(Field field:fields)
{
fieldName=field.getName();
if(fieldName.charAt(0)>=65 && fieldName.charAt(0)<=122)
{
tmp=fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
}
else
{
tmp=fieldName;
}
getterName="get"+tmp;
try
{
getter=sourceClass.getMethod(getterName);
}catch(NoSuchMethodException noSuchMethodException)
{
throw new DataException("No suitable getter method found in "+sourceClass.getName()+" class for field : "+field.getName());
}
returnType=getter.getReturnType();
parameters=new Class[1];
parameters[0]=returnType;
setterName="set"+tmp;
try
{
setter=targetClass.getMethod(setterName,parameters);
}catch(NoSuchMethodException noSuchMethodException)
{
throw new DataException("No suitable setter method found in "+targetClass.getName()+" class for field : "+field.getName());
}
try
{
value=getter.invoke(source);
}catch(InvocationTargetException invocationTargetException)
{
throw new DataException(invocationTargetException.getCause().getMessage());
}
arguments=new Object[1];
arguments[0]=value;
try
{
setter.invoke(target,arguments);
}catch(InvocationTargetException invocationTargetException)
{
throw new DataException(invocationTargetException.getCause().getMessage());
}
}


}catch(Exception exception)
{
throw new DataException(exception.getMessage());
}
}
}