package com.pradera.stream.model;

import org.apache.storm.jdbc.common.Column;

/**
 * 
 * @author jalor
 *
 * @param <T>
 */
@SuppressWarnings({ "serial", "rawtypes" , "unchecked" })
public class CustomColumn<T> extends Column{
	
	/**
	 *  Default Value
	 */
	private Boolean shouldCompare = Boolean.FALSE;
	
	public CustomColumn(String columnName, int sqlType) {
		super(columnName, sqlType);
	}
	
	public CustomColumn(String columnName, T val, int sqlType) {
		super(columnName, val , sqlType);
    }
	
	public CustomColumn(String columnName, T val, int sqlType , Boolean shouldCompare) {
		super(columnName, val , sqlType);
		this.shouldCompare	= shouldCompare;
	}
	
	public CustomColumn(String columnName , int sqlType , Boolean shouldCompare) {
		super(columnName, sqlType);
		this.shouldCompare	= shouldCompare;
	}
	
	public Boolean getShouldCompare() {
		return shouldCompare;
	}
	public void setShouldCompare(Boolean shouldCompare) {
		this.shouldCompare = shouldCompare;
	}
	
	 
    public boolean equalsField(Object o) {
    	
        if (this == o) return true;
        if (!(o instanceof Column)) return false;
        Column<?> column = (Column<?>) o;
        if (getSqlType() != column.getSqlType()) return false;
        return getColumnName() != null ? getColumnName().equalsIgnoreCase(column.getColumnName()) : column.getColumnName() == null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Column)) return false;

        Column<?> column = (Column<?>) o;

        if (getSqlType() != column.getSqlType()) return false;
        if (!getColumnName().equalsIgnoreCase(column.getColumnName())) return false;
        return getVal() != null ? getVal() .equals(column.getVal() ) : column.getVal()  == null;

    }
    
    @Override
    public String toString() {
        return "Column{" +
                "columnName='" + getColumnName() + '\'' +
                ", val=" + getVal() +
                ", sqlType=" + getSqlType() +
                ", shouldCompare=" + this.shouldCompare +
                '}';
    }
}
