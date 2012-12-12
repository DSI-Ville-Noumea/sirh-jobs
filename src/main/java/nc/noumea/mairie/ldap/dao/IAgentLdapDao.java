package nc.noumea.mairie.ldap.dao;

import nc.noumea.mairie.ldap.domain.AgentLdap;

public interface IAgentLdapDao {

	public AgentLdap retrieveAgentFromLdapFromMatricule(String agentId) throws AgentLdapDaoException;
}
