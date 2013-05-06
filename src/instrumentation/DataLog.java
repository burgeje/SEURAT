/*	This code belongs to the SEURAT project as written by Dr. Janet Burge
    Copyright (C) 2013  Janet Burge

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
/*
 * Created on Oct 24, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package instrumentation;

//import java.text.DateFormat;
//import java.util.Date;
//import java.util.logging.FileHandler;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import java.util.logging.SimpleFormatter;

/**
 * This was used to log data during experiments. Most of the contents are 
 * commented out.
 * @author jburge
 */
public final class DataLog {
	
	/**
	 * 
	 */
	private static DataLog s;
//	private static String outFile;
//	private static FileHandler fh;
//	private static Logger ourLog;
	
	private DataLog() {
		super();
		/*
		 // TODO Auto-generated constructor stub
		  Date dStamp;
		  dStamp = new Date();
		  DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		  DateFormat tf = DateFormat.getTimeInstance(DateFormat.SHORT);
		  String time = tf.format(dStamp);
		  time = time.replace(':', '_');
		  String dString = df.format(dStamp) + "_" + time;
		  System.out.println(dString);
		  outFile = "log_" + dString + ".txt";
		  try
		  {
		  fh = new FileHandler(outFile);
		  fh.setFormatter(new SimpleFormatter());
		  ourLog = Logger.getLogger("edu.wpi.cs.jburge.SEURAT");
		  ourLog.addHandler(fh);
		  ourLog.setLevel(Level.ALL);
		  ourLog.fine("start session");
		  } catch (Exception ex)
		  {
		  System.out.println("Exception setting up log");
		  }
		  */
		
	}
	
	public static DataLog getHandle() {
		if (s == null)
		{
			s = new DataLog();
		}
		return s;
	}
	
	public void writeData(String data)
	{
//		ourLog.log(Level.INFO, data);
		
	}
	
}
