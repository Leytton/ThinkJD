package com.llqqww.thinkjdbc;

public class FieldInfo{
	private String name="";
	private boolean isKey=false;
	private Boolean isAutoInc=null;
	private boolean isColumn=true;
	private Object valueObj;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isKey() {
		return isKey;
	}
	public void setKey(boolean isKey) {
		this.isKey = isKey;
	}
	public Boolean isAutoInc() {
		return isAutoInc;
	}
	public void setAutoInc(boolean isAutoInc) {
		this.isAutoInc = isAutoInc;
	}
	public boolean isColumn() {
		return isColumn;
	}
	public void setColumn(boolean isColumn) {
		this.isColumn = isColumn;
	}
	public Object getValueObj() {
		return valueObj;
	}
	public void setValueObj(Object valueObj) {
		this.valueObj = valueObj;
	}
}
