/*
 *	  This file is part of the Bytewalla Project
 *    More information can be found at "http://www.tslab.ssvl.kth.se/csd/projects/092106/".
 *    
 *    Copyright 2009 Telecommunication Systems Laboratory (TSLab), Royal Institute of Technology, Sweden.
 *    
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *    
 */
package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleAcceptRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleCancelRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleDeleteRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleDeliveredEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleExpiredEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleFreeEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleInjectRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleInjectedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleQueryRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleQueuedQueryRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleQueuedReportEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleReceivedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleReportEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleSendCancelledEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleSendRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleTransmittedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.CLAParametersQueryRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.CLAParametersReportEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.CLAParamsSetEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.CLASetParamsRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ContactAttributeChangedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ContactDownEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ContactQueryRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ContactReportEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ContactUpEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.CustodySignalEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.CustodyTimeoutEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.EIDReachableQueryRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.EIDReachableReportEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.IfaceAttributesQueryRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.IfaceAttributesReportEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkAttributeChangedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkAttributesQueryRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkAttributesReportEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkAvailableEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkCreatedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkDeleteRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkDeletedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkQueryRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkReconfigureRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkReportEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkStateChangeRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkUnavailableEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.NewEIDReachableEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ReassemblyCompletedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.RegistrationAddedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.RegistrationDeleteRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.RegistrationExpiredEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.RegistrationRemovedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.RouteAddEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.RouteDelEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.RouteQueryRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.RouteReportEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.SetLinkDefaultsRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ShutdownRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.StatusRequest;
import android.util.Log;

/**
 * Abstract class for handling various Bundle Events.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public abstract class BundleEventHandler {

	private static final String TAG = "BundleEventHandler";
	/**
	 * Main event handler
	 */
	 public abstract void handle_event(BundleEvent event);

	/**
	 * Event dispatcher function. This will dispatch input event to handle routine according to the event's type code.
	 */
	protected void dispatch_event(BundleEvent e)
	{
		Log.d(TAG, String.format("dispatching event (%s) %s", 
				          e.toString(),  
				          e.type().getCaption()
				          ));
	
		switch(e.type())
		{
		case BUNDLE_RECEIVED:
	        handle_bundle_received((BundleReceivedEvent)e);
	        break;

	    case BUNDLE_TRANSMITTED:
	        handle_bundle_transmitted((BundleTransmittedEvent)e);
	        break;

	    case BUNDLE_DELIVERED:
	        handle_bundle_delivered((BundleDeliveredEvent)e);
	        break;

	    case BUNDLE_EXPIRED:
	        handle_bundle_expired((BundleExpiredEvent)e);
	        break;
	        
	    case BUNDLE_FREE:
	        handle_bundle_free((BundleFreeEvent)e);
	        break;

	    case BUNDLE_SEND:
	        handle_bundle_send((BundleSendRequest)e);
	        break;

	    case BUNDLE_CANCEL:
	        handle_bundle_cancel((BundleCancelRequest)e);
	        break;

	    case BUNDLE_CANCELLED:
	        handle_bundle_cancelled((BundleSendCancelledEvent)e);
	        break;

	    case BUNDLE_INJECT:
	        handle_bundle_inject((BundleInjectRequest)e);
	        break;

	    case BUNDLE_INJECTED:
	        handle_bundle_injected((BundleInjectedEvent)e);
	        break;

	    case BUNDLE_DELETE:
	        handle_bundle_delete((BundleDeleteRequest)e);
	        break;

	    case BUNDLE_ACCEPT_REQUEST:
	        handle_bundle_accept((BundleAcceptRequest)e);
	        break;

	    case BUNDLE_QUERY:
	        handle_bundle_query((BundleQueryRequest)e);
	        break;

	    case BUNDLE_REPORT:
	        handle_bundle_report((BundleReportEvent)e);
	        break;
	        
	  
	        
	    case REGISTRATION_ADDED:
	        handle_registration_added((RegistrationAddedEvent)e);
	        break;

	    case REGISTRATION_REMOVED:
	        handle_registration_removed((RegistrationRemovedEvent)e);
	        break;

	    case REGISTRATION_EXPIRED:
	        handle_registration_expired((RegistrationExpiredEvent)e);
	        break;
	 
	    case REGISTRATION_DELETE:
	        handle_registration_delete((RegistrationDeleteRequest)e);
	        break;

	   case ROUTE_ADD:
	        handle_route_add((RouteAddEvent)e);
	        break;

	    case ROUTE_DEL:
	        handle_route_del((RouteDelEvent)e);
	        break;

	    case ROUTE_QUERY:
	        handle_route_query((RouteQueryRequest)e);
	        break;

	    case ROUTE_REPORT:
	        handle_route_report((RouteReportEvent)e);
	        break;

	    case CONTACT_UP:
	        handle_contact_up((ContactUpEvent)e);
	        break;

	    case CONTACT_DOWN:
	        handle_contact_down((ContactDownEvent)e);
	        break;

	    case CONTACT_QUERY:
	        handle_contact_query((ContactQueryRequest)e);
	        break;

	    case CONTACT_REPORT:
	        handle_contact_report((ContactReportEvent)e);
	        break;

	    case CONTACT_ATTRIB_CHANGED:
	        handle_contact_attribute_changed((ContactAttributeChangedEvent)e);
	        break;

	    case LINK_CREATED:
	        handle_link_created((LinkCreatedEvent)e);
	        break;

	    case LINK_DELETED:
	        handle_link_deleted((LinkDeletedEvent)e);
	        break;

	    case LINK_AVAILABLE:
	        handle_link_available((LinkAvailableEvent)e);
	        break;

	    case LINK_UNAVAILABLE:
	        handle_link_unavailable((LinkUnavailableEvent)e);
	        break;

	    case LINK_STATE_CHANGE_REQUEST:
	        handle_link_state_change_request((LinkStateChangeRequest)e);
	        break;

	   

	    case LINK_DELETE:
	        handle_link_delete((LinkDeleteRequest)e);
	        break;

	    case LINK_RECONFIGURE:
	        handle_link_reconfigure((LinkReconfigureRequest)e);
	        break;

	    case LINK_QUERY:
	        handle_link_query((LinkQueryRequest)e);
	        break;

	    case LINK_REPORT:
	        handle_link_report((LinkReportEvent)e);
	        break;
	        
	    case LINK_ATTRIB_CHANGED:
	        handle_link_attribute_changed((LinkAttributeChangedEvent)e);
	        break;

	    case REASSEMBLY_COMPLETED:
	        handle_reassembly_completed((ReassemblyCompletedEvent)e);
	        break;

	    case CUSTODY_SIGNAL:
	        handle_custody_signal((CustodySignalEvent)e);
	        break;

	    case CUSTODY_TIMEOUT:
	        handle_custody_timeout((CustodyTimeoutEvent)e);
	        break;

	    case DAEMON_SHUTDOWN:
	        handle_shutdown_request((ShutdownRequest)e);
	        break;

	    case DAEMON_STATUS:
	        handle_status_request((StatusRequest)e);
	        break;

	    case CLA_SET_PARAMS:
	        handle_cla_set_params((CLASetParamsRequest)e);
	        break;

	    case CLA_PARAMS_SET:
	        handle_cla_params_set((CLAParamsSetEvent)e);
	        break;

	    case CLA_SET_LINK_DEFAULTS:
	        handle_set_link_defaults((SetLinkDefaultsRequest)e);
	        break;

	    case CLA_EID_REACHABLE:
	        handle_new_eid_reachable((NewEIDReachableEvent)e);
	        break;

	    case CLA_BUNDLE_QUEUED_QUERY:
	        handle_bundle_queued_query((BundleQueuedQueryRequest)e);
	        break;

	    case CLA_BUNDLE_QUEUED_REPORT:
	        handle_bundle_queued_report((BundleQueuedReportEvent)e);
	        break;

	    case CLA_EID_REACHABLE_QUERY:
	        handle_eid_reachable_query((EIDReachableQueryRequest)e);
	        break;

	    case CLA_EID_REACHABLE_REPORT:
	        handle_eid_reachable_report((EIDReachableReportEvent)e);
	        break;

	    case CLA_LINK_ATTRIB_QUERY:
	        handle_link_attributes_query((LinkAttributesQueryRequest)e);
	        break;

	    case CLA_LINK_ATTRIB_REPORT:
	        handle_link_attributes_report((LinkAttributesReportEvent)e);
	        break;

	    case CLA_IFACE_ATTRIB_QUERY:
	        handle_iface_attributes_query((IfaceAttributesQueryRequest)e);
	        break;

	    case CLA_IFACE_ATTRIB_REPORT:
	        handle_iface_attributes_report((IfaceAttributesReportEvent)e);
	        break;

	    case CLA_PARAMS_QUERY:
	        handle_cla_parameters_query((CLAParametersQueryRequest)e);
	        break;

	    case CLA_PARAMS_REPORT:
	        handle_cla_parameters_report((CLAParametersReportEvent)e);
	        break;

	    default:
	    	Log.e(TAG, String.format("unimplemented event type %s, code = ", 
	    			 e.type().getCaption(), 
	    			 e.type().getCode())
	    			 
	    	);
	        
	    } 
		
		
		
		
	}

	/**
	 * "Default event handler for new bundle arrivals." [DTN2]
	 */
	abstract protected void handle_bundle_received(BundleReceivedEvent event);

	/**
	 * "Default event handler when bundles are transmitted." [DTN2]
	 */
	abstract protected void handle_bundle_transmitted(
			BundleTransmittedEvent event);

	/**
	 * "Default event handler when bundles are locally delivered." [DTN2]
	 */
	abstract protected void handle_bundle_delivered(BundleDeliveredEvent event);

	/**
	 * "Default event handler when bundles expire." [DTN2]
	 */
	abstract protected void handle_bundle_expired(BundleExpiredEvent event);

	/**
	 * "Default event handler when bundles are free (i.e. no more references)." [DTN2]
	 */
	abstract protected void handle_bundle_free(BundleFreeEvent event);

	/**
	 * "Default event handler for bundle send requests" [DTN2]
	 */
	abstract protected void handle_bundle_send(BundleSendRequest event);

	/**
	 * "Default event handler for send bundle request cancellations" [DTN2]
	 */
	abstract protected void handle_bundle_cancel(BundleCancelRequest event);

	/**
	 * "Default event handler for bundle cancellations." [DTN2]
	 */
	abstract protected void handle_bundle_cancelled(
			BundleSendCancelledEvent event);

	/**
	 * "Default event handler for bundle inject requests." [DTN2]
	 */
	abstract protected void handle_bundle_inject(BundleInjectRequest event);

	/**
	 * "Default event handler for bundle injected events." [DTN2]
	 */
	abstract protected void handle_bundle_injected(BundleInjectedEvent event);

	/**
	 * "Default event handler for bundle delete requests." [DTN2]
	 */
	abstract protected void handle_bundle_delete(BundleDeleteRequest request);

	/**
	 * "Default event handler for a bundle accept request probe." [DTN2]
	 */
	abstract protected void handle_bundle_accept(BundleAcceptRequest event);

	/**
	 * "Default event handler for bundle query requests." [DTN2]
	 */
	abstract protected void handle_bundle_query(BundleQueryRequest request);

	/**
	 * "Default event handler for bundle reports." [DTN2]
	 */
	abstract protected void handle_bundle_report(BundleReportEvent request);

	
	/**
	 * "Default event handler when a new application registration arrives." [DTN2]
	 */
	abstract protected void handle_registration_added(
			RegistrationAddedEvent event);

	/**
	 * "Default event handler when a registration is removed." [DTN2]
	 */
	abstract protected void handle_registration_removed(
			RegistrationRemovedEvent event);

	/**
	 * "Default event handler when a registration expires." [DTN2]
	 */
	abstract protected void handle_registration_expired(
			RegistrationExpiredEvent event);

	/**
	 * "Default event handler when a registration is to be deleted." [DTN2]
	 */
	abstract protected void handle_registration_delete(
			RegistrationDeleteRequest event);

	/**
	 * "Default event handler when a new contact is up." [DTN2]
	 */
	abstract protected void handle_contact_up(ContactUpEvent event);

	/**
	 * "Default event handler when a contact is down." [DTN2]
	 */
	abstract protected void handle_contact_down(ContactDownEvent event);

	/**
	 * "Default event handler for contact query requests." [DTN2]
	 */
	abstract protected void handle_contact_query(ContactQueryRequest request);

	/**
	 * "Default event handler for contact reports." [DTN2]
	 */
	abstract protected void handle_contact_report(ContactReportEvent request);

	/**
	 * "Default event handler for contact attribute changes." [DTN2]
	 */
	abstract protected void handle_contact_attribute_changed(
			ContactAttributeChangedEvent event);

	/**
	 * "Default event handler when a new link is created." [DTN2]
	 */
	abstract protected void handle_link_created(LinkCreatedEvent event);

	/**
	 * "Default event handler when a link is deleted." [DTN2]
	 */
	abstract protected void handle_link_deleted(LinkDeletedEvent event);

	/**
	 * "Default event handler when link becomes available." [DTN2]
	 */
	abstract protected void handle_link_available(LinkAvailableEvent event);

	/**
	 * "Default event handler when a link is unavailable." [DTN2]
	 */
	abstract protected void handle_link_unavailable(LinkUnavailableEvent event);

	/**
	 * "Default event handler for link state change requests." [DTN2]
	 */
	abstract protected void handle_link_state_change_request(
			LinkStateChangeRequest req);

	/**
	 * "Default event handler for link delete requests." [DTN2]
	 */
	abstract protected void handle_link_delete(LinkDeleteRequest request);

	/**
	 * "Default event handler for link reconfigure requests." [DTN2]
	 */
	abstract protected void handle_link_reconfigure(
			LinkReconfigureRequest request);

	/**
	 * "Default event handler for link query requests." [DTN2]
	 */
	abstract protected void handle_link_query(LinkQueryRequest request);

	/**
	 * "Default event handler for link reports." [DTN2]
	 */
	abstract protected void handle_link_report(LinkReportEvent request);

	/**
	 * "Default event handler for link attribute changes." [DTN2]
	 */
	abstract protected void handle_link_attribute_changed(
			LinkAttributeChangedEvent event);

	/**
	 * "Default event handler when reassembly is completed." [DTN2]
	 */
	abstract protected void handle_reassembly_completed(
			ReassemblyCompletedEvent event);

	/**
	 * "Default event handler when a new route is added by the command or
	 * management interface." [DTN2]
	 */
	abstract protected void handle_route_add(RouteAddEvent event);

	/**
	 * "Default event handler when a route is deleted by the command or
	 * management interface." [DTN2]
	 */
	abstract protected void handle_route_del(RouteDelEvent event);

	/**
	 * "Default event handler for static route query requests." [DTN2]
	 */
	abstract protected void handle_route_query(RouteQueryRequest request);

	/**
	 * "Default event handler for static route reports." [DTN2]
	 */
	abstract protected void handle_route_report(RouteReportEvent request);

	/**
	 * "Default event handler when custody signals are received." [DTN2]
	 */
	abstract protected void handle_custody_signal(CustodySignalEvent event);

	/**
	 * "Default event handler when custody transfer timers expire." [DTN2]
	 */
	abstract protected void handle_custody_timeout(CustodyTimeoutEvent event);

	/**
	 * "Default event handler for shutdown requests." [DTN2]
	 */
	abstract protected void handle_shutdown_request(ShutdownRequest event);

	/**
	 * "Default event handler for status requests." [DTN2]
	 */
	abstract protected void handle_status_request(StatusRequest event);

	/**
	 * "Default event handler for CLA parameter set requests." [DTN2]
	 */
	abstract protected void handle_cla_set_params(CLASetParamsRequest event);

	/**
	 * "Default event handler for CLA parameters set events." [DTN2]
	 */
	abstract protected void handle_cla_params_set(CLAParamsSetEvent event);

	/**
	 * "Default event handler for set link defaults requests." [DTN2]
	 */
	abstract protected void handle_set_link_defaults(
			SetLinkDefaultsRequest event);

	/**
	 * "Default event handler for new EIDs discovered by CLA." [DTN2]
	 */
	abstract protected void handle_new_eid_reachable(NewEIDReachableEvent event);

	/**
	 * "Default event handlers for queries to and reports from the CLA." [DTN2]
	 */
	abstract protected void handle_bundle_queued_query(
			BundleQueuedQueryRequest event);

	/**
	 * "Default event handlers for bundle queue report." [DTN2]
	 * @param event
	 */
	abstract protected void handle_bundle_queued_report(
			BundleQueuedReportEvent event);

	/**
	 * "Default event handlers for new EID reachable." [DTN2]
	 * @param event
	 */
	abstract protected void handle_eid_reachable_query(
			EIDReachableQueryRequest event);

	/**
	 * "Default event handlers for new EID reachable report." [DTN2]
	 * @param event
	 */
	abstract protected void handle_eid_reachable_report(
			EIDReachableReportEvent event);

	/**
	 * "Default event handlers for link attribute query." [DTN2]
	 * @param event
	 */
	abstract protected void handle_link_attributes_query(
			LinkAttributesQueryRequest event);

	/**
	 * "Default event handler for link attribute query report." [DTN2]
	 * @param event
	 */
	abstract protected void handle_link_attributes_report(
			LinkAttributesReportEvent event);

	/**
	 * "Default event handler for interface attribute query." [DTN2]
	 * @param event
	 */
	abstract protected void handle_iface_attributes_query(
			IfaceAttributesQueryRequest event);

	/**
	 * "Default event handler for interface attribute query report." [DTN2]
	 * @param event
	 */
	abstract protected void handle_iface_attributes_report(
			IfaceAttributesReportEvent event);

	/**
	 * "Default event handler for cla parameters query." [DTN2]
	 * @param event
	 */
	abstract protected void handle_cla_parameters_query(
			CLAParametersQueryRequest event);

	/**
	 * "Default event handler for cla parameters query report." [DTN2] 
	 * @param event
	 */
	abstract protected void handle_cla_parameters_report(
			CLAParametersReportEvent event);
};
