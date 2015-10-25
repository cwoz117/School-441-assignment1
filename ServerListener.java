import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class ServerListener implements Runnable{
	private Critical critRegion;
	private ServerSocket serv;
	private RequestHandler client;
	private List<Thread> subThreads;
	
	public ServerListener(int port, Critical crit){
		try {
			serv = new ServerSocket(port);
			serv.setSoTimeout(2000);
			critRegion = crit;
			subThreads = new ArrayList<Thread>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		critRegion.setActive(true);
		try {
			while (critRegion.getActive()){
				try{
					client = new RequestHandler(serv.accept(), critRegion);
					Thread t = new Thread(client);
					t.setName("Request Handler");
					subThreads.add(t);
					t.start();
				}catch(SocketTimeoutException e ){}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
