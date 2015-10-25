import website.URLNotFoundException;
import website.WebsiteDatabase;

public class Critical {

	private boolean active;
	private WebsiteDatabase website;
	
	public Critical(){
		try {
			website = new WebsiteDatabase();
			active = true;
		} catch (URLNotFoundException e) {
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
