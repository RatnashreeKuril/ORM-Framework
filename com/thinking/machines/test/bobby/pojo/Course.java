package com.thinking.machines.test.bobby.pojo;
@Table(name="course")
public class Course implements java.io.Serializable
{
@AutoIncrement
@PrimaryKey
@Column(name="code")
private int code;
@Column(name="title")
private java.lang.String title;
public Course()
{
this.code=0;
this.title="";
}
public void setCode(int code)
{
this.code=code;
}
public int getCode()
{
return this.code;
}
public void setTitle(java.lang.String title)
{
this.title=title;
}
public java.lang.String getTitle()
{
return this.title;
}
}
