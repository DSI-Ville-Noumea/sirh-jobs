package nc.noumea.mairie.ldap.dao;

import nc.noumea.mairie.ldap.domain.AgentLdap;

import org.springframework.stereotype.Repository;

@Repository
public class MockAgentLdapDao implements IAgentLdapDao {

	@Override
	public AgentLdap retrieveAgentFromLdapFromMatricule(String agentId)
			throws AgentLdapDaoException {
		AgentLdap a  = new AgentLdap();
		a.setMail("nicolas.raynaud@ville-noumea.nc");
		a.setDisplayName("Nicolas Raynaud");
		
		return a;
	}

}
