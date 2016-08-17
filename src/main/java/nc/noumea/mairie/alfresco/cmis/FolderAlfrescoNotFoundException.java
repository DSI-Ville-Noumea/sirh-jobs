package nc.noumea.mairie.alfresco.cmis;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason = "Dossier distant Alfresco non trouvé", value = HttpStatus.CONFLICT)
public class FolderAlfrescoNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public FolderAlfrescoNotFoundException() {
		super();
	}
	
	public FolderAlfrescoNotFoundException(String message) {
		super(message);
	}
	
	public FolderAlfrescoNotFoundException(String message, Exception innerException) {
		super(message, innerException);
	}

}
