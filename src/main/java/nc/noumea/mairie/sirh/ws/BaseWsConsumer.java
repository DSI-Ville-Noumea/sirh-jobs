package nc.noumea.mairie.sirh.ws;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public abstract class BaseWsConsumer {
	
	
	public ClientResponse createAndFirePostRequest(Map<String, String> parameters, String url) {
		return createAndFireRequest(parameters, url, true, null);
	}
	
	public ClientResponse createAndFireRequest(Map<String, String> parameters, String url, boolean isPost, String postContent) {

		Client client = Client.create();
		WebResource webResource = client.resource(url);
		
		for (String key : parameters.keySet()) {
			webResource = webResource.queryParam(key, parameters.get(key));
		}

		ClientResponse response = null;

		try {
			if (isPost)
				if (postContent != null)
					response = webResource.accept(MediaType.APPLICATION_JSON_VALUE).post(ClientResponse.class);
				else
					response = webResource.accept(MediaType.APPLICATION_JSON_VALUE).post(ClientResponse.class, postContent);
			else
				response = webResource.accept(MediaType.APPLICATION_JSON_VALUE).get(ClientResponse.class);
		} catch (ClientHandlerException ex) {
			throw new WSConsumerException(String.format("An error occured when querying '%s'.", url), ex);
		}

		return response;
	}

	public void readResponse(ClientResponse response, String url) {
		
		if (response.getStatus() == HttpStatus.OK.value()) 
			return;
		
		throw new WSConsumerException(String.format("An error occured when querying '%s'. Return code is : %s, content is %s", 
				url, response.getStatus(), response.getEntity(String.class)));
	}
}
