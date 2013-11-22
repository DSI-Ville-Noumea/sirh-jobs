package nc.noumea.mairie.ptg.dao;

import java.util.List;
import java.util.Properties;

import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.ExportEtatsPayeurTask;
import nc.noumea.mairie.ptg.domain.ExportPaieTask;
import nc.noumea.mairie.ptg.domain.VentilTask;

import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.internal.TypeLocatorImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.EnumType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;
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
		
		StringBuilder sb = new StringBuilder();
		sb.append("select ptg.ID_ETAT_POINTAGE as idEtatPointage, ptg.ID_POINTAGE as idPointage, ptg.DATE_ETAT as dateEtat, ptg.DATE_MAJ as dateMaj, ptg.ETAT as etat, ptg.ID_AGENT as idAgent, ptg.VERSION as version from PTG_ETAT_POINTAGE ptg ");
		sb.append("where (ptg.ID_POINTAGE, ptg.ID_ETAT_POINTAGE) in ");
		sb.append("(select e.ID_POINTAGE, max(e.ID_ETAT_POINTAGE) as id_ptg_etat from PTG_ETAT_POINTAGE e group by e.ID_POINTAGE ) ");
		sb.append("and ptg.DATE_ETAT <= current_date - interval '3 month' and ptg.ETAT = :etat ");
		
		Properties params = new Properties();
		params.put("enumClass", "nc.noumea.mairie.ptg.domain.EtatPointageEnum"); 
		params.put("type", "12"); /*type 12 instructs to use the String representation of enum value*/
		Type etatPointageEnum = new TypeLocatorImpl(new TypeResolver()).custom(EnumType.class, params);

		
		@SuppressWarnings("unchecked")
		List<EtatPointage> result = ptgSessionFactory.getCurrentSession().createSQLQuery(sb.toString())
				.addScalar("idEtatPointage", StandardBasicTypes.INTEGER )
				.addScalar("idPointage", StandardBasicTypes.INTEGER )
				.addScalar("dateEtat", StandardBasicTypes.DATE )
				.addScalar("dateMaj", StandardBasicTypes.DATE )
				.addScalar("etat", etatPointageEnum )
				.addScalar("idAgent", StandardBasicTypes.INTEGER )
				.addScalar("version", StandardBasicTypes.INTEGER )
				.setParameter("etat", etat.getCodeEtat())
				.setResultTransformer(Transformers.aliasToBean(EtatPointage.class))
				.list();
		
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
