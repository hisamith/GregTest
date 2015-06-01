package org.wso2.appfactory.greg.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.appfactory.greg.Constants;
import org.wso2.appfactory.greg.internal.ServiceHolder;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.context.RegistryType;
import org.wso2.carbon.core.AbstractAdmin;
import org.wso2.carbon.governance.api.generic.GenericArtifactManager;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifactImpl;
import org.wso2.carbon.governance.api.util.GovernanceUtils;
import org.wso2.carbon.registry.core.RegistryConstants;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.session.UserRegistry;

/**
 * Created by wdfdo1986 on 5/28/15.
 */
public class GregService extends AbstractAdmin {

	private static Log log = LogFactory.getLog(GregService.class);

	public String getAppInfoArtifact(String applicationKey, String tenantDomain, String username) {
		long start = System.currentTimeMillis();
		try {
			PrivilegedCarbonContext.startTenantFlow();
			PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenantDomain, true);
			PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername(username);

			UserRegistry userRegistry = getUserRegistry();
			String resourcePath = getAppInfoRegistryPath(applicationKey);
			if(!userRegistry.resourceExists(resourcePath)){
				return null;
			}
			Resource resource = userRegistry.get(resourcePath);
			GovernanceUtils.loadGovernanceArtifacts(userRegistry);
			GenericArtifactManager artifactManager = new GenericArtifactManager(userRegistry,
			                                                                    Constants.RXT_KEY_APPINFO_APPLICATION);
			GenericArtifact artifact = (GenericArtifactImpl) artifactManager.getGenericArtifact(resource.getUUID());
			long end = System.currentTimeMillis();

			long timespent = end - start;
			log.info("Time in milliseconds to get artifact for application : " + applicationKey + ", tenantDomain : " + tenantDomain + " : " + timespent);

			if (artifact != null) {
				return artifact.getAttribute(Constants.RXT_KEY_APPINFO_KEY);
			}

		} catch (Exception e) {
			log.error("Error while getting the user registry, ", e);
		} finally {
			PrivilegedCarbonContext.endTenantFlow();
		}
		return null;
	}


	private UserRegistry getUserRegistry() throws RegistryException {
		ServiceHolder.getInstance().getTenantRegistryLoader().loadTenantRegistry(
				CarbonContext.getThreadLocalCarbonContext().getTenantId());
		return (UserRegistry)CarbonContext.getThreadLocalCarbonContext().getRegistry(RegistryType.SYSTEM_GOVERNANCE);
	}

	private String getAppInfoRegistryPath(String applicationId) {
		return Constants.REGISTRY_APPLICATION_PATH + RegistryConstants.PATH_SEPARATOR + applicationId +
		       RegistryConstants.PATH_SEPARATOR + Constants.RXT_KEY_APPINFO;
	}

}
