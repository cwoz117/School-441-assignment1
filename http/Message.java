package http;


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class Message implements Serializable{

	private static final long serialVersionUID = -8712240486510113552L;
	public static final int TEXT = 0;
	public static final int PDF = 1;
	public static final int JPEG = 2;
	public static final int GIF = 3;

	private byte[] entity;
	private String[] messageInfo = new String[3];
	private List<ArrayList<String>> headerLines;
	
	public Message(String messgeType, String contentLocation, String contentType){
		this.messageInfo[0] = messgeType; 	// Request Type
		this.messageInfo[1] = contentLocation;	// Content URL
		this.messageInfo[2] = contentType;	// Content Type

		headerLines = new ArrayList<ArrayList<String>>();
		entity = new byte[0];
	}
	public Message(String unparsedData) throws InvalidURLException{
		headerLines = new ArrayList<ArrayList<String>>();
		String[] headFromEntity = unparsedData.split("\n\n", 2);
		String[] msgInfo = headFromEntity[0].split("\n");
		String[] status = msgInfo[0].split(" ");
		
		if (status.length != 3){
			throw new InvalidURLException("whoops");
		}
		this.messageInfo[0]= status[0];					// Version
		this.messageInfo[1] = status[1];				// status code
		this.messageInfo[2] = msgInfo[0].substring(2);	// phrase
		
		for (int i = 1; i < msgInfo.length; i++){
			status = msgInfo[i].split(" ");
			headerLines.add(new ArrayList<String>());
			headerLines.get(headerLines.size()-1).add(status[0]);
			headerLines.get(headerLines.size()-1).add(msgInfo[i].substring(status[0].length()));
		}
		
		entity = headFromEntity[1].getBytes();
	}

	// Standard Getters
	public byte[] getEntity(){return entity;}
	public String getTypeOfMessage(){return messageInfo[0];}
	public String getContentStatus(){return messageInfo[1];}
	public String getPhraseType(){return messageInfo[2];}
	public String getEntityType(){
		Iterator<ArrayList<String>> i = headerLines.iterator();
		while(i.hasNext()){
			ArrayList<String> headerLines = i.next();
			String headerName = headerLines.get(0);
			if (headerName.matches("Content-Type:")){
				String headerData = headerLines.get(1);
				if(headerData.contains("text")){
					return "text";
				} else if (headerData.contains("pdf")){
					return "pdf";
				} else if (headerData.contains("gif")){
					return "gif";
				} else if (headerData.contains("jpeg")){
					return "jpeg";
				}
			}
		}
		return null;
	}
	public long getLastModified(){
		Date lastMod;
		Iterator<ArrayList<String>> i = headerLines.iterator();
		while(i.hasNext()){
			ArrayList<String> headerLines = i.next();
			String headerName = headerLines.get(0);
			if (headerName.matches("Last-Modified:")){
				String headerData = headerLines.get(1);
				SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
				try {
					lastMod = dateFormat.parse(headerData.trim());
					return lastMod.getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		return (long) -1;
	}
	public void setEntity(byte[] newEntity){
		entity = new byte[newEntity.length];
		for (byte i : entity){
			entity[i] = newEntity[i];
		}
	}
	public void addHeader(String headerFieldName, String value){
		headerLines.add(new ArrayList<String>());
		headerLines.get(headerLines.size()-1).add(headerFieldName);
		headerLines.get(headerLines.size()-1).add(value);
	}
	
	public byte[] toByteArray(){
		String msg = "";
		msg += messageInfo[0] + " " + messageInfo[1] + " " + messageInfo[2] + "\n";
		if (!headerLines.isEmpty()){
			for (int i = 0; i < headerLines.size(); i++){
				msg += headerLines.get(i).get(0) + " " + headerLines.get(i).get(1) + "\n";
			}
		}
		msg += "\n";
		byte[] header = msg.getBytes();
		byte[] output = new byte[(header.length + entity.length)];
		for (int i = 0; i < output.length; i++){
			if (i < header.length){
				output[i] = header[i];
			} else{
				output[i] = entity[i - header.length];
			}
		}
		return output;
	}
	public String toString(){
		String msg = "";
		msg += messageInfo[0] + " " + messageInfo[1] + " " + messageInfo[2] + "\n";
		if (!headerLines.isEmpty()){
			for (int i = 0; i < headerLines.size(); i++){
				msg += headerLines.get(i).get(0) + " " + headerLines.get(i).get(1) + "\n";
			}
		}
		msg += "\n";
		if (entity.length > 0){
			for(int i = 0; i < entity.length; i++){
				msg += entity[i];
			}
		}
		return msg;
	}
}