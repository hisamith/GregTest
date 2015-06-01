package org.wso2.appfactory.gregloadtest;

/**
 * Created by wdfdo1986 on 5/27/15.
 */

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.wso2.appfactory.gregwebservice.stub.GregServiceStub;
import org.wso2.carbon.governance.generic.stub.ManageGenericArtifactServiceRegistryExceptionException;
import org.wso2.carbon.governance.generic.stub.ManageGenericArtifactServiceStub;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ApplicationManager {

	ManageGenericArtifactServiceStub manageGenericArtifactServiceStub = null;
	GregServiceStub gregServiceStub = null;

	public ApplicationManager(){
		try {
			manageGenericArtifactServiceStub =
					new ManageGenericArtifactServiceStub(ConfigReader.getInstance().getProperty("greg_url")
					                                     + "/services/ManageGenericArtifactService");
			gregServiceStub = new GregServiceStub(ConfigReader.getInstance().getProperty("greg_url")
			                                      + "/services/GregService");
		} catch (AxisFault axisFault) {
			axisFault.printStackTrace();
		}
	}

	public void createApplication(String applicationKey, String tenantDomain)
			throws RemoteException, ManageGenericArtifactServiceRegistryExceptionException {
		String userName = getTenantAdminUserName(tenantDomain);
		String content = "<metadata xmlns=\"http://www.wso2.org/governance/metadata\">" +
		                 "<application>" +
		                 "<description>" + ConfigReader.getInstance().getProperty("application_desc") + "</description>" +
		                 "<name>" + applicationKey + "</name>" +
		                 "<owner>" + userName + "</owner>" +
		                 "<key>" + applicationKey + "</key>" +
		                 "<type>" + ConfigReader.getInstance().getProperty("application_type") + "</type>" +
		                 "<repoAccessability>" + ConfigReader.getInstance().getProperty("application_repo_accecibility") + "</repoAccessability>" +
		                 "<repositorytype>" + ConfigReader.getInstance().getProperty("application_repo_type") + "</repositorytype>" +
		                 "<applicationCreationStatus>PENDING</applicationCreationStatus>" +
		                 "</application>" +
		                 "</metadata>";

		setAuthHeaders(userName);

		manageGenericArtifactServiceStub.addArtifact(ConfigReader.getInstance().getProperty("rxt_name"), content, null);
	}

	public String getArtifact(String applicationKey, String tenantDomain)
			throws RemoteException, ManageGenericArtifactServiceRegistryExceptionException {
		String userName = getTenantAdminUserName(tenantDomain);
		setAuthHeaders(userName);
		return gregServiceStub.getAppInfoArtifact(applicationKey, tenantDomain, userName);
	}

	private void setAuthHeaders(String userName){
		ServiceClient applicationServiceClient = manageGenericArtifactServiceStub._getServiceClient();

		HttpTransportProperties.Authenticator basicAuthenticator = new HttpTransportProperties.Authenticator();
		List<String> authSchemes = new ArrayList<String>();
		authSchemes.add(HttpTransportProperties.Authenticator.BASIC);

		basicAuthenticator.setAuthSchemes(authSchemes);
		basicAuthenticator.setUsername(userName);
		basicAuthenticator.setPassword(ConfigReader.getInstance().getProperty("tenant_admin_password"));
		basicAuthenticator.setPreemptiveAuthentication(true);
		applicationServiceClient.getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE, basicAuthenticator);
		applicationServiceClient.getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, "false");
	}

	private String getTenantAdminUserName(String tenantDomain){
		return ConfigReader.getInstance().getProperty("tenant_admin_name") + "@" + tenantDomain;
	}

}
