package org.wso2.appfactory.gregloadtest;

/**
 * Created by wdfdo1986 on 5/22/15.
 */
public class Client {

	private static ConfigReader reader = ConfigReader.getInstance();
	public static void main(String[] args){
		try {
			System.setProperty("javax.net.ssl.trustStore", reader.getProperty("jksLocation"));
			System.setProperty("javax.net.ssl.trustStorePassword",reader.getProperty("jkspassword"));
			int no_of_tenants = Integer.parseInt(ConfigReader.getInstance().getProperty("no_of_tenants"));
			int no_of_tenants_to_load = Integer.parseInt(ConfigReader.getInstance().getProperty("no_of_tenants_to_load"));
			int no_of_applications = Integer.parseInt(ConfigReader.getInstance().getProperty("no_of_rxt_instances"));
			ApplicationManager applicationManager = new ApplicationManager();
			int tenant_domain_postfix_start_number =
					Integer.parseInt(ConfigReader.getInstance().getProperty("tenant_domain_postfix_start_number"));
			int tenant_domain_postfix_end_number = tenant_domain_postfix_start_number + no_of_tenants;
			int application_postfix_start_number =
					Integer.parseInt(ConfigReader.getInstance().getProperty("application_postfix_start_number"));
			int tenant_domain_to_load_postfix_end_number = tenant_domain_postfix_start_number + no_of_tenants_to_load;
			int application_postfix_end_number = application_postfix_start_number + no_of_applications;
			TenantManager tenantManager = new TenantManager();
			long sleeptime = Long.parseLong(reader.getProperty("sleeptime"));

			for (int i = tenant_domain_postfix_start_number; i <= tenant_domain_to_load_postfix_end_number; i++) {
				tenantManager.loadTenant(Initializer.getTenantDomain(i));
				Thread.sleep(sleeptime);
			}

			for (int i = tenant_domain_postfix_start_number; i <= tenant_domain_to_load_postfix_end_number; i++) {
				for (int j = application_postfix_start_number; j <= application_postfix_end_number; j++) {
					String applicationKey = Initializer.getTenantDomain(i) + "_" + reader.getProperty("application_key_prefix") + j;
					String artifactApplicationKey = applicationManager.getArtifact(applicationKey,
					                                                               Initializer.getTenantDomain(i));
					if(!applicationKey.equals(artifactApplicationKey)){
						System.err.println("Application key doesn't map.");
						System.out.println("Application key is : " + applicationKey);
					}
				}
			}

		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
