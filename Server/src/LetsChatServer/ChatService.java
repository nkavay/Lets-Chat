package LetsChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;


public class ChatService extends Thread{  //ChatService == 접속 클라이언트 한명!! 

	Room myRoom; //클라이언트가 입장한 대화방
	BufferedReader in;  
	OutputStream out;      
	//Vector<ChatService> allV; //모든 사용자(대기실사용자 + 대화방사용자)
	Vector<Room> roomV; //개설된 대화방 Room-vs(Vector) : 대화방사용자      
	Socket s;      
	String nickName;      
	public ChatService(Socket s, Server server) {
		//allV=server.allV;      
        roomV=server.roomV; 
		this.s=s;
		try {  
        	in = new BufferedReader(new InputStreamReader(s.getInputStream())); 
            out = s.getOutputStream();
			start();   
        } catch (IOException e) { 
        	e.printStackTrace();  
        } 
	} //생성
	
	public void run() {
		try {
			while(true) {
				
				String msg = in.readLine();
				if(msg == null) return;
				if(msg.trim().length()>0) {
					System.out.println("from Client: "+ msg +":"+                   
					s.getInetAddress().getHostAddress());//서버에서 상황을 모니터!!                  
		    		String msgs[]=msg.split("\\|");            
		    		String protocol = msgs[0]; 
		    		
		    		switch(protocol){       
						case "150": //대화명 입력                
							nickName=msgs[1];                           
							//최초 대화명 입력했을때 대기실의 정보를 출력                                    
							break;   
						case "160": //방만들기 (대화방 입장)        
    						myRoom = new Room();          
    						myRoom.title =msgs[1];//방제목
    						myRoom.count = 1;
    						myRoom.boss = nickName;  
    						myRoom.userV.add(this);  
    						roomV.add(myRoom);                                
    						messageRoom("200|"+nickName);//방인원에게 입장 알림          
    						break;
            
    					case "175": //(대화방에서) 대화방 인원정보                
    						messageRoom("175|"+getRoomInwon());                
    						break;                       
            
    					case "200": //방들어가기 (대화방 입장) ----> 
    						//msgs[] = {"200","자바방"};             
    						for(int i=0; i<roomV.size(); i++){//방이름 찾기!!                
    							Room r = roomV.get(i);                
    							if(r.title.equals(msgs[1])){//일치하는 방 찾음!!
    								myRoom = r;                
    								myRoom.count++;//인원수 1증가                
    								break;                
    							}               
    						}//for                              
            
    						myRoom.userV.add(this);                                
    						messageRoom("200|"+nickName);
            
    						//들어갈 방의 title전달               
    						messageTo("202|"+ myRoom.title);                                       
    						break;               
            
    					case "300": //메시지                              
    						messageRoom("300|["+nickName +"]▶ "+msgs[1]);
    						//클라이언트에게 메시지 보내기                
    						break;           
            
    					case "400": //대화방 퇴장                
    						myRoom.count--;//인원수 감소                        
    						messageRoom("400|"+nickName);//방인원들에게 퇴장 알림!!            
    						//대화방----> 대기실 이동!!                
    						myRoom.userV.remove(this);                                                     
    						break; 
		    		}//서버 switch   
    			}//if 
    		}//while  
		
		}catch (IOException e) {  
			System.out.println("server★"); e.printStackTrace(); 
		}   
	}
		
	public String getRoomInfo(){   
	    String str="";   
	    for(int i=0; i<roomV.size(); i++){   
	        //"자바방--1,오라클방--1,JDBC방--1"  
	        Room r= roomV.get(i);  
	        str += r.title+"--"+r.count;  
	        if(i<roomV.size()-1)str += ",";   
	    }   
	    System.out.println("server:"+str);
	    return str;   //gg--4
	}//getRoomInfo 	
	
	public String getRoomInwon(){//같은방의 인원정보               
	    String str="";        
	    for(int i=0; i<myRoom.userV.size(); i++){    
	        //"길동,라임,주원"     
	        ChatService ser= myRoom.userV.get(i);   
	        str += ser.nickName;      
	        if(i<myRoom.userV.size()-1)str += ",";    
	    }   
	    System.out.println("server"+str);
	    return str;   
	}//getRoomInwon 	
	public void messageRoom(String msg){//대화방사용자    
	    for(int i=0; i< myRoom.userV.size(); i++){//벡터 인덱스    
	    	ChatService service = myRoom.userV.get(i); //각각의 클라이언트 얻어오기    
	        try {    
	            service.messageTo(msg);    
	        } catch (IOException e) {    //에러발생 ---> 클라이언트 접속 끊음!!    
	            myRoom.userV.remove(i--); //접속 끊긴 클라이언트를 벡터에서 삭제!!
	            System.out.println("클라이언트 접속 끊음!!");    
	        }    
	    }   
	}//messageAll  
	
	public void messageTo(String msg) throws IOException{//특정 클라이언트에게 메시지 전달 (실제 서버--->클라이언트 메시지 전달)    
    	out.write(  (msg+"\n").getBytes()   );   
    }	
		
	
}
