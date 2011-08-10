/*
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program; if not, write to the Free 
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, 
 * MA  02111-1307, USA.
 */
package com.cloudhopper.commons.io;

import java.util.*;
import java.io.File;
import java.lang.ref.WeakReference;



/**
 * Class for monitoring changes in disk files.
 * Usage:
 *
 *    1. Implement the FileListener interface.
 *    2. Create a FileMonitor instance.
 *    3. Add the file(s)/directory(ies) to listen for.
 *
 * fileChanged() will be called when a monitored file is created,
 * deleted or its modified time changes.
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */   
public class FileMonitor
{
  private Timer timer_;
  private HashMap<File, Long> files_;       // File -> Long
  private ArrayList<WeakReference<FileChangedListener>> listeners_;   // of WeakReference(FileListener)
   

  /**
   * Create a file monitor instance with specified polling interval.
   * 
   * @param pollingInterval  Polling interval in milli seconds.
   */
  public FileMonitor (long pollingInterval)
  {
    files_     = new HashMap<File, Long>();
    listeners_ = new ArrayList<WeakReference<FileChangedListener>>();

    timer_ = new Timer (true);
    timer_.schedule (new FileMonitorNotifier(), 0, pollingInterval);
  }


  
  /**
   * Stop the file monitor polling.
   */
  public void stop()
  {
    timer_.cancel();
  }
  

  /**
   * Add file to listen for. File may be any java.io.File (including a
   * directory) and may well be a non-existing file in the case where the
   * creating of the file is to be trepped.
   * <p>
   * More than one file can be listened for. When the specified file is
   * created, modified or deleted, listeners are notified.
   * 
   * @param file  File to listen for.
   */
  public void addFile (File file)
  {
    if (!files_.containsKey (file)) {
      long modifiedTime = file.exists() ? file.lastModified() : -1;
      files_.put (file, new Long (modifiedTime));
    }
  }

  

  /**
   * Remove specified file for listening.
   * 
   * @param file  File to remove.
   */
  public void removeFile (File file)
  {
    files_.remove (file);
  }


  
  /**
   * Add listener to this file monitor.
   * 
   * @param fileListener  Listener to add.
   */
  public void addListener (FileChangedListener fileListener)
  {
    // Don't add if its already there
    for (Iterator<WeakReference<FileChangedListener>> i = listeners_.iterator(); i.hasNext(); ) {
      WeakReference<FileChangedListener> reference = i.next();
      FileChangedListener listener = reference.get();
      if (listener == fileListener)
        return;
    }

    // Use WeakReference to avoid memory leak if this becomes the
    // sole reference to the object.
    listeners_.add(new WeakReference<FileChangedListener>(fileListener));
  }


  
  /**
   * Remove listener from this file monitor.
   * 
   * @param fileListener  Listener to remove.
   */
  public void removeListener (FileChangedListener fileListener)
  {
    for (Iterator<WeakReference<FileChangedListener>> i = listeners_.iterator(); i.hasNext(); ) {
      WeakReference<FileChangedListener> reference = i.next();
      FileChangedListener listener = reference.get();
      if (listener == fileListener) {
        i.remove();
        break;
      }
    }
  }


  
  /**
   * This is the timer thread which is executed every n milliseconds
   * according to the setting of the file monitor. It investigates the
   * file in question and notify listeners if changed.
   */
  private class FileMonitorNotifier extends TimerTask
  {
    @SuppressWarnings("unchecked")
	public void run()
    {
      // Loop over the registered files and see which have changed.
      // Use a copy of the list in case listener wants to alter the
      // list within its fileChanged method.
      Collection files = new ArrayList (files_.keySet());
      
      for (Iterator i = files.iterator(); i.hasNext(); ) {
        File file = (File) i.next();
        long lastModifiedTime = ((Long) files_.get (file)).longValue();
        long newModifiedTime  = file.exists() ? file.lastModified() : -1;

        // Chek if file has changed
        if (newModifiedTime != lastModifiedTime) {

          // Register new modified time
          files_.put (file, new Long (newModifiedTime));

          // Notify listeners
          for (Iterator j = listeners_.iterator(); j.hasNext(); ) {
            WeakReference reference = (WeakReference) j.next();
            FileChangedListener listener = (FileChangedListener) reference.get();

            // Remove from list if the back-end object has been GC'd
            if (listener == null)
              j.remove();
            else
              listener.fileChanged (file);
          }
        }
      }
    }
  }


  /**
   * Test this class.
   * 
   * @param args  Not used.
   */
  public static void main (String args[])
  {
    // Create the monitor
    FileMonitor monitor = new FileMonitor (1000);

    // Add some files to listen for
    monitor.addFile (new File ("/Users/garth/cloudhopper/dev/utility/java/ch-commons-io/trunk/tmp/"));
    //monitor.addFile (new File ("/home/jacob/test2.txt"));
    //monitor.addFile (new File ("/home/jacob/"));    

    // Add a dummy listener
    monitor.addListener (monitor.new TestListener());

    // Avoid program exit
    while (!false) ;
  }

  
  private class TestListener
    implements FileChangedListener
  {
    public void fileChanged (File file)
    {
      System.out.println ("File changed: " + file);
    }
  }
}
