package org.apache.storm.jdbc.mapper;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.storm.jdbc.bolt.KpiComparatorBolt;
import org.apache.storm.jdbc.common.Column;
import org.apache.storm.jdbc.common.Util;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.ITuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pradera.stream.model.CustomColumn;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class KpiJdbcLookupMapper extends SimpleJdbcLookupMapper implements KpiLookupMapper {

	private static final Logger LOG = LoggerFactory.getLogger(KpiJdbcLookupMapper.class);
	private static final long serialVersionUID = -5940542251870147281L;

	/**
	 *  Both attributes  should be equals over your values !
	 */
	private Fields 			outputFields;
	private List<Column> 	outputColumnFields;

	public KpiJdbcLookupMapper(Fields outputFields, List<Column> queryColumns) {
		super(outputFields, queryColumns);
		this.outputFields = outputFields;
	}

	@Override
	public List<Values> toTuple(ITuple input, List<Column> columns) {
		Values values = new Values();

		for(String field : outputFields) {
			if(input.contains(field)) {
				values.add(input.getValueByField(field));
			} else {
				for(Column column : columns) {
					if(column.getColumnName().equalsIgnoreCase(field)) {
						values.add(column.getVal());
					}
				}
			}
		}
		List<Values> result = new ArrayList<Values>();
		result.add(values);
		return result;
	}

	@Override
	public Map convertColumnsToMap(List<Column> columns) {
		Map<String,Object> map = new HashMap<String,Object>();

		for(Column outputColumn : this.outputColumnFields) {

			for(Column column : columns) {
				if(column.getColumnName().equalsIgnoreCase(outputColumn.getColumnName())) {
					((CustomColumn)column).setShouldCompare(((CustomColumn)outputColumn).getShouldCompare());
					map.put(outputColumn.getColumnName(), column);
				}
			}
		}


		return map;
	}


	@Override
	public Map convertTupleToMap(ITuple tuple) {
		Map<String,Object> map = new HashMap<String,Object>();



		for(Column column : this.outputColumnFields) {

			try {

				String columnName = column.getColumnName();
				Integer columnSqlType = column.getSqlType();

				if(Util.getJavaType(columnSqlType).equals(String.class)) {
					String value = tuple.getStringByField(columnName);
					map.put(columnName, new CustomColumn(columnName, value, columnSqlType ,  ((CustomColumn)column).getShouldCompare()) );
				} else if(Util.getJavaType(columnSqlType).equals(Short.class)) {
					Short value = tuple.getShortByField(columnName);
					map.put(columnName, new CustomColumn(columnName, value, columnSqlType,  ((CustomColumn)column).getShouldCompare()));
				} else if(Util.getJavaType(columnSqlType).equals(Integer.class)) {
					Integer value = tuple.getIntegerByField(columnName);
					map.put(columnName, new CustomColumn(columnName, value, columnSqlType,  ((CustomColumn)column).getShouldCompare()));
				} else if(Util.getJavaType(columnSqlType).equals(Long.class)) {
					Long value = tuple.getLongByField(columnName);
					map.put(columnName, new CustomColumn(columnName, value, columnSqlType,  ((CustomColumn)column).getShouldCompare()));
				} else if(Util.getJavaType(columnSqlType).equals(Double.class)) {
					Double value = tuple.getDoubleByField(columnName);
					map.put(columnName, new CustomColumn(columnName, value, columnSqlType,  ((CustomColumn)column).getShouldCompare()));
				} else if(Util.getJavaType(columnSqlType).equals(Float.class)) {
					Float value = tuple.getFloatByField(columnName);
					map.put(columnName, new CustomColumn(columnName, value, columnSqlType,  ((CustomColumn)column).getShouldCompare()));
				} else if(Util.getJavaType(columnSqlType).equals(Boolean.class)) {
					Boolean value = tuple.getBooleanByField(columnName);
					map.put(columnName, new CustomColumn(columnName, value, columnSqlType,  ((CustomColumn)column).getShouldCompare()));
				} else if(Util.getJavaType(columnSqlType).equals(byte[].class)) {
					byte[] value = tuple.getBinaryByField(columnName);
					map.put(columnName, new CustomColumn(columnName, value, columnSqlType,  ((CustomColumn)column).getShouldCompare()));
				} else if(Util.getJavaType(columnSqlType).equals(Date.class)) {
					Date value = (Date)tuple.getValueByField(columnName);
					map.put(columnName, new CustomColumn(columnName, new Date(value.getTime()), columnSqlType,  ((CustomColumn)column).getShouldCompare()));
				} else if(Util.getJavaType(columnSqlType).equals(Time.class)) {
					Time value = (Time)tuple.getValueByField(columnName);
					map.put(columnName, new CustomColumn(columnName, new Time(value.getTime()), columnSqlType,  ((CustomColumn)column).getShouldCompare()));
				} else if(Util.getJavaType(columnSqlType).equals(Timestamp.class)) {
					Timestamp value = (Timestamp)tuple.getValueByField(columnName);
					map.put(columnName, new CustomColumn(columnName, new Timestamp(value.getTime()), columnSqlType,  ((CustomColumn)column).getShouldCompare()));
				}
			}catch (Exception e) {
				if (!(e  instanceof IllegalArgumentException)) {
					LOG.info("" +e.getStackTrace());
				}
			}
		}

		return map;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(outputFields);
	}

	@Override
	public void declareOutputFields(String streamId, OutputFieldsDeclarer declarer) {
		declarer.declareStream(streamId,outputFields);
	}

	public List<Column> getOutputColumnFields() {
		return outputColumnFields;
	}

	public void setOutputColumnFields(List<Column> outputColumnFields) {
		this.outputColumnFields = outputColumnFields;
	}

	public Fields getOutputFields() {
		return outputFields;
	}

	public void setOutputFields(Fields outputFields) {
		this.outputFields = outputFields;
	}



}
