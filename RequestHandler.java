
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import http.InvalidURLException;
import http.Message;

public class RequestHandler implements Runnable{
	
	private Socket soc;
	private Critical region;
	
	public RequestHandler(Socket socket, Critical crit){
		this.soc = socket;
		region = crit;
	}
	
	public Socket getSocket(){
		return soc;
	}
	public void setSocket(ServerSocket s){
		try {
			soc.bind(s.getLocalSocketAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(soc.getInputStream()));
					
			String s = "";
			while(input.ready()){
				s += input.readLine();
			}
			Message receivedMessage, serverResponse;
			try {
				receivedMessage = new Message(s);
				System.out.println(s);
				Message webObject = region.getWebsite().getObject(receivedMessage.getContentStatus());
				if (webObject.getEntity() == null){
					// send 404 not found
					serverResponse = new Message("HTTP/1.1", "404", "Not Found");
					serverResponse.addHeader("connection:", "close");

				} else {
					// Found message
					serverResponse = new Message("HTTP/1.1", "200", "OK");
					serverResponse.addHeader("connection:", "close");
					serverResponse.addHeader("Content-Type:", webObject.getEntityType());
					serverResponse.setEntity(webObject.getEntity());
				}
			} catch (InvalidURLException e) {
				// send 400 bad request
				serverResponse = new Message("HTTP/1.1", "400", "Bad Request");
				serverResponse.addHeader("Connection:", "close");

				String notFound = "<!DOCTYPE html><html><head><title>Not Found</title></head>" +
								  "<body>Not Found</body></html>";
				byte[] ent= notFound.getBytes("UTF-8");
				serverResponse.addHeader("Content-Encoding", "UTF-8");
				serverResponse.addHeader("Content-Length", Integer.toString(ent.length));
				serverResponse.addHeader("Content-Type", "text/html");

				serverResponse.setEntity(ent);
			}
			if (serverResponse != null){
				soc.getOutputStream().write(serverResponse.toByteArray());
				soc.getOutputStream().flush();
			}
			soc.shutdownOutput();
			soc.shutdownInput();
			soc.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
