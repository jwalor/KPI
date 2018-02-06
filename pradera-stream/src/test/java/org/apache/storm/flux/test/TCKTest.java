
package org.apache.storm.flux.test;

import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.flux.FluxBuilder;
import org.apache.storm.flux.model.ExecutionContext;
import org.apache.storm.flux.model.TopologyDef;
import org.apache.storm.flux.parser.FluxParser;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 
 * @author jalor
 *
 */
public class TCKTest {
	
    @Test
    public void testTCK() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/tck.yaml", false, true, null, false);
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
        assertNotNull(topology);
        topology.validate();
    }

    @Test
    public void testShellComponents() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/shell_test.yaml", false, true, null, false);
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
        assertNotNull(topology);
        topology.validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadShellComponents() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/bad_shell_test.yaml", false, true, null, false);
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
        assertNotNull(topology);
        topology.validate();
    }

    @Test
    public void testKafkaSpoutConfig() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/kafka_test.yaml", false, true, 
        		"C:\\Users\\jalor\\git\\storm\\flux\\flux-core\\src\\test\\resources\\configs\\test.properties", false);
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
        assertNotNull(topology);
        topology.validate();
    }

    @Test
    public void testLoadFromResource() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/kafka_test.yaml", false, true, null, false);
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
        assertNotNull(topology);
        topology.validate();
    }


    @Test
    public void testHdfs() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/hdfs_test.yaml", false, true, null, false);
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
        assertNotNull(topology);
        topology.validate();
    }

    @Test
    public void testDiamondTopology() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/dsl-topology.yaml", false, true, null, false);
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
        assertNotNull(topology);
        topology.validate();
    }


    @Test
    public void testHbase() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/simple_hbase.yaml", false, true, null, false);
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
        assertNotNull(topology);
        topology.validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadHbase() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/bad_hbase.yaml", false, true, null, false);
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
        assertNotNull(topology);
        topology.validate();
    }

    @Test
    public void testIncludes() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/include_test.yaml", false, true, null, false);
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
        assertNotNull(topology);
        assertTrue(topologyDef.getName().equals("include-topology"));
        assertTrue(topologyDef.getBolts().size() > 0);
        assertTrue(topologyDef.getSpouts().size() > 0);
        topology.validate();
    }

    @Test
    public void testTopologySource() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/existing-topology.yaml", false, true, null, false);
        assertTrue(topologyDef.validate());
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
        assertNotNull(topology);
        topology.validate();
    }

    @Test
    public void testTopologySourceWithReflection() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/existing-topology-reflection.yaml", false, true, null, false);
        assertTrue(topologyDef.validate());
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
        assertNotNull(topology);
        topology.validate();
    }

    @Test
    public void testTopologySourceWithConfigParam() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/existing-topology-reflection-config.yaml", false, true, null, false);
        assertTrue(topologyDef.validate());
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
        assertNotNull(topology);
        topology.validate();
    }

    @Test
    public void testTopologySourceWithMethodName() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/existing-topology-method-override.yaml", false, true, null, false);
        assertTrue(topologyDef.validate());
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
        assertNotNull(topology);
        topology.validate();
    }


    @Test
    public void testTridentTopologySource() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/existing-topology-trident.yaml", false, true, null, false);
        assertTrue(topologyDef.validate());
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
        assertNotNull(topology);
        topology.validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTopologySource() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/invalid-existing-topology.yaml", false, true, null, false);
        assertFalse("Topology config is invalid.", topologyDef.validate());
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
    }


    @Test
    public void testTopologySourceWithGetMethodName() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/existing-topology-reflection.yaml", false, true, null, false);
        assertTrue(topologyDef.validate());
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
        assertNotNull(topology);
        topology.validate();
    }

    @Test
    public void testTopologySourceWithConfigMethods() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/config-methods-test.yaml", false, true, null, false);
        assertTrue(topologyDef.validate());
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
        assertNotNull(topology);
        topology.validate();

        // make sure the property was actually set
//        TestBolt bolt = (TestBolt)context.getBolt("bolt-1");
//        assertTrue(bolt.getFoo().equals("foo"));
//        assertTrue(bolt.getBar().equals("bar"));
//        assertTrue(bolt.getFooBar().equals("foobar"));
//        assertArrayEquals(new TestBolt.TestClass[] {new TestBolt.TestClass("foo"), new TestBolt.TestClass("bar"), new TestBolt.TestClass("baz")}, bolt.getClasses());
    }

    @Test
    public void testVariableSubstitution() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/substitution-test.yaml", false, true, "src/test/resources/configs/test.properties", true);
        assertTrue(topologyDef.validate());
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
        assertNotNull(topology);
        topology.validate();

        // test basic substitution
        assertEquals("Property not replaced.",
                "substitution-topology",
                context.getTopologyDef().getName());

        // test environment variable substitution
        // $PATH should be defined on most systems
        String envPath = System.getenv().get("PATH");
        assertEquals("ENV variable not replaced.",
                envPath,
                context.getTopologyDef().getConfig().get("test.env.value"));

    }
}
