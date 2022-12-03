package LetsChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JOptionPane;



public class ChatService extends Thread{  //ChatService == 접속 클라이언트 한명!! 

	Room myRoom; //클라이언트가 입장한 대화방
	BufferedReader in;  
	OutputStream out;      
	//Vector<ChatService> allV; //모든 사용자(대기실사용자 + 대화방사용자)
	Vector<Room> roomV; //개설된 대화방 Room-vs(Vector) : 대화방사용자      
	Socket s;      
	String nickName;      
	Vector roomList = new Vector(); //채팅방 이름 벡터
	
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
		    		/*====================*/
		    		System.out.println("protocol: " + protocol);
		    		
		    		switch(protocol){       
						case "150": //대화명 입력                
							nickName=msgs[1]; 
							//messageTo("151|"+ nickName);
							//최초 대화명 입력했을때 대기실의 정보를 출력                                    
							break;   
						case "160": //방만들기 (대화방 입장)  
							String makemsg;
							String inmsgs[]=msgs[1].split("!!");
							System.out.println(inmsgs[1]);
							roomList.clear();
							for(int i=0; i<roomV.size(); i++){
								//"자바방--1,오라클방--1,JDBC방--1"
								Room r= roomV.get(i);
								roomList.add(r.title);
							}
							
							int index = roomList.indexOf(inmsgs[0]); //검색
							if(index != -1)
							{
								messageTo("700|used");
								JOptionPane.showMessageDialog(null, "이미 사용중인 채팅방입니다. 다른 이름을 입력해주세요.", "WARNING MESSAGE", JOptionPane.WARNING_MESSAGE);
							}
							else {
								roomList.add(inmsgs[0]);
								myRoom = new Room();          
	    						myRoom.title =inmsgs[0];//방제목         
	    						myRoom.count = 1;          
	    						myRoom.boss = nickName;  
	    						myRoom.limitcount = Integer.parseInt(inmsgs[1]);
	    						myRoom.userV.add(this);  
	    						roomV.add(myRoom);                                
	    						messageRoom("200|"+nickName);//방인원에게 입장 알림  
	    						makemsg=myRoom.title+"!!"+myRoom.limitcount;
	    						messageTo("202|"+ makemsg);  //채팅창 열기, 채팅방 정보전달
							}
    						break;
            
    					case "175": //(대화방에서) 대화방 인원정보                
    						messageRoom("175|"+getRoomInwon());    
    						System.out.println("server: "+getRoomInwon());
    						break;                       
            
    					case "200": //방들어가기 (대화방 입장) ----> 
    						String entermsg;
    						System.out.println(msgs[1]);
    						int flag=0;
    						for(int i=0; i<roomV.size(); i++){//방이름 찾기!!                
    							Room r = roomV.get(i);                
    							if(r.title.equals(msgs[1])){//일치하는 방 찾음!!  
    								if(r.count<r.limitcount) {
    									myRoom = r;  
    									myRoom.count++;//인원수 1증가     
    									flag=1;
    									break;                
    								}
    								else {
    									flag=2;
    									/*==============================추가=============*/
    	    							messageTo("700|full");
    	    							/*==============================추가=============*/
    									JOptionPane.showMessageDialog(null, "수용 최대 인원을 초과하였습니다. 다른 채팅방을 이용해주세요.", "WARNING MESSAGE", JOptionPane.WARNING_MESSAGE);
    								}
    									
    							}               
    						}//for 
    						if(flag==1) {
    							myRoom.userV.add(this);                                
        						messageRoom("200|"+nickName);//채팅방인원들에게 입장알람    
        						entermsg=myRoom.title+"!!"+myRoom.limitcount;
        						messageTo("202|"+ entermsg); //채팅방정보 전달, 채팅창 열
    						}
    						else if(flag==0)
    						{
    							/*==============================추가=============*/
    							messageTo("700|none");
    							/*==============================추가=============*/
    							JOptionPane.showMessageDialog(null, "채팅방이 존재하지 않습니다.", "WARNING MESSAGE", JOptionPane.WARNING_MESSAGE);
    						}                            
    						break;     
    					case "300": //메시지                              
    						messageRoom("300|["+nickName +"]>> "+msgs[1]);
    						//클라이언트에게 메시지 보내기                
    						break;           
            
    					case "400": //대화방 퇴장                
    						myRoom.count--;//인원수 감소                        
    						messageRoom("400|"+nickName);//방인원들에게 퇴장 알림!!            
    						//대화방----> 대기실 이동!!                
    						myRoom.userV.remove(this);   
    						//messageRoom("175|"); //리스트에서 지우기
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
	        str += ",";   
	    }   
	    return str;  
	}//getRoomInfo 	
	
	public String getRoomInwon(){//같은방의 인원정보               
	    String str="";        
	    for(int i=0; i<myRoom.userV.size(); i++){    
	        //"길동,라임,주원"     
	        ChatService ser= myRoom.userV.get(i);   
	        str += ser.nickName;      
	        if(i<myRoom.userV.size()-1)str += ",";    
	    }   
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
    	out.write((msg+"\n").getBytes());   
    }	
		
	
}
