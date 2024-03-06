package io.openems.edge.bridge.http.dummy;

import java.lang.reflect.InvocationTargetException;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentServiceObjects;

import io.openems.common.utils.ReflectionUtils;
import io.openems.edge.bridge.http.api.BridgeHttp;
import io.openems.edge.bridge.http.api.BridgeHttpFactory;

public class DummyBridgeHttpFactory extends BridgeHttpFactory {

	private BridgeHttp bridgeHttp;

	public DummyBridgeHttpFactory() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		super();
		ReflectionUtils.setAttribute(BridgeHttpFactory.class, this, "csoBridgeHttp", new DummyBridgeHttpCso(this));
	}
	
	public void setBridgeHttp(BridgeHttp bridgeHttp) {
		this.bridgeHttp = bridgeHttp;
	}

	private static class DummyBridgeHttpCso implements ComponentServiceObjects<BridgeHttp> {

		private DummyBridgeHttpFactory factory;

		public DummyBridgeHttpCso() {}
		public DummyBridgeHttpCso(DummyBridgeHttpFactory factory) {
			this.factory = factory;
		}
		@Override
		public BridgeHttp getService() {
			return factory != null ? factory.bridgeHttp : new DummyBridgeHttp();
		}

		@Override
		public void ungetService(BridgeHttp service) {
			// empty for tests
		}

		@Override
		public ServiceReference<BridgeHttp> getServiceReference() {
			// empty for tests
			return null;
		}
	}

}
