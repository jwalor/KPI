package org.apache.storm.jdbc.mapper;

import java.util.List;
import java.util.Map;

import org.apache.storm.jdbc.common.Column;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.ITuple;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings("rawtypes")
public interface KpiLookupMapper extends JdbcLookupMapper {


    /**
     * declare what are the fields that this code will output.
     * @param declarer
     */
    void declareOutputFields(String streamId , OutputFieldsDeclarer declarer);
    
    /**
     * 
     * @param tuple
     * @return Map with values
     */
	public Map convertTupleToMap(ITuple tuple);
	
	/**
	 * 
	 * @param input
	 * @param columns
	 * @return
	 */
	public Map convertColumnsToMap(List<Column> columns) ;
}
