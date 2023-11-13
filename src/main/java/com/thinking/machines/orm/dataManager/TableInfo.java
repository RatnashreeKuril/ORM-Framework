package com.thinking.machines.orm.dataManager;
import java.util.*;
public class TableInfo
{
public String name;
public Class c;
public List<ColumnInfo> columnInfoList;
public ColumnInfo primaryKey;
public List<ColumnInfo> foreignKeys;
public Map<String,List<ExportedKeyInfo>> exportedKeysMap;
public boolean isCacheable;
public String add;
public String update;
public String delete;
public String getAll;
}