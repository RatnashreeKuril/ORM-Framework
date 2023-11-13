package com.thinking.machines.orm.dataManager;
import java.lang.reflect.*;
class ColumnInfo
{
public String name;
public Field field;
public Method setter;
public Method getter;
public boolean isAutoIncrement;
public boolean isPrimaryKey;
public boolean isForeignKey;
public String parentTable;
public String parentColumn;
}