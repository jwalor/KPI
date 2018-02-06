package org.apache.storm.bolt;

import org.apache.storm.jdbc.bolt.LogicKpiBolt;
import org.apache.storm.topology.base.BaseTickTupleAwareRichBolt;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jalor
 *
 */
public class LogicKpiBoltTest {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8783495568069984732L;

	private static final Logger LOG = LoggerFactory.getLogger(LogicKpiBoltTest.class);
	
    
   @Test
    public void testSimple() {
	   
	   BaseTickTupleAwareRichBolt LogicKpiBolt = new LogicKpiBolt();
	   
   }
}
