package edu.wpi.cs.jburge.SEURAT;

import org.eclipse.ui.plugin.*;
//import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

//import test1.Activator;

import edu.wpi.cs.jburge.SEURAT.views.IRationaleUpdateEventListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.Iterator;

/**
 * @author jburge
 *
 * 
 * The main plugin class for SEURAT.
 */


/**
 * The activator class controls the plug-in life cycle
 */
public class SEURATPlugin extends AbstractUIPlugin {
	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "test1";
	
	/**
	 * The shared instance
	 */
	private static SEURATPlugin plugin;
	
	/**
	 * The resource bundle (whatever that is...)
	 */
	private ResourceBundle resourceBundle;
	
	/**
	 * A vector containing all of our update listeners. 
	 */
	private Vector<IRationaleUpdateEventListener> updateListeners;
	
	/**
	 * The constructor
	 */
	public SEURATPlugin() {
		super();
		updateListeners = new Vector<IRationaleUpdateEventListener>();
		plugin = this;
		try {
			resourceBundle= ResourceBundle.getBundle("edu.wpi.cs.jburge.SEURAT.SEURATPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static SEURATPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 *//*
	 public static ImageDescriptor getImageDescriptor(String path) {
	 return imageDescriptorFromPlugin(PLUGIN_ID, path);
	 
	 
	 }*/
	
	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	
	public void addUpdateListener(IRationaleUpdateEventListener lst)
	{
		updateListeners.add(lst);
	}
	public Iterator getUpdateListenerI()
	{
		return updateListeners.iterator();
	}
	
	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle= SEURATPlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}
	
	/**
	 * Gets the image descriptor for the icons used in SEURAT. This is
	 * where the icon path is set up as well. The image descriptor is
	 * an empty URL and the icon directory.
	 * @param name - the name of the icon
	 * @return - the image descriptor
	 */
	//From the tree view plugin article, 
	public static ImageDescriptor getImageDescriptor(String name) {
		String iconPath = "icons/";
		try {
			URL installURL = getDefault().getBundle().getEntry("/");
			//URL installURL = getDefault().getBundle().getEntry("edu.wpi.cs.jburge.SEURAT.SEURATPluginResources");
			
			URL url = new URL(installURL, iconPath + name);
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
			// should not happen
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}
	
	/**
	 * Returns the plugin's resource bundle,
	 */
	
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
}
