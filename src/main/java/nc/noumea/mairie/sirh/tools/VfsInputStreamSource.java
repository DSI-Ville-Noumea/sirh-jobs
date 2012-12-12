package nc.noumea.mairie.sirh.tools;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.vfs2.FileObject;
import org.springframework.core.io.InputStreamSource;

/**
 * This class is a helper aiming at providing the Spring MimeMessageHelper
 * a valid InputStreamSource from a commons-vfs FileObject
 * @author rayni84
 *
 */
public class VfsInputStreamSource implements InputStreamSource {

	private FileObject fileObject;
	
	public VfsInputStreamSource(FileObject fileObject) {
		this.fileObject = fileObject;
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		
		if (fileObject.isContentOpen())
			fileObject.close();
		
		return fileObject.getContent().getInputStream();
	}

}
