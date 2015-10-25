package website;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import http.*;

/**
 * @author Majid Ghaderi
 * @author Chris Wozniak
 *
 * UrlCache implements a hashMap for caching visited websites.
 */
public class WebsiteDatabase {
	
	private File filename;
	private Map<String, Message> data;
	
	/**
	 * Standard Constructor for the UrlCache, it will load, or create and save
	 * a hashMap to/from disk if it has one in its current working directory
	 * named "cache.dat"
	 * 
	 * Further development should include a constructor so that different
	 * cache databases could be passed into the object.
	 * 
	 * @throws URLNotFoundException
	 */
	public WebsiteDatabase() throws URLNotFoundException{
		// Load cache, either brand new or from saved.
		filename = new File("cache.dat");
		if (filename.exists()){
			loadFromFile();
		} else{
			data = new HashMap<String, Message>(10);
			saveToFile();
		}
	}
	
	/**
	 * getObject will open a TCP/IP network socket, and attain the webpage
	 * from the provided url. It confirms with the server if the cached
	 * site is valid, if it has previously visited the page before.
	 * 
	 * @param url
	 * @throws URLNotFoundException
	 */
	public synchronized Message getObject(String url){
		// Confirm if file is in directory
		if (data.containsKey(url)){
			return data.get(url);
		}
		return null;
	}
	
	/**
	 * Finds the webpage that was cached based on the URL key.
	 * This will throw an exception if the key is not found as per 
	 * assignment instructions, but plan was to return -1 for determining
	 * if the url was in the cache or not.
	 * 
	 * @param url
	 * @return
	 * @throws URLNotFoundException
	 */
	public synchronized long getLastModified(String url) throws URLNotFoundException{
		MyURL m = new MyURL(url);
		Message msg = data.get(m.toString());
		long value = msg.getLastModified();
		if (value < 0){
			throw new URLNotFoundException(url + " \n was not found in the cashe, "
					+" course requirements dictated a thrown message over a completed"
					+" conditional-GET, so this was thrown.");
		}else{
			return value;
		}	
	}
	
	/**
	 * Saves the cache to disk
	 */
	private void saveToFile(){
		ObjectOutputStream writer;
		try {
			writer = new ObjectOutputStream(new FileOutputStream(filename));
			writer.writeObject(data);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the cache from disk, if it can find the outputstream.
	 * If there is no cache, this program will create one before this
	 * code has a chance to execute.
	 * 
	 * The annotation to suppress warnings is introduced to re-cast
	 * the hashMap back to its generic type. Not using datafiles which
	 * are of a different type will crash.
	 * 
	 * @throws URLNotFoundException
	 */
	@SuppressWarnings(value = "unchecked")
	private void loadFromFile() throws URLNotFoundException{
		ObjectInputStream reader;
		try {
			reader = new ObjectInputStream(new FileInputStream(filename));
			Object obj = reader.readObject();
			if (obj instanceof Map<?,?>){
				data = (Map<String, Message>) obj;
			} else {
				reader.close();
				throw new URLNotFoundException("Could not load cache from: " + "filename");
			}
			reader.close();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}
