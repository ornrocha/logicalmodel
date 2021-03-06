package org.colomoto.logicalmodel.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.colomoto.logicalmodel.io.LogicalModelFormat;
import org.colomoto.logicalmodel.tool.LogicalModelTool;

/**
 * List available "services".
 * For now it only manages formats, to be further extended...
 * 
 * @author Aurelien Naldi
 */
public class ServiceManager {

	private static ServiceManager MANAGER = null;
	
	/**
	 * Retrieve the single-instance service manager.
	 * 
	 * @return the service manager
	 */
	public static ServiceManager getManager() {
		if (MANAGER == null) {
			MANAGER = new ServiceManager();
		}
		return MANAGER;
	}
	
	private final List<LogicalModelFormat> formats;
	private final Map<String, LogicalModelFormat> id2format = new HashMap<String, LogicalModelFormat>();

	private final List<LogicalModelTool> tools;
	private final Map<String, LogicalModelTool> id2tool = new HashMap<String, LogicalModelTool>();

	private final List<Service> services;
	private final Map<String, Service> id2service = new HashMap<String, Service>();

	private ServiceManager() {
		formats = new ArrayList<LogicalModelFormat>();
		
        Iterator<LogicalModelFormat> format_list = ExtensionLoader.iterator( LogicalModelFormat.class);
        while (format_list.hasNext()) {
            try {
            	LogicalModelFormat format = format_list.next();
            	if( format != null){
            		formats.add(format);
        			id2format.put( format.getID(), format);
            	}
            }
            catch (ServiceConfigurationError e){

            }
        }

		tools = new ArrayList<LogicalModelTool>();

		Iterator<LogicalModelTool> tool_list = ExtensionLoader.iterator(LogicalModelTool.class);
		while (tool_list.hasNext()) {
			try {
				LogicalModelTool tool = tool_list.next();
				if( tool != null){
					tools.add(tool);
					id2tool.put( tool.getID(), tool);
				}
			}
			catch (ServiceConfigurationError e){

			}
		}


		services = new ArrayList<Service>();

		Iterator<Service> service_list = ExtensionLoader.iterator(Service.class);
		while (service_list.hasNext()) {
			try {
				Service service = service_list.next();
				if( service != null){
					services.add(service);
					id2service.put( service.getID(), service);
				}
			}
			catch (ServiceConfigurationError e){

			}
		}
	}
	
	/**
	 * Get the format declaration for a given ID.
	 * 
	 * @param name ID of the format
	 * @return the format declaration instance or null if not found.
	 */
	public LogicalModelFormat getFormat(String name) {
		
		return id2format.get(name);
	}
	
	/**
	 * Get the available formats.
	 * 
	 * @return all available formats
	 */
	public Iterable<LogicalModelFormat> getFormats() {
		return formats;
	}

	/**
	 * Get the tool declaration for a given ID.
	 *
	 * @param name ID of the tool
	 * @return the tool declaration instance or null if not found.
	 */
	public LogicalModelTool getTool(String name) {

		return id2tool.get(name);
	}

	/**
	 * Get the available tools.
	 *
	 * @return all available tools
	 */
	public Iterable<LogicalModelTool> getTools() {
		return tools;
	}


	/**
	 * Get the service for a given ID.
	 *
	 * @param name ID of the service
	 * @return the service instance or null if not found.
	 */
	public Service getService(String name) {

		return id2service.get(name);
	}

	/**
	 * Get the available services.
	 *
	 * @return all available services
	 */
	public Iterable<Service> getServices() {
		return services;
	}
}
