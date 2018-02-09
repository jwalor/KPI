package org.apache.storm.jdbc.bolt;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.apache.storm.Config;
import org.apache.storm.jdbc.common.Column;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.mapper.JdbcLookupMapper;
import org.apache.storm.jdbc.mapper.KpiJdbcLookupMapper;
import org.apache.storm.jdbc.mapper.SimpleJdbcMapper;
import org.apache.storm.mongodb.common.MongoDBClient;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.fest.assertions.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.mongodb.DBRef;
import com.mongodb.client.model.Filters;
import com.pradera.stream.constant.Constant;
import com.pradera.stream.model.CustomColumn;
import com.pradera.stream.model.OperationPayload;
import com.pradera.stream.singleton.HikariCPConnectionSingletonSource;
import com.pradera.stream.singleton.HikariCPConnectionSingletonTarget;
import com.pradera.stream.util.DateUtil;
import com.pradera.stream.util.jdbc.JdbcClientExt;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class UpsertKpiBolt extends JdbcInsertBolt {

	/**
	 * 
	 */

	private static final long serialVersionUID = -8783495568069984732L;
	private static final Logger LOG = LoggerFactory.getLogger(UpsertKpiBolt.class);
	private Map<String, String> mapInsert;
	private JdbcLookupMapper jdbcMapper;
	Object map;

	public UpsertKpiBolt(JdbcLookupMapper jdbcMapper, Map mapUpser) {

		super(HikariCPConnectionSingletonTarget.getInstance(), jdbcMapper);
		Validate.notNull(mapUpser);
		this.mapInsert = mapUpser;
		this.jdbcMapper = jdbcMapper;
	}

	@Override
	public void prepare(Map stormConf, TopologyContext topologyContext, OutputCollector collector) {

		this.collector = collector;
		LOG.info("Setting dinamycs  operations  on UpsertKpiBolt");

		if (queryTimeoutSecs == null) {
			queryTimeoutSecs = Integer.parseInt(stormConf.get(Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS).toString());
		}

		String user = (stormConf.get(Constant.SettingMongo.USER) == null
				|| StringUtils.isEmpty(stormConf.get(Constant.SettingMongo.USER).toString())) ? null
						: stormConf.get(Constant.SettingMongo.USER).toString();

		String password = (stormConf.get(Constant.SettingMongo.PASSWORD) == null
				|| StringUtils.isEmpty(stormConf.get(Constant.SettingMongo.PASSWORD).toString())) ? null
						: stormConf.get(Constant.SettingMongo.PASSWORD).toString();

		String host = stormConf.get(Constant.SettingMongo.HOST).toString();
		int port = Integer.parseInt(stormConf.get(Constant.SettingMongo.PORT).toString());
		String dbName = stormConf.get(Constant.SettingMongo.DB).toString();
		MongoDBClient mongoDBClient = new MongoDBClient(user, password, dbName, host, port);

		Bson filter = Filters.regex("name", Constant.StormComponent.INSERT_NATIVE_BOLT);
		Document taskComponent = mongoDBClient.find(filter, "process");

		List<Object> parameters = (List) taskComponent.get("columnFieldsMap");
		Map _mapParameter = null;
		List<Column> columns = new ArrayList<Column>(parameters.size());

		for (int i = 0; i < parameters.size(); i++) {
			_mapParameter = (Map) parameters.get(i);
			columns.add(new Column((String) _mapParameter.get("name"), (Integer) _mapParameter.get("type")));
			LOG.debug(columns.toString());
		}

		Fields outputFieldsMock = new Fields("MOCK");
		SimpleJdbcMapper mapper = new KpiJdbcLookupMapper(outputFieldsMock, columns);

		StringBuilder sb = new StringBuilder((String) taskComponent.get(Constant.Fields.SCRIPT));
		Map<String, String> mapInsert = new HashMap<String, String>();
		mapInsert.put("NATIVE", sb.toString());

		this.mapInsert = mapInsert;
		this.jdbcMapper = (JdbcLookupMapper) mapper;
		setJdbcMapper((JdbcLookupMapper) mapper);
		
		String _setTopologyName = (String) stormConf.get("name");
		Map<String, Object> mapSource = getPropertiesConnection(mongoDBClient, _setTopologyName);
		
		if (HikariCPConnectionSingletonTarget.lostReferences()) {
			HikariCPConnectionSingletonTarget.setHikariCPConfigMap((Map<String, Object>) mapSource);
		}

		ConnectionProvider connectionProvider = HikariCPConnectionSingletonTarget.getInstance();
		connectionProvider.prepare();
		this.jdbcClient = new JdbcClientExt(connectionProvider, queryTimeoutSecs);

		mongoDBClient.close();

	}

	@Override
	protected void process(Tuple tuple) {

		LOG.info(" Processing message on bol UpsertKpiBolt Native ");

		Validate.notNull(HikariCPConnectionSingletonSource.getInstance());
		try {
			String upsertQuery = this.mapInsert.get("NATIVE");

			if (StringUtils.isBlank(upsertQuery)) {
				return;
			}

			/**
			 * Getting Payload from Tuple
			 */
			OperationPayload operationPayload = (OperationPayload) tuple.getValueByField("Constants.PAYLOAD");

			Map<String, Object> payloadMap = operationPayload.getPayload();
			Tuple tupleNative = (Tuple) payloadMap.get("TUPLE");
			LOG.debug(" UpsertKpiBolt ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
			LOG.debug(" UpsertKpiBolt " + tupleNative.toString());
			
			KpiJdbcLookupMapper kpiJdbcLookupMapper = (KpiJdbcLookupMapper) jdbcMapper;

			/**
			 * Working with Columns which will be part of final query.
			 */
			/**
			 * Adding columns generics.Don't move thats columns which are used in query for
			 * logic and options of work with last record.
			 */
			
			List<Column> columns = kpiJdbcLookupMapper.getColumns(tupleNative);

			columns.add(new CustomColumn("REGISTER_DATE", DateUtil.getSystemTimestamp(), Types.TIMESTAMP));
			columns.add(new CustomColumn("STATE", 1, Types.INTEGER));
			List<List<Column>> columnLists = new ArrayList<List<Column>>();
			columnLists.add(columns);

			this.jdbcClient.executeInsertQuery(upsertQuery, columnLists);
			this.collector.ack(tuple);

			Object _total = getValueByField(tupleNative, "TOTAL");
			Object _code = getValueByField(tupleNative, "CODE");

			if (_total == null || _code == null) {
				return;
			}

			////////////////////////////////////////////////////////////////////////////////////////////
			/**
			 * Only send tuple to WriterBolt when the next conditional is True.Just due to
			 * improve the performance on WebSocket Component.
			 */
			////////////////////////////////////////////////////////////////////////////////////////////

			if (_total.toString().equalsIgnoreCase(_code.toString())) {
				collector.emit("REST_STREAM", tuple, new Values(operationPayload));
			}
		
		   this.collector.ack(tuple);	
		   
		} catch (Exception e) {
			this.collector.reportError(e);
			this.collector.fail(tuple);
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declareStream("REST_STREAM", new Fields("Constants.PAYLOAD"));
	}

	private Object getValueByField(Tuple tuple, String columnName) {
		Object _value = null;

		try {
			_value = tuple.getValueByField(columnName);
		} catch (Exception e) {
			if (e instanceof IllegalArgumentException) {
				LOG.info("Stream id : " + tuple.getSourceStreamId() + " don't have the Field : " + columnName);
			}
		}
		return _value;
	}

	protected Map getPropertiesConnection(MongoDBClient mongoDBClient, String topologyName) {

		// Getting info about connections
		Bson filter = Filters.eq("name", topologyName);
		Document topology = mongoDBClient.find(filter, "topology");
		List<DBRef> communications = (List) topology.get(Constant.Fields.COMMUNICATIONS);
		Assertions.assertThat(communications.size()).isEqualTo(2);

		DBRef dbf1 = communications.get(0);
		DBRef dbf2 = communications.get(1);

		filter = Filters.and(Filters.or(Filters.eq("_id", dbf1.getId()), Filters.eq("_id", dbf2.getId())),
				Filters.eq("origin", Constant.Fields.TARGET));
		Document communicationSource = mongoDBClient.find(filter, "communication");
		Assertions.assertThat(communicationSource).isNotNull();

		/**
		 * Loading properties to be used in HikariFactory or HikariProvider
		 */
		Map<String, Object> mapSource = Maps.newHashMap();
		mapSource.putAll((Map) communicationSource.get(Constant.Fields.SETTING));

		mapSource.put("dataSource." + Constant.Fields.URL, mapSource.get(Constant.Fields.URL));
		mapSource.remove(Constant.Fields.URL);
		mapSource.put("dataSource." + Constant.Fields.USER, mapSource.get(Constant.Fields.USER));
		mapSource.remove(Constant.Fields.USER);
		mapSource.put("dataSource." + Constant.Fields.PASSWORD, mapSource.get(Constant.Fields.PASSWORD));
		mapSource.remove(Constant.Fields.PASSWORD);
		mapSource.put("registerMbeans", Boolean.TRUE);
		mapSource.put("maximumPoolSize", 41);

		return mapSource;
	}
}
