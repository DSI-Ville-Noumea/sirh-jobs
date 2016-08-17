package nc.noumea.mairie.alfresco.cmis;

import java.io.File;

import javax.annotation.PostConstruct;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AlfrescoCMISService implements IAlfrescoCMISService {
	
	private Logger logger = LoggerFactory.getLogger(AlfrescoCMISService.class);
	
	
	private static final String MIME_TYPE = "application/octet-stream";
	
	@Autowired
	@Qualifier("alfrescoUrl")
	private String alfrescoUrl;
	
	@Autowired
	@Qualifier("alfrescoLogin")
	private String alfrescoLogin;
	
	@Autowired
	@Qualifier("alfrescoPassword")
	private String alfrescoPassword;
	
	@Autowired
	private CreateSession createSession;
	
	@Autowired
	private CmisService cmisService;
	
	private static String staticAlfrescoUrl;
	
	@PostConstruct
	public void init() {
		AlfrescoCMISService.staticAlfrescoUrl = alfrescoUrl;
	}
	
	/**
	 * exemple de nodeRef : "workspace://SpacesStore/1a344bd7-6422-45c6-94f7-5640048b20ab"
	 * exemple d URL a retourner :
	 * http://localhost:8080/alfresco/service/api/node/workspace/SpacesStore/418c511a-7c0a-4bb1-95a2-37e5946be726/content
	 * 
	 * @param nodeRef String
	 * @return String l URL pour acceder au document directement a alfresco
	 */
	public static String getUrlOfDocument(String nodeRef) {
		
		return CmisUtils.getUrlOfDocument(staticAlfrescoUrl, nodeRef);
	}
	
	@Override
	public File getFile(String nodeRef) {
		
		Session session = null;
		try {
			session = createSession.getSession(alfrescoUrl, alfrescoLogin, alfrescoPassword);
		} catch(CmisConnectionException e) {
			logger.debug("Erreur de connexion a Alfresco CMIS : " + e.getMessage());
			throw e;
		}
		
		return cmisService.getFile(session, nodeRef);
	}

}
