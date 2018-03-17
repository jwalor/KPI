package org.apache.storm.util;

import java.util.List;

import org.apache.storm.task.GeneralTopologyContext;
import org.apache.storm.tuple.Fields;

/**
 * 
 * @author jalor
 *
 */
public class KpiTuple {

	private List<Object> 			values;
	private Fields 					outputFields;
	
	public KpiTuple( List<Object> values , Fields outputFields) {
		this.values	= values;
		this.outputFields = outputFields;
	}
	
	
	public int size() {
        return values.size();
    }
	
    public Object getValue(int i) {
        return values.get(i);
    }

    public String getString(int i) {
        return (String) values.get(i);
    }

    public Integer getInteger(int i) {
        return (Integer) values.get(i);
    }

    public Long getLong(int i) {
        return (Long) values.get(i);
    }

    public Boolean getBoolean(int i) {
        return (Boolean) values.get(i);
    }

    public Short getShort(int i) {
        return (Short) values.get(i);
    }

    public Byte getByte(int i) {
        return (Byte) values.get(i);
    }

    public Double getDouble(int i) {
        return (Double) values.get(i);
    }

    public Float getFloat(int i) {
        return (Float) values.get(i);
    }

    public byte[] getBinary(int i) {
        return (byte[]) values.get(i);
    }
    
    
    public Object getValueByField(String field) {
        return values.get(fieldIndex(field));
    }

    public String getStringByField(String field) {
        return (String) values.get(fieldIndex(field));
    }

    public Integer getIntegerByField(String field) {
        return (Integer) values.get(fieldIndex(field));
    }

    public Long getLongByField(String field) {
        return (Long) values.get(fieldIndex(field));
    }

    public Boolean getBooleanByField(String field) {
        return (Boolean) values.get(fieldIndex(field));
    }

    public Short getShortByField(String field) {
        return (Short) values.get(fieldIndex(field));
    }

    public Byte getByteByField(String field) {
        return (Byte) values.get(fieldIndex(field));
    }

    public Double getDoubleByField(String field) {
        return (Double) values.get(fieldIndex(field));
    }

    public Float getFloatByField(String field) {
        return (Float) values.get(fieldIndex(field));
    }

    public byte[] getBinaryByField(String field) {
        return (byte[]) values.get(fieldIndex(field));
    }
    
    public List<Object> getValues() {
        return values;
    }
	
	 public int fieldIndex(String field) {
	        return getFields().fieldIndex(field);
	 }
	 
	 public boolean contains(String field) {
	        return getFields().contains(field);
	 }
	 
	 public Fields getFields() {
	        return outputFields;
	 }

	public Fields getOutputFields() {
		return outputFields;
	}

	public void setOutputFields(Fields outputFields) {
		this.outputFields = outputFields;
	}

	public void setValues(List<Object> values) {
		this.values = values;
	}
	 
    @Override
    public String toString() {
        return "values : " +  values.toString();
    }
    
    @Override
    public boolean equals(Object other) {
        return this == other;
    }    
    
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    } 
}
