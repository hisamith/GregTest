package org.wso2.appfactory.greg.internal;

import org.wso2.carbon.registry.core.service.TenantRegistryLoader;

/**
 * Created by wdfdo1986 on 5/28/15.
 */
public class ServiceHolder {

	private TenantRegistryLoader tenantRegistryLoader = null;
	private static ServiceHolder serviceHolder = new ServiceHolder();


	public static ServiceHolder getInstance(){
		return serviceHolder;
	}

	private ServiceHolder(){}

	protected void unsetRegistryLoader() {
		this.tenantRegistryLoader = null;
	}

	public TenantRegistryLoader getTenantRegistryLoader() {
		return tenantRegistryLoader;
	}

	public void setTenantRegistryLoader(TenantRegistryLoader tenantRegistryLoader) {
		this.tenantRegistryLoader = tenantRegistryLoader;
	}
}
