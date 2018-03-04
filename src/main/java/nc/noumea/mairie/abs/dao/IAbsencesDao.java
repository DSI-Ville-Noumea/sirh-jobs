package nc.noumea.mairie.abs.dao;

import java.util.List;

import nc.noumea.mairie.abs.domain.Demande;
import nc.noumea.mairie.abs.domain.EtatAbsenceEnum;

public interface IAbsencesDao {

	void beginTransaction();

	void commitTransaction();

	void rollBackTransaction();

	List<Integer> getListeAbsWithEtat(EtatAbsenceEnum etat);

	List<Integer> getListeAbsWithEtatAndTypeAbsence(List<Integer> listTypeGroupeAbs, EtatAbsenceEnum etat);

	List<Integer> getListeCongeUnique();

	List<Demande> getAllInfoForAbs(List<Integer> ids);

	void persistObject(Object object);
}
