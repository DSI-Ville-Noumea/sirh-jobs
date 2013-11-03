package nc.noumea.mairie.sirh.service;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ptg.dao.IPointagesDao;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class PointageServiceTest {

	@Test
	public void testreadJsonResponseAsList_ListOfString() throws Exception {
		// Given
		List<EtatPointage> result = new ArrayList<EtatPointage>();
		EtatPointage ep = new EtatPointage();
		ep.setEtat(EtatPointageEnum.REFUSE_DEFINITIVEMENT);
		result.add(ep);
		
		IPointagesDao pointagesDao = Mockito.mock(IPointagesDao.class);
		Mockito.when(pointagesDao.getListePtgRefusesEtRejetesPlus3Mois(EtatPointageEnum.REFUSE)).thenReturn(result); 
		
		IPointageService pointageService = new PointageService();
		
		ReflectionTestUtils.setField(pointageService, "pointagesDao", pointagesDao); 
		
		pointageService.majEtatPointagesRefusesEtRejetesPlus3Mois();
	}

}
