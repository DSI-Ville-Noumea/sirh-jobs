package nc.noumea.mairie.ptg.dao;

import java.util.List;

import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.ExportEtatsPayeurTask;
import nc.noumea.mairie.ptg.domain.ExportPaieTask;
import nc.noumea.mairie.ptg.domain.VentilTask;

import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class PointagesDao implements IPointagesDao {

	@Autowired
	@Qualifier("ptgSessionFactory")
	private SessionFactory ptgSessionFactory;

	public void beginTransaction() {
		ptgSessionFactory.getCurrentSession().beginTransaction();
	}
	
	public void commitTransaction() {
		ptgSessionFactory.getCurrentSession().getTransaction().commit();
	}
	
	public void rollBackTransaction() {
		ptgSessionFactory.getCurrentSession().getTransaction().rollback();
	}
	
	@Override
	public VentilTask getNextVentilTask() {
		
		@SuppressWarnings("unchecked")
		List<VentilTask> result = ptgSessionFactory.getCurrentSession().getNamedQuery("VentilTask.getNextVentilTask")
				.setLockMode("vT", LockMode.PESSIMISTIC_WRITE).setMaxResults(1).list();
		
		if (result.size() == 0)
			return null;
		
		return result.get(0);
	}

	@Override
	public ExportPaieTask getNextExportPaieTask() {

		@SuppressWarnings("unchecked")
		List<ExportPaieTask> result = ptgSessionFactory.getCurrentSession().getNamedQuery("ExportPaieTask.getNextExportPaieTask")
				.setLockMode("eT", LockMode.PESSIMISTIC_WRITE).setMaxResults(1).list();
		
		if (result.size() == 0)
			return null;
		
		return result.get(0);
	}

	@Override
	public ExportEtatsPayeurTask getNextExportEtatsPayeurTask() {
		
		@SuppressWarnings("unchecked")
		List<ExportEtatsPayeurTask> result = ptgSessionFactory.getCurrentSession().getNamedQuery("ExportEtatsPayeurTask.getNextExportEtatsPayeurTask")
				.setLockMode("eT", LockMode.PESSIMISTIC_WRITE).setMaxResults(1).list();
		
		if (result.size() == 0)
			return null;
		
		return result.get(0);
	}
	
	@Override
	public List<EtatPointage> getListePtgRefusesEtRejetesPlus3Mois(EtatPointageEnum etat) {
		
		@SuppressWarnings("unchecked")
		List<EtatPointage> result = ptgSessionFactory.getCurrentSession().getNamedQuery("EtatPointage.getListePtgRefusesEtRejetesPlus3Mois").setParameter("etat", etat).list();
		
		if (null == result || result.size() == 0)
			return null;
		
		return result; 
	}
	
	@Override
	public void createEtatPointage(EtatPointage etatPointage) {
		
		if (null != etatPointage && etatPointage.getIdEtatPointage() == null || etatPointage.getIdEtatPointage().equals(0)) {
			ptgSessionFactory.getCurrentSession().persist(etatPointage);
        }
	}
}
