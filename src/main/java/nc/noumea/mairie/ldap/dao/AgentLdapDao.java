package nc.noumea.mairie.ldap.dao;

import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import nc.noumea.mairie.ldap.domain.AgentLdap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AgentLdapDao implements IAgentLdapDao {

	private Logger logger = LoggerFactory.getLogger(AgentLdapDao.class);
	
	@Autowired
	private LdapTemplate ldapTemplate;
	
	public AgentLdapDao() {
		
	}
	
	private static class AgentLdapAttributesMapper implements AttributesMapper {

		@Override
		public AgentLdap mapFromAttributes(Attributes attrs) throws NamingException {
			AgentLdap ag = new AgentLdap();
			ag.setDisplayName((String)attrs.get("displayName").get());
			ag.setEmployeeNumber((String)attrs.get("employeeNumber").get());
			ag.setMail((String)attrs.get("mail").get());
			return ag;
		}
	}

	@Override
	public AgentLdap retrieveAgentFromLdapFromMatricule(String agentId) throws AgentLdapDaoException {
		
		logger.info("Looking for agent employeeId '{}' in AD...", agentId);
		
		@SuppressWarnings("unchecked")
		List<AgentLdap> agents = ldapTemplate.search(
							       "OU=Z-Users", "(employeeNumber=905138)",
							       new AgentLdapAttributesMapper());

		if (agents.size() != 1)
			throw new AgentLdapDaoException(
					String.format("Expected 1 user corresponding to this employeeNumber '%s' but found '%s'.", agentId, agents.size()));

		AgentLdap agent = agents.get(0);
		logger.info("Agent found: displayName={}, matricule={}, mail={}", new Object[] {agent.getDisplayName(), agent.getEmployeeNumber(), agent.getMail()});
		
		return agent;
	}
}
