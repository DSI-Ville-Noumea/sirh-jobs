package nc.noumea.mairie.sirh.ws;

public interface IEaeWSConsumer {
	
	ReturnMessageDto creerEAESansAffecte(Integer idCampagneEae, Integer idAgent);
	
	ReturnMessageDto creerEaeAffecte(Integer idCampagneEae, Integer idAgent);
}
