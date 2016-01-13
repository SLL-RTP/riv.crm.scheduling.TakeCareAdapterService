package se.skl.skltpservices.takecare.takecareintegrationcomponent.getalltimetypes;

import static se.skl.skltpservices.takecare.takecareintegrationcomponent.TakeCareIntegrationComponentMuleServer.getAddress;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.Fault;
import org.w3.wsaddressing10.AttributedURIType;

import se.riv.crm.scheduling.getalltimetypes.v1.GetAllTimeTypesResponderInterface;
import se.riv.crm.scheduling.getalltimetypes.v1.GetAllTimeTypesResponderService;
import se.riv.crm.scheduling.getalltimetypes.v1.GetAllTimeTypesResponseType;
import se.riv.crm.scheduling.getalltimetypes.v1.GetAllTimeTypesType;
import se.skl.skltpservices.takecare.TakeCareTestConsumer;

public class GetAllTimeTypesTestConsumer extends TakeCareTestConsumer {

	private static final Logger log = LoggerFactory.getLogger(GetAllTimeTypesTestConsumer.class);

	private GetAllTimeTypesResponderInterface _service = null;

	public static void main(String[] args) throws Fault {
		String serviceAddress = getAddress("GETALLTIMETYPES_INBOUND_URL_1");
		String healthcareFacility = "HSA-VKK123";

		GetAllTimeTypesTestConsumer consumer = new GetAllTimeTypesTestConsumer(serviceAddress);
		GetAllTimeTypesResponseType response = consumer.callService(healthcareFacility);
		log.info("Returned value = " + response.getListOfTimeTypes());
	}

	public GetAllTimeTypesTestConsumer(String serviceAddress) {
		try {
			initHttpsCommunication();
			URL url = new URL(serviceAddress + "?wsdl");
			_service = new GetAllTimeTypesResponderService(url).getGetAllTimeTypesResponderPort();
		} catch (MalformedURLException e) {
			throw new RuntimeException("Malformed URL Exception: " + e.getMessage());
		}
	}

	public GetAllTimeTypesResponseType callService(String healthcareFacility) throws Fault {
		log.debug("Calling GetAllTimeTypes-service with healthcareFacility", healthcareFacility);

		GetAllTimeTypesType request = new GetAllTimeTypesType();
		request.setHealthcareFacility(healthcareFacility);
		String senderId = "1";

		return _service.getAllTimeTypes(new AttributedURIType(), request);
	}

}