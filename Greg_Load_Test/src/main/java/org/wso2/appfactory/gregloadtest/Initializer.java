package org.wso2.appfactory.gregloadtest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.tenant.mgt.stub.beans.xsd.TenantInfoBean;

/**
 * Created by wdfdo1986 on 6/1/15.
 */
public class Initializer {
	private static ConfigReader reader = ConfigReader.getInstance();
	public static void main(String[] args){
		final Log log = LogFactory.getLog(Initializer.class);
		log.info("\n##########################################################");
		log.info("Tenant Creation started ...... \n");
		try {
			System.setProperty("javax.net.ssl.trustStore", reader.getProperty("jksLocation"));
			System.setProperty("javax.net.ssl.trustStorePassword",reader.getProperty("jkspassword"));
			int no_of_tenants = Integer.parseInt(ConfigReader.getInstance().getProperty("no_of_tenants"));
			int no_of_applications = Integer.parseInt(ConfigReader.getInstance().getProperty("no_of_rxt_instances"));
			TenantManager tenantManager = new TenantManager();
			ApplicationManager applicationManager = new ApplicationManager();
			int tenant_domain_postfix_start_number =
					Integer.parseInt(ConfigReader.getInstance().getProperty("tenant_domain_postfix_start_number"));
			int tenant_domain_postfix_end_number = tenant_domain_postfix_start_number + no_of_tenants;
			int application_postfix_start_number =
					Integer.parseInt(ConfigReader.getInstance().getProperty("application_postfix_start_number"));
			int application_postfix_end_number = application_postfix_start_number + no_of_applications;
			long tenant_create_sleep_time = Long.parseLong(reader.getProperty("tenant_create_sleep_time"));
			long application_create_sleep_time = Long.parseLong(reader.getProperty("application_create_sleep_time"));
			log.info("# of tenants to create: "+no_of_tenants);
			log.info("start creating from tenant domain: "+tenant_domain_postfix_start_number);
			log.info("ends with tenant domain: "+tenant_domain_postfix_end_number);
			log.info("# of apps to create: "+no_of_applications);

			for (int i = tenant_domain_postfix_start_number; i < tenant_domain_postfix_end_number; i++) {
				TenantInfoBean bean = new TenantInfoBean();
				log.info("Creating tenant: "+i);
				populateTenantInfoBean(bean, i);
				try {
					tenantManager.createTenant(bean);
				} catch (Exception e){
					log.error("Error occurred while creating tenant at first attempt", e);
					Thread.sleep(tenant_create_sleep_time);
					try {
						tenantManager.createTenant(bean);
					} catch (Exception e1){
						log.error("Error occurred while creating tenant at 2nd attempt", e1);
						Thread.sleep(tenant_create_sleep_time);
						try {
							tenantManager.createTenant(bean);
						} catch (Exception e2){
							log.error("Error occurred while creating tenant at 2rd attempt and skipping tenant " +
							          "creation",e2);
						}
					}
				}
				Thread.sleep(tenant_create_sleep_time);
			}

			log.info("---------- Tenant creation finished --------");
			for (int i = tenant_domain_postfix_start_number; i < tenant_domain_postfix_end_number; i++) {
				log.info("Creating apps for tenant: "+i);
				for (int j = application_postfix_start_number; j < application_postfix_end_number; j++) {
					log.info("  Creation of App: "+j +" started for tenant: "+i);
					try {
						applicationManager.createApplication(
								getTenantDomain(i) + "_" + reader.getProperty("application_key_prefix") + j,
								getTenantDomain(i));
					} catch (Exception e){
						log.error("Error occurred creating app: "+j+" for tenant: "+i+" at first attempt",e);
						Thread.sleep(application_create_sleep_time);
						try {
							applicationManager.createApplication(
									getTenantDomain(i) + "_" + reader.getProperty("application_key_prefix") + j,
									getTenantDomain(i));
						} catch (Exception e1){
							log.error("Error occurred creating app: "+j+" for tenant: "+i+" at 2nd attempt",e1);
							Thread.sleep(application_create_sleep_time);
							try {
								applicationManager.createApplication(
										getTenantDomain(i) + "_" + reader.getProperty("application_key_prefix") + j,
										getTenantDomain(i));
							} catch (Exception e2){
								log.error("Error occurred creating app: "+j+" for tenant: "+i+" at 3rd attempt",e2);
								Thread.sleep(application_create_sleep_time);
								applicationManager.createApplication(
										getTenantDomain(i) + "_" + reader.getProperty("application_key_prefix") + j,
										getTenantDomain(i));
							}
						}
					}
				}
				log.info("App creation for tenant: "+i+" is done");
				Thread.sleep(application_create_sleep_time);
			}

		} catch (Exception e){
			log.error("Error occurred!!!!",e);
		}

		log.info("Tenant creation is completed!!!");

	}

	private static void populateTenantInfoBean(TenantInfoBean bean, int i){
		bean.setTenantDomain(getTenantDomain(i));
		bean.setAdmin(reader.getProperty("tenant_admin_name"));
		bean.setAdminPassword(reader.getProperty("tenant_admin_password"));
		bean.setEmail(reader.getProperty("tenant_admin_email"));
		bean.setFirstname(reader.getProperty("tenant_admin_first_name"));
		bean.setLastname(reader.getProperty("tenant_admin_last_name"));
	}

	public static String getTenantDomain(int i){
		return reader.getProperty("tenant_domain_prefix") + i + ".com";
	}
}
