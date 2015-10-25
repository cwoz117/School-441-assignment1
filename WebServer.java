
public class WebServer{
	private ServerListener connector;
	private Critical critRegion;
	
	public WebServer(int portNumber){
		critRegion = new Critical();
		connector = new ServerListener(portNumber, critRegion);
	}
	
	public void start(){
		Thread t = new Thread(connector);
		t.setName("Server listener");
		t.start();
	}
	
	public void stop(){
		critRegion.setActive(false);
	}
}
