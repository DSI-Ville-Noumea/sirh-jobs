package nc.noumea.mairie.abs.dao;

import java.util.List;

import nc.noumea.mairie.abs.domain.EtatAbsenceEnum;

import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AbsencesDao implements IAbsencesDao {

	@Autowired
	@Qualifier("absSessionFactory")
	private SessionFactory absSessionFactory;

	public void beginTransaction() {
		absSessionFactory.getCurrentSession().beginTransaction();
	}

	public void commitTransaction() {
		absSessionFactory.getCurrentSession().getTransaction().commit();
	}

	public void rollBackTransaction() {
		absSessionFactory.getCurrentSession().getTransaction().rollback();
	}

	@Override
	public List<Integer> getListeAbsApprouve(EtatAbsenceEnum etat) {
		StringBuilder sb = new StringBuilder();
		sb.append("select abs.ID_DEMANDE as idEtatDemande from ABS_ETAT_DEMANDE abs ");
		sb.append("where (abs.ID_DEMANDE, abs.ID_ETAT_DEMANDE) in ");
		sb.append("(select e.ID_DEMANDE, max(e.ID_ETAT_DEMANDE) as id_abs_etat from ABS_ETAT_DEMANDE e group by e.ID_DEMANDE ) ");
		sb.append("and abs.DATE <= current_date  and abs.ID_REF_ETAT = :etat ");

		@SuppressWarnings("unchecked")
		List<Integer> result = absSessionFactory.getCurrentSession().createSQLQuery(sb.toString())
				.addScalar("idEtatDemande", StandardBasicTypes.INTEGER).setParameter("etat", etat.getCodeEtat()).list();

		if (null == result || result.size() == 0)
			return null;

		return result;
	}
}
