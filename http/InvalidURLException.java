package http;

public class InvalidURLException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidURLException(){
		super("The provided URL is invalid");
	}
	public InvalidURLException(String msg){
		super(msg);
	}
}
