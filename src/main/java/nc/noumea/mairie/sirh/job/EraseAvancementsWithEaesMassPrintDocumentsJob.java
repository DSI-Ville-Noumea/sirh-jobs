package nc.noumea.mairie.sirh.job;

import nc.noumea.mairie.sirh.tools.Helper;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Service
public class EraseAvancementsWithEaesMassPrintDocumentsJob extends QuartzJobBean implements
IEraseAvancementsWithEaesMassPrintDocumentsJob {

	private Logger logger = LoggerFactory.getLogger(EraseAvancementsWithEaesMassPrintDocumentsJob.class);
	
	@Autowired
	@Qualifier("avcstTempWorkspacePath")
	private String avcstTempWorkspacePath;
	
	@Autowired
	@Qualifier("sirhAvctPastThresholdMilliseconds")
	private long sirhAvctPastThresholdMilliseconds;
	
	@Autowired
	private Helper helper;
	
	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {

		try {

			FileSystemManager fsm = VFS.getManager();
			FileObject rootFo = fsm.resolveFile(avcstTempWorkspacePath);
			
			if (rootFo.getType() != FileType.FOLDER) {
				logger.error("The given path is not a directory [{}]", avcstTempWorkspacePath);
				return;
			}

			logger.info("Scanning old files from {} ", avcstTempWorkspacePath);
			
			long deleteThresholdTimestamp = helper.getCurrentDate().getTime() - sirhAvctPastThresholdMilliseconds;
			
			for (FileObject fo : rootFo.getChildren()) {
				if (fo.getContent().getLastModifiedTime() <= deleteThresholdTimestamp) {
					logger.info("Deleting {}...", fo.getName());
					fo.delete();
				}
			}
			
		} catch (FileSystemException e) {
			logger.error(String.format("An error occured while trying to delete old files from [%s]", avcstTempWorkspacePath), e);
		} 
		
	}

}
