package org.wso2.appfactory.gregloadtest;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties.Authenticator;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.wso2.carbon.tenant.mgt.stub.TenantMgtAdminServiceExceptionException;
import org.wso2.carbon.tenant.mgt.stub.TenantMgtAdminServiceStub;
import org.wso2.carbon.tenant.mgt.stub.beans.xsd.TenantInfoBean;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wdfdo1986 on 5/22/15.
 */
public class TenantManager {

	TenantMgtAdminServiceStub tenantMgtAdminServiceStub = null;

	public TenantManager(){
		try {
			tenantMgtAdminServiceStub = new TenantMgtAdminServiceStub(ConfigReader.getInstance().getProperty("greg_url")
			                                                          + "/services/TenantMgtAdminService");
			ServiceClient serviceClient = tenantMgtAdminServiceStub._getServiceClient();

			Authenticator basicAuthenticator = new Authenticator();
			List<String> authSchemes = new ArrayList<String>();
			authSchemes.add(Authenticator.BASIC);

			basicAuthenticator.setAuthSchemes(authSchemes);
			basicAuthenticator.setUsername(ConfigReader.getInstance().getProperty("system_admin_username"));
			basicAuthenticator.setPassword(ConfigReader.getInstance().getProperty("system_admin_password"));
			basicAuthenticator.setPreemptiveAuthentication(true);
			serviceClient.getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE, basicAuthenticator);
			serviceClient.getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, "false");

			int timeOutInMilliSeconds = 5 * 60 * 1000;
			tenantMgtAdminServiceStub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, new Integer(timeOutInMilliSeconds));
			tenantMgtAdminServiceStub._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT, new Integer(timeOutInMilliSeconds));


		} catch (AxisFault axisFault) {
			axisFault.printStackTrace();
		}
	}

	public void createTenant(TenantInfoBean bean) throws RemoteException, TenantMgtAdminServiceExceptionException {
		tenantMgtAdminServiceStub.addTenant(bean);
	}

	public void loadTenant(String tenantDomain) throws IOException {
		HttpClient client = new DefaultHttpClient();
		String tenantWebappUrl = ConfigReader.getInstance().getProperty("greg_url") + "/t/" + tenantDomain + "/webapps/"
				+ ConfigReader.getInstance().getProperty("webappname");
		HttpGet request = new HttpGet(tenantWebappUrl);
		HttpResponse response = client.execute(request);
	}

}
