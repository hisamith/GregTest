package org.appfac.jaxws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.context.RegistryType;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.service.TenantRegistryLoader;
import org.wso2.carbon.registry.core.session.UserRegistry;

import javax.jws.WebService;

/* @scr.reference name="registry.loader.default"
		*                interface="org.wso2.carbon.registry.core.service.TenantRegistryLoader"
		*                cardinality="1..1"
		*                policy="dynamic"
		*                bind="setRegistryLoader"
		*                unbind="unsetRegistryLoader"
*/

@WebService(serviceName = "GovernanceServiceImpl")
public class GovernanceServiceImpl implements GovernanceService {

	private static Log log = LogFactory.getLog(GovernanceServiceImpl.class);
	private TenantRegistryLoader tenantRegistryLoader = null;

	public GovernanceServiceImpl() {

	}

	public String getAppInfoArtifact(String applicationKey, String tenantDomain, String username) {
		try {
			PrivilegedCarbonContext.startTenantFlow();
			PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenantDomain, true);
			PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername(username);

			UserRegistry userRegistry = getUserRegistry();

		} catch (RegistryException e) {
			log.error("Error while getting the user registry, ", e);
		} finally {
			PrivilegedCarbonContext.endTenantFlow();
		}
		return null;
	}

	protected void setRegistryLoader(TenantRegistryLoader tenantRegistryLoader) {
		this.tenantRegistryLoader = tenantRegistryLoader;
	}

	protected void unsetRegistryLoader(TenantRegistryLoader tenantRegistryLoader) {
		this.tenantRegistryLoader = null;
	}

	private UserRegistry getUserRegistry() throws RegistryException {
		tenantRegistryLoader.loadTenantRegistry(CarbonContext.getThreadLocalCarbonContext().getTenantId());
		return (UserRegistry)CarbonContext.getThreadLocalCarbonContext().getRegistry(RegistryType.SYSTEM_GOVERNANCE);
	}
}
