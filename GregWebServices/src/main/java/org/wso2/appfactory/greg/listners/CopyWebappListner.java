package org.wso2.appfactory.greg.listners;

/**
 * Created by wdfdo1986 on 6/1/15.
 */

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.stratos.common.beans.TenantInfoBean;
import org.wso2.carbon.stratos.common.exception.StratosException;
import org.wso2.carbon.stratos.common.listeners.TenantMgtListener;
import org.wso2.carbon.utils.CarbonUtils;

import java.io.File;
import java.io.IOException;

public class CopyWebappListner implements TenantMgtListener {

	private static Log log = LogFactory.getLog(CopyWebappListner.class);
	@Override
	public void onTenantCreate(TenantInfoBean tenantInfoBean) throws StratosException {
		String resourcesLocation = CarbonUtils.getCarbonHome() + "/repository/resources/tenantwebapps";
		String webappsLocation = CarbonUtils.getCarbonHome() + "/repository/tenants/" + tenantInfoBean.getTenantId() + "/webapps";
		File source = new File(resourcesLocation);
		File destination = new File(webappsLocation);
		try {
			destination.mkdirs();
			FileUtils.copyDirectory(source, destination);
		} catch (IOException e) {
			log.error("Exception occured while copying the webapps");
		}
	}

	@Override
	public void onTenantUpdate(TenantInfoBean tenantInfoBean) throws StratosException {
		//Do nothing
	}

	@Override
	public void onTenantRename(int i, String s, String s1) throws StratosException {
		//Do nothing
	}

	@Override
	public void onTenantInitialActivation(int i) throws StratosException {
		//Do nothing
	}

	@Override
	public void onTenantActivation(int i) throws StratosException {
		//Do nothing
	}

	@Override
	public void onTenantDeactivation(int i) throws StratosException {
		//Do nothing
	}

	@Override
	public void onSubscriptionPlanChange(int i, String s, String s1) throws StratosException {
		//Do nothing
	}

	@Override
	public int getListenerOrder() {
		return 10;
	}
}
