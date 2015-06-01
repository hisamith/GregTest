package org.wso2.appfactory.greg.internal;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.wso2.appfactory.greg.listners.CopyWebappListner;
import org.wso2.carbon.registry.core.service.TenantRegistryLoader;
import org.wso2.carbon.stratos.common.listeners.TenantMgtListener;

/**
 * Created by wdfdo1986 on 5/28/15.
 */

/**
 *
 * @scr.component name=
 *                "org.wso2.appfactory.greg.internal.GregWebServiceServiceComponent"
 *                immediate="true"
 * @scr.reference name="registry.loader.default"
 *                interface="org.wso2.carbon.registry.core.service.TenantRegistryLoader"
 *                cardinality="1..1"
 *                policy="dynamic"
 *                bind="setRegistryLoader"
 *                unbind="unsetRegistryLoader"
 */
public class GregWebServiceServiceComponent {


	private static BundleContext bundleContext;

	protected void activate(ComponentContext context) {
		GregWebServiceServiceComponent.bundleContext = context.getBundleContext();
		bundleContext.registerService(TenantMgtListener.class.getName(), new CopyWebappListner(),null);
	}

	protected void unsetRegistryLoader(TenantRegistryLoader tenantRegistryLoader) {
		ServiceHolder.getInstance().unsetRegistryLoader();
	}

	public void setRegistryLoader(TenantRegistryLoader tenantRegistryLoader) {
		ServiceHolder.getInstance().setTenantRegistryLoader(tenantRegistryLoader);
	}
}
