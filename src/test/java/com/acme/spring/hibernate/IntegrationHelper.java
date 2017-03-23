package com.acme.spring.hibernate;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

public class IntegrationHelper {
	private static final String BATCH_CONTAINER_NAME = "batch";
	private static final String INTEGRATION_SCRIPT = "/opt/stock-application-batch/run_integration.sh";

	/**
	 * option SYS properties
	 * DOCKER_HOST=tcp://xx:yy
	 * @throws IOException
	 * @See https://github.com/docker-java/docker-java
	 */
	public static void executeIntegration() {

	   final String serverUri = System.getenv("DOCKER_SERVER_URI");
	   if (serverUri == null || serverUri.isEmpty()) {
	   	 throw new IllegalStateException("DOCKER_SERVER_URI environment variable is missing");
	   }


	    ProcessBuilder builder = new ProcessBuilder(
		    	"/opt/dockerclient/exec.sh",
		    	serverUri,
		    	BATCH_CONTAINER_NAME,
		    	INTEGRATION_SCRIPT
		    )
		    //REDIRECT TO STRING AND LOG IT
		    .redirectErrorStream(true)
		    .redirectOutput(Redirect.INHERIT);

	    try {
		Process p = builder.start();
		if (p.waitFor() != 0) {
			throw new IllegalStateException("Docker client process did not terminate successfully");
		}
	    } catch (InterruptedException e) {
		throw new IllegalStateException("Docker client execution was interrupted");
	    }
	    catch(IOException x) {
		throw new IllegalStateException("Error executing process", x);
	    }
	}
}
