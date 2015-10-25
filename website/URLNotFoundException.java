package website;


/**
 * UrlCacheException Class
 * 
 * @author 	Majid Ghaderi
 * @version	1.0, Sep 22, 2015
 */
public class URLNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4568740090394611974L;

	/**
     * Constructor calls Exception super class with message
     */
    public URLNotFoundException() {
        super("UrlCache exception");
    }

    /**
     * Constructor calls Exception super class with message
     * @param message The message of exception
     */
    public URLNotFoundException(String message) {
        super(message);
    }
}
