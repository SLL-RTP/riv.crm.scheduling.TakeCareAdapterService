package se.skl.skltpservices.takecare.takecareintegrationcomponent;

import org.apache.commons.lang.StringUtils;
import org.soitoolkit.commons.mule.test.StandaloneMuleServer;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TakeCareIntegrationComponentMuleServer {

    public static final String MULE_SERVER_ID = "TakeCareIntegrationComponent";

    private static final Logger logger = LoggerFactory.getLogger(TakeCareIntegrationComponentMuleServer.class);
    private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("TakeCareIntegrationComponent-config");

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws Exception {

        // Configure the mule-server:
        // 1. Specify the "TakeCareIntegrationComponent-teststubs.xml" file if teststub-services are to be loaded
        // 2. Specify true if all files including the services are to be loaded from the mule-deploy.properties - file
        // 3. Specify false if services are NOT to be loaded from the mule-deploy.properties - file, only common config files will be loaded
        StandaloneMuleServer muleServer = new StandaloneMuleServer(MULE_SERVER_ID, "TakeCareIntegrationComponent-teststubs.xml", true);

        // Start the server
        muleServer.run();
    }

    /**
     * Address based on usage of the servlet-transport and a config-property for the URI-part
     * 
     * @param serviceUrlPropertyName
     * @return
     */
    public static String getAddress(String serviceUrlPropertyName) {

        String url = rb.getString(serviceUrlPropertyName);

        if (StringUtils.isBlank(url)) {
            logger.warn("Failed to resolve property:" + serviceUrlPropertyName);
        } else {
            logger.info("URL: {}", url);
        }
        return url;

    }
}