package nc.noumea.mairie.ldap.dao;

import nc.noumea.mairie.ldap.domain.AgentLdap;

//@Repository
public class MockAgentLdapDao implements IAgentLdapDao {

	@Override
	public AgentLdap retrieveAgentFromLdapFromMatricule(String agentId)
			throws AgentLdapDaoException {
		AgentLdap a  = new AgentLdap();
		a.setMail("noemie.nicolas@ville-noumea.nc");
		a.setDisplayName("Noémie NICOLAS");
		
		return a;
	}

}
