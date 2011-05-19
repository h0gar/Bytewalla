package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.prophet;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleDaemon;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol.status_report_reason_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleDeleteRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleDeliveredEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointIDPattern;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg.Registration;
import android.util.Log;

public class ProphetRegistration extends Registration {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String TAG = "ProphetRegistration";
	private ProphetBundleRouter router_;

	public ProphetRegistration(ProphetBundleRouter router_) {
		super(PROPHET_REGID, new EndpointIDPattern(router_.localEid()
				+ "/prophet"), Registration.failure_action_t.DEFER, 0, 0, "");
		this.router_ = router_;
		set_active(true);
	}

	@Override
	public void deliver_bundle(Bundle bundle) {
		Log.d(TAG, "Prophet bundle from " + bundle.source());
		router_.deliver_bundle(bundle);
		BundleDaemon.getInstance().post_at_head(new BundleDeliveredEvent(bundle, this));
		BundleDaemon.getInstance().post_at_head(new BundleDeleteRequest(bundle, status_report_reason_t.REASON_NO_ADDTL_INFO));
	}

}
