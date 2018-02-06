
package org.apache.storm.flux.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.storm.flux.Flux;
import org.junit.Test;

/**
 * 
 * @author jalor
 *
 */
public class IntegrationTest {

    private static boolean skipTest = false;

    static {
        String skipStr = System.getProperty("skipIntegration");
        if(skipStr != null && skipStr.equalsIgnoreCase("false")){
            skipTest = false;
        }
    }

    @Test
    public void testRunTopologySource() throws Exception {
        if(!skipTest) {
            //Flux.main(new String[]{"-s", "900000", "src/main/resources/configs/existing-topology.yaml"});
            //Flux.main(new String[]{"-s", "900000", "src/main/resources/configs/existing-topology-method-override.yaml"});
        	Flux.main(new String[]{ "-s", "900000","src/test/resources/scripts/fixed-topology.yaml"});
            //Flux.main(new String[]{ "r","src/test/resources/scripts/fixed-topology.yaml"});
            System.out.print("salida");
        }
    }
    

	
}
