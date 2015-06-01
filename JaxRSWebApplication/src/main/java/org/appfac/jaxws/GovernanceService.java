package org.appfac.jaxws;

import javax.jws.WebService;

@WebService
public interface GovernanceService {

	public String getAppInfoArtifact(String applicationKey, String tenantDomain, String username);

}