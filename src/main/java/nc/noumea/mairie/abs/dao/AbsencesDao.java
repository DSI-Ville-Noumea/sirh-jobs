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
	public List<Integer> getListeAbsWithEtat(EtatAbsenceEnum etat) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT abs.ID_DEMANDE AS idEtatDemande FROM ABS_ETAT_DEMANDE abs ");
		sb.append("INNER JOIN ABS_DEMANDE dem ON dem.ID_DEMANDE = abs.ID_DEMANDE ");
		sb.append("WHERE (abs.ID_DEMANDE, abs.ID_ETAT_DEMANDE) in ");
		sb.append("(SELECT e.ID_DEMANDE, max(e.ID_ETAT_DEMANDE) AS id_abs_etat FROM ABS_ETAT_DEMANDE e GROUP BY e.ID_DEMANDE ) ");
		sb.append("AND dem.DATE_DEBUT <= current_date AND abs.ID_REF_ETAT = :etat ");

		@SuppressWarnings("unchecked")
		List<Integer> result = absSessionFactory.getCurrentSession().createSQLQuery(sb.toString())
				.addScalar("idEtatDemande", StandardBasicTypes.INTEGER).setParameter("etat", etat.getCodeEtat()).list();

		return result;
	}
}
