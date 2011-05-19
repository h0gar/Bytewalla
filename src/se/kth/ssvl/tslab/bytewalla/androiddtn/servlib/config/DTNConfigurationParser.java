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
package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.DiscoveriesSetting.AnnounceEntry;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.DiscoveriesSetting.DiscoveryEntry;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.DiscoveriesSetting.address_family_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.InterfacesSetting.InterfaceEntry;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.LinksSetting.LinkEntry;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.RoutesSetting.RouteEntry;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.StorageSetting.storage_type_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link.link_type_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.BundleRouter.router_type_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.List;

/**
 * The parser class for DTNConfiguration. This parser uses XML DOM model to parse the configuration file written in XML  
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 * 
 */
public class DTNConfigurationParser {


	/**
	 * The TAG name to parse for the DTNConfigration element
	 */
	private final static String DTNConfigurationTagName   = "tns:DTNConfiguration";
	
	/**
	 * The TAG name to parse for the StorageSetting element
	 */
	private final static String StorageSettingTagName     = "tns:StorageSetting";
	
	/**
	 * The TAG name to parse for the InterfacesSetting element
	 */
	private final static String InterfacesSettingTagName  = "tns:InterfacesSetting";
	
	/**
	 * The TAG name to parse for the LinksSetting element
	 */
	private final static String LinksSettingTagName       = "tns:LinksSetting";
	
	/**
	 * The TAG name to parse for the RoutesSetting element
	 */
	private final static String RoutesSettingTagName      = "tns:RoutesSetting";
	
	/**
	 * The TAG name to parse for the DiscoveriesSetting element
	 */
	private final static String DiscoveriesSettingTagName = "tns:DiscoveriesSetting"; 
	
	/**
	 * The TAG name to parse for the Link element
	 */
	private final static String LinkTagName = "tns:Link";
	
	/**
	 * The TAG name to parse for the Interface element
	 */
	private final static String InterfaceTagName = "tns:Interface";
	
	/**
	 * The TAG name to parse for the Route element
	 */
	private final static String RouteTagName = "tns:Route";
	
	/**
	 * The TAG name to parse for the Discovery element
	 */
	private final static String DiscoveryTagName = "tns:Discovery";
	
	/**
	 * The TAG name to parse for the Announce element
	 */
	private final static String AnnounceTagName = "tns:Announce";
	
	/**
	 * Main routine for parsing XML Configuration file input stream
	 * @param xml_stream the input stream of xml configuration file
	 * @return the result DTNConfiguration object filled with data from the stream
	 * @throws InvalidDTNConfigurationException
	 */
	public static DTNConfiguration parse_config_file(InputStream xml_stream) throws InvalidDTNConfigurationException 
	{
		 DTNConfiguration config_ = new DTNConfiguration();
		 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	       
	        try {
	            DocumentBuilder builder = factory.newDocumentBuilder();
	            Document dom = builder.parse(xml_stream);
	            Element DTNConfigurationElement = dom.getDocumentElement();
	            
	            if (DTNConfigurationElement.getTagName().equals(DTNConfigurationTagName))
	            {
	            	NodeList configurationNodes = DTNConfigurationElement.getChildNodes();
	            	
	            	for (int i=0; i < configurationNodes.getLength(); i++)
	            	{
	            		if (configurationNodes.item(i).getNodeType() == Node.ELEMENT_NODE)
	            		{
	            			process_config_element((Element) configurationNodes.item(i), config_);
	            		}
	            		
	            	}
	            }
	            else throw new InvalidDTNConfigurationException("The expected DTNConfiguration is missing");
	       
	        } catch (Exception e) {
	            throw new InvalidDTNConfigurationException(e.toString());
	        } 
	       
		
		return config_;
		
	}
	
	/**
	 * Routine to process each individual config element
	 * @param config_element the input element received
	 * @param config the configuration file we will put output on
	 * @throws Exception
	 */
	private static void process_config_element(Element config_element, DTNConfiguration config ) throws Exception
	{
		if ( config_element.getTagName().equals(StorageSettingTagName))
		{
			process_storage_config(config_element, config);
		}
		
		else if ( config_element.getTagName().equals(InterfacesSettingTagName))
		{
			process_interfaces_config(config_element, config);
			return;
		}
		
		else if ( config_element.getTagName().equals(LinksSettingTagName))
		{
			process_links_config(config_element, config);
			return;
		}
		
		else if ( config_element.getTagName().equals(RoutesSettingTagName))
		{
			process_routes_config(config_element, config);
			return;	
		}
		
		else if ( config_element.getTagName().equals(DiscoveriesSettingTagName))
		{
			process_discoveries_config(config_element, config);
			return;
		}
		
		
		else throw new Exception("Unknow configuration option " + config_element.getTagName());
		
	}
	
	/**
	 * Routine to process config relating to interface element
	 * @param config_element the input element received
	 * @param config the configuration file we will put output on
	 * @throws InvalidDTNConfigurationException
	 */
	private static void process_interfaces_config(Element config_element, DTNConfiguration config) throws InvalidDTNConfigurationException
	{
		NodeList childNodes = config_element.getChildNodes();
		
		List<InterfaceEntry> interface_entries = config.interfaces_setting_.interface_entries();
		for(int i=0; i < childNodes.getLength(); i++)
		{
			
			if( childNodes.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				Element e = (Element) childNodes.item(i);
				
				if (e.getTagName().equals(InterfaceTagName))
				{
					InterfaceEntry ie = new InterfaceEntry();
					Attr conv_layer_type = e.getAttributeNode("conv_layer_type");
					if (conv_layer_type.getValue().equals("tcp"))
					{
						ie.set_conv_layer_type(conv_layer_type_t.TCP);
					}
					Attr id = e.getAttributeNode("id");
					ie.set_id(id.getValue());
					
					Attr local_port = e.getAttributeNode("local_port");
					if (local_port != null)
					{
						ie.set_local_port(Short.parseShort(local_port.getValue()));
						ie.set_fixed_local_port(true);
					}
					else
					{
						ie.set_local_port((short)-1);
						ie.set_fixed_local_port(false);
					}
					
					interface_entries.add(ie);
					
				}
				else throw new InvalidDTNConfigurationException("Interface Element unknown");
					
				
			}
			
		}
		
		return;
	}

	/**
	 * Routine to processs configuration related to links configuration
	 * @param config_element the input element received
	 * @param config the configuration file we will put output on
	 * @throws InvalidDTNConfigurationException
	 */
	private static void process_links_config(Element config_element, DTNConfiguration config) throws InvalidDTNConfigurationException
	{
		NodeList childNodes = config_element.getChildNodes();
		
		List<LinkEntry> link_entries = config.links_setting().link_entries();
		for(int i=0; i < childNodes.getLength(); i++)
		{
			
			if( childNodes.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				Element e = (Element) childNodes.item(i);
				
				if (e.getTagName().equals(LinkTagName))
				{
					LinkEntry link_entry = new LinkEntry();
					
					Attr id = e.getAttributeNode("id");
					link_entry.set_id(id.getValue());
					
					Attr conv_layer_type = e.getAttributeNode("conv_layer_type");
					if (conv_layer_type.getValue().equals("tcp"))
					{
						link_entry.set_conv_layer_type(conv_layer_type_t.TCP);
					}
					
					
					Attr dest = e.getAttributeNode("dest");
					link_entry.set_des(dest.getValue());
								
					Attr type = e.getAttributeNode("type");
					
					String typeText = type.getValue();
					if (typeText.equals("ALWAYSON"))
					{
						link_entry.set_type(link_type_t.ALWAYSON);
					} 
					
					else if (typeText.equals("ONDEMAND"))
					{
						link_entry.set_type(link_type_t.ONDEMAND);
					}
					
					else if (typeText.equals("SCHEDULED"))
					{
						link_entry.set_type(link_type_t.SCHEDULED);
					}
					
					
					else if (typeText.equals("OPPORTUNISTIC"))
					{
						link_entry.set_type(link_type_t.OPPORTUNISTIC);
					}
					
					
					link_entries.add(link_entry);
					
				}
				else throw new InvalidDTNConfigurationException("Link Element unknown");
					
				
			}
			
		}
		
		return;
	}

	/**
	 * Routine to process config related to Route
	 * @param config_element the input element received
	 * @param config the configuration file we will put output on
	 * @throws InvalidDTNConfigurationException
	 */
	private static void process_routes_config(Element config_element, DTNConfiguration config) throws InvalidDTNConfigurationException
	{
		NodeList childNodes = config_element.getChildNodes();
		
		Attr router_type = config_element.getAttributeNode("router_type");
		
		if (router_type.getValue().equals("static"))
		{
			config.routes_setting().set_router_type(router_type_t.STATIC_BUNDLE_ROUTER);
		}
		else if (router_type.getValue().equals("prophet"))
		{
			config.routes_setting().set_router_type(router_type_t.PROPHET_BUNDLE_ROUTER);
		}
		else
		{
			throw new InvalidDTNConfigurationException("Invalid DTN Config Exception: Router Type is invalid");
		}
		
		
//		Attr local_eid = config_element.getAttributeNode("local_eid");
//		
//		if (local_eid!=null)
		{
			config.routes_setting().set_local_eid(DTNConfiguration.default_dtn_config().routes_setting().local_eid());
		}
//		else
//		{
//			throw new InvalidDTNConfigurationException("Invalid DTN Config Exception: local_eid is not specified");
//		}
		
		
		List<RouteEntry> route_entries = config.routes_setting().route_entries();
		for(int i=0; i < childNodes.getLength(); i++)
		{
			
			if( childNodes.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				Element e = (Element) childNodes.item(i);
				
				if (e.getTagName().equals(RouteTagName))
				{
					RouteEntry re = new RouteEntry();
					Attr dest = e.getAttributeNode("dest");
					re.set_dest(dest.getValue());
					
					
					Attr link_id = e.getAttributeNode("link_id");
					re.set_link_id(link_id.getValue());
					
					route_entries.add(re);
					
				}
				else throw new InvalidDTNConfigurationException("Route Element unknown");
					
				
			}
			
		}
		
		return;
	}

	/**
	 * The routine to process config related to discovery
	 * @param config_element the input element received
	 * @param config the configuration file we will put output on
	 * @throws InvalidDTNConfigurationException
	 */
	private static void process_discoveries_config(Element config_element, DTNConfiguration config) throws InvalidDTNConfigurationException
	{
		NodeList childNodes = config_element.getChildNodes();
		
		List<DiscoveryEntry> discovery_entries = config.discoveries_setting().discovery_entries();
		List<AnnounceEntry>  announce_entries = config.discoveries_setting().announce_entries();
		for(int i=0; i < childNodes.getLength(); i++)
		{
			
			if( childNodes.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				Element e = (Element) childNodes.item(i);
				
				if (e.getTagName().equals(DiscoveryTagName))
				{
					DiscoveryEntry de = new DiscoveryEntry();
					Attr id = e.getAttributeNode("id");
					de.set_id(id.getValue());
					
					
					Attr address_family = e.getAttributeNode("address_family");
					
					if (address_family.getValue().equals("ip"))
					de.set_address_family(address_family_t.IP);
					else
						throw new InvalidDTNConfigurationException("Address Family Unknown");
					
					Attr port = e.getAttributeNode("port");
					de.set_port(Short.parseShort(port.getValue()));
					discovery_entries.add(de);
					
				}
				else if (e.getTagName().equals(AnnounceTagName))
				{
					AnnounceEntry ae = new AnnounceEntry();
					
					Attr interface_id = e.getAttributeNode("interface_id");
					ae.set_interface_id(interface_id.getValue());
					
					
					Attr discovery_id = e.getAttributeNode("discovery_id");
					ae.set_discovery_id(discovery_id.getValue());
					
					
					Attr conv_layer_type = e.getAttributeNode("conv_layer_type");
					if (conv_layer_type.getValue().equals("tcp"))
					{
						ae.set_conv_layer_type(conv_layer_type_t.TCP);
					}
					else
					{
						throw new InvalidDTNConfigurationException("Conv Layer Type in Announce Entry is invalid");
					}
					
					
					Attr interval = e.getAttributeNode("interval");
					if (interval!= null)
					{
						ae.set_interval(Integer.parseInt(interval.getValue()));
					}
					else
					{
						ae.set_interval(5);
					}
					announce_entries.add(ae);
				}
				else
				{
					throw new InvalidDTNConfigurationException("Element under Discovery is unknown");
				}
					
				
			}
			
		}
		
		return;
	}

	/**
	 * Routine to process config related to storage setting
	 * @param config_element the input element received
	 * @param config the configuration file we will put output on
	 * @throws InvalidDTNConfigurationException
	 */
	private static void process_storage_config(Element config_element, DTNConfiguration config) throws InvalidDTNConfigurationException
	{
		Attr type = config_element.getAttributeNode("type");
		if (type.getValue().equals("sdcard"))
		config.storage_setting().set_storage_type(storage_type_t.SDCard);
		else if (type.getValue().equals("phone"))
		config.storage_setting().set_storage_type(storage_type_t.PHONE);
		else
			throw new InvalidDTNConfigurationException("Storage type not known exception"); 
		
		Attr quota = config_element.getAttributeNode("quota");
		config.storage_setting().set_quota(Integer.parseInt(quota.getValue()));
		
		Attr storage_path = config_element.getAttributeNode("storage_path");
		config.storage_setting().set_storage_path(storage_path.getValue());
		
		
		return;
	}
	
	
}
