import cache.UrlCacheException;
import cache.WebsiteDatabase;

public class Critical {

	private boolean active;
	private WebsiteDatabase website;
	
	public Critical(){
		try {
			website = new WebsiteDatabase();
			active = true;
		} catch (UrlCacheException e) {
			e.printStackTrace();
		}
	}
	public synchronized void setActive(boolean b) {
		active = b;
	}
	public synchronized boolean getActive(){
		return active;
	}

	public synchronized WebsiteDatabase getWebsite(){
		return website;
	}
}
