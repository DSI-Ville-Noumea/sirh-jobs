package nc.noumea.mairie.sirh.eae.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.sirh.eae.domain.EaeCampagneAction;
import nc.noumea.mairie.sirh.tools.Helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EaeCampagneActionService implements IEaeCampagneActionService {

	@PersistenceContext(unitName = "eaePersistenceUnit")
	private EntityManager eaeEntityManager;
	
	@Autowired
	private Helper helper;
	
	public EaeCampagneActionService() {
	}
	
	@Override
	public List<EaeCampagneAction> getEaeCampagneActionToSend() {

		TypedQuery<EaeCampagneAction> eaeQuery = eaeEntityManager.createNamedQuery("EaeCampagneAction.getTodayNotifications", EaeCampagneAction.class);
		eaeQuery.setParameter("todayDate", helper.getCurrentDate());
		List<EaeCampagneAction> result = eaeQuery.getResultList();
		
		return result;
	}
}
