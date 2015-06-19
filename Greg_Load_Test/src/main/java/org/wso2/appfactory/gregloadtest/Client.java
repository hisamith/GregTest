package org.wso2.appfactory.gregloadtest;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by wdfdo1986 on 5/22/15.
 */
public class Client {

	public static final int NUM_OF_APPS_TO_IGNORE_FOR_CACHED = 2;
	private static ConfigReader reader = ConfigReader.getInstance();
	public static void main(String[] args){
		final Log log = LogFactory.getLog(Client.class);
		try {
			log.info("********************* Starting *********************  ");
			System.setProperty("javax.net.ssl.trustStore", reader.getProperty("jksLocation"));
			System.setProperty("javax.net.ssl.trustStorePassword",reader.getProperty("jkspassword"));
			int no_of_tenants_to_load = Integer.parseInt(ConfigReader.getInstance().getProperty("no_of_tenants_to_load"));
			int no_of_applications = Integer.parseInt(ConfigReader.getInstance().getProperty("no_of_rxt_instances"));
			String outputFile = ConfigReader.getInstance().getProperty("output_csv_file");
			if (args.length > 0) {
				try {
					no_of_tenants_to_load = Integer.parseInt(args[0]);
					if(args.length > 1) {
						no_of_applications = Integer.parseInt(args[1]);
					}
				} catch (NumberFormatException e) {
					log.error("Arguments must be integers.");
					System.exit(1);
				}
			}
			log.info("---------------------------------------");
			log.info("# of tenants: "+no_of_tenants_to_load);
			log.info("# of applications: "+no_of_applications);
			log.info("---------------------------------------");
			ApplicationManager applicationManager = new ApplicationManager();
			int tenant_domain_postfix_start_number =
					Integer.parseInt(ConfigReader.getInstance().getProperty("tenant_domain_postfix_start_number"));
			int application_postfix_start_number =
					Integer.parseInt(ConfigReader.getInstance().getProperty("application_postfix_start_number"));
			int tenant_domain_to_load_postfix_end_number = tenant_domain_postfix_start_number + no_of_tenants_to_load;
			int application_postfix_end_number = application_postfix_start_number + no_of_applications;
			TenantManager tenantManager = new TenantManager();
			long tenant_load_sleep_time = Long.parseLong(reader.getProperty("tenant_load_sleep_time"));
			log.info("Loading tenants!!!...");
			for (int i = tenant_domain_postfix_start_number; i < tenant_domain_to_load_postfix_end_number; i++) {
				try {
					log.info("Loading tenant: "+i);
					tenantManager.loadTenant(Initializer.getTenantDomain(i));
				} catch (Exception e) {
					log.error("Error while loading tenant: " + i, e);
				}
				Thread.sleep(tenant_load_sleep_time);
			}

			long timeForGetArtifact = 0;
			long timeForGetAttribute = 0;
			long afterCachedTimeForGetArtifact = 0;
			long afterCachedTimeForGetAttribute = 0;

			for (int tenantId = tenant_domain_postfix_start_number; tenantId < tenant_domain_to_load_postfix_end_number; tenantId++) {
				log.info("Getting Artifacts of tenant: "+tenantId);
				for (int appId = application_postfix_start_number; appId < application_postfix_end_number; appId++) {
					String applicationKey = Initializer.getTenantDomain(tenantId) + "_" + reader.getProperty("application_key_prefix") + appId;
					String result;
					try {
						result = applicationManager.getArtifact(applicationKey, Initializer.getTenantDomain(tenantId));
					} catch (Exception e){
						try {
							result = applicationManager.getArtifact(applicationKey, Initializer.getTenantDomain(tenantId));
						} catch (Exception e2){
							result = applicationManager.getArtifact(applicationKey, Initializer.getTenantDomain(tenantId));
						}
					}
					String[] resultArr = result.split(",");
					timeForGetArtifact += Long.parseLong(resultArr[0]);
					timeForGetAttribute += Long.parseLong(resultArr[1]);

					// Calculating time ignoring first 2 artifact retrieving time to get time without cache
					if(no_of_applications > NUM_OF_APPS_TO_IGNORE_FOR_CACHED &&
					   ( (appId - application_postfix_start_number) > (NUM_OF_APPS_TO_IGNORE_FOR_CACHED -1) ) ) {
						afterCachedTimeForGetArtifact += Long.parseLong(resultArr[0]);
						afterCachedTimeForGetAttribute += Long.parseLong(resultArr[1]);
					} else {
						log.info("Ignoring time to get artifact for artifact id: "+appId+
						         " value:"+ Long.parseLong(resultArr[0])+" for cached output");
					}
				}
			}

			timeForGetArtifact = avgWithCachedResults(no_of_tenants_to_load, no_of_applications, timeForGetArtifact);
			timeForGetAttribute = avgWithCachedResults(no_of_tenants_to_load, no_of_applications, timeForGetAttribute);

			// Calculating time ignoring first 2 artifact retrieving time to get time without cache
			if (no_of_applications > NUM_OF_APPS_TO_IGNORE_FOR_CACHED) {
				afterCachedTimeForGetArtifact = avgIgnoringCachedResults(no_of_tenants_to_load, no_of_applications, afterCachedTimeForGetArtifact);
				afterCachedTimeForGetAttribute = avgIgnoringCachedResults(no_of_tenants_to_load, no_of_applications, afterCachedTimeForGetAttribute);
			}

			log.info("############################################\n");
			log.info("Time to get artifact : " + timeForGetArtifact);
			log.info("Time to get artifact attribute : " + timeForGetAttribute);
			log.info("Time to get artifact(Removing cached) : " + afterCachedTimeForGetArtifact);
			log.info("Time to get artifact attribute(Removing cached) : " + afterCachedTimeForGetAttribute);
			log.info("********************* END *********************  ");
			try {
				CSVWriter out = new CSVWriter(new BufferedWriter(new FileWriter(outputFile, true)));
				String[] results = {String.valueOf(no_of_tenants_to_load), String.valueOf(no_of_applications),
				                    String.valueOf(timeForGetArtifact), String.valueOf(timeForGetAttribute),
				                    String.valueOf(afterCachedTimeForGetArtifact), String.valueOf(afterCachedTimeForGetAttribute)

				};
				out.writeNext(results);
				out.close();
			} catch (IOException e) {
				log.error("Exception occurred while writing", e);
				//exception handling left as an exercise for the reader
			}

		} catch (Exception e){
			log.error("Exception occurred while executing",e);
		}
	}

	private static long avgWithCachedResults(int no_of_tenants_to_load, int no_of_applications,
	                                         long timeForGetArtifact) {
		return timeForGetArtifact / (no_of_tenants_to_load * no_of_applications);
	}

	private static long avgIgnoringCachedResults(int no_of_tenants_to_load, int no_of_applications,
	                                             long afterCachedTimeForGetArtifact) {
		return afterCachedTimeForGetArtifact /
                (no_of_tenants_to_load * (no_of_applications - NUM_OF_APPS_TO_IGNORE_FOR_CACHED));
	}
}
