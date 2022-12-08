package LetsChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JOptionPane;

import LetsChat.ChatRoom;


public class ChatService extends Thread{  //ChatService == 접속 클라이언트 한명!! 

	Room myRoom; //클라이언트가 입장한 대화방
	BufferedReader msg_in;  
	OutputStream msg_out;      
	Vector<Room> roomV; //개설된 대화방 Room-vs(Vector) : 대화방사용자      
	Socket socket;      
	String nickName;      
	Vector<LetsChatServer.ChatRoom> chatRoomV ;
	Vector roomList = new Vector(); //채팅방 이름 벡터]
	ChatService serCS;
	
	public ChatService(Socket socket, Server server) {
        roomV=server.roomV; //모든 채팅방의 정보 벡터 가져옴
        chatRoomV=server.chatRoomV;
        serCS=server.serverCS;
		this.socket=socket;
		try {  
        	msg_in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8")); 
            msg_out = socket.getOutputStream();
			start();   
        } catch (IOException e) { 
        	e.printStackTrace();  
        } 
	} //생성
	public ChatService() {
	} //생성
	public void run() {
		try {
			while(true) {
				String msg = msg_in.readLine(); //클라이언트에게 받은 메세지
				if(msg == null) return;
				if(msg.trim().length()>0) {
					System.out.println("from Client: "+ msg +":"+socket.getInetAddress().getHostAddress());//서버에서 상황을 모니터!!                  
		    		String msgs[]=msg.split("\\|");            
		    		String protocol = msgs[0]; 
		    		/*====================*/
		    		System.out.println("protocol: " + protocol);
		    		
		    		switch(protocol){       
						case "150": //대화명 입력                
							nickName=msgs[1];   //닉네임 정보저장                              
							break;   
						case "160": //방만들기 (대화방 입장)  
							String makemsg; //방정보 전달 변수
							String inmsgs[]=msgs[1].split("!!"); 
							roomList.clear(); 
							
							for(int i=0; i<roomV.size(); i++){ //현재 존재하는 채팅방 이름 리스트 
								Room r= roomV.get(i);
								roomList.add(r.room_title);
							}
							int index = roomList.indexOf(inmsgs[0]); //채팅방이름 중복 확인 검색
							if(inmsgs[0].indexOf("#")!=-1)
							{
								index=100;
							}
							if(index==100) //chatbot
							{
								String name=socket.getInetAddress().getHostAddress();
								name=name.substring(name.indexOf(".")+1,name.length());
								name=name.substring(name.indexOf(".")+1,name.length());
								inmsgs[0]+=name.substring(0,name.indexOf("."));
								name=name.substring(name.indexOf(".")+1,name.length());
								inmsgs[0]+=name;
								myRoom = new Room();      //새로운 채팅방 객체 생성    
	    						myRoom.room_title =inmsgs[0];//방제목 
	    						myRoom.count = 2;          //채팅방 참여 인원 
	    						myRoom.room_boss = nickName;   //채팅방 생성자 이름
	    						myRoom.limitcount = Integer.parseInt(inmsgs[1]); //채팅방에 참여가능한 최대인원
	    						myRoom.room_userV.add(this);  //채팅방에 접속한 사람 정보 저장
	    						myRoom.room_userV.add(serCS);
	    						myRoom.chatbot=true;
	    						roomV.add(myRoom); //개설된 채팅방 추가                               
	    						messageToone("200|"+nickName);//방인원에게 입장 알림  
	    						makemsg=nickName+"!!"+myRoom.room_title+"!!"+myRoom.limitcount;
	    						messageToone("205|"+ makemsg);  //채팅창 열기, 채팅방 정보전달
	    						System.out.println("chatbot 채팅 생성! "+myRoom.room_title+":"+myRoom.limitcount);
	    						for(int i=0; i<chatRoomV.size();i++)
	    						{
	    							LetsChatServer.ChatRoom cr = chatRoomV.get(i); 
	    							if(cr.clientIP.equals(socket.getInetAddress().getHostAddress()))
	    							{
	    								cr.la_roominfo.setText(myRoom.room_title+"(2/2)");
	    								cr.clientNickname = nickName;
	    								String[] members=new String[2];
	    								members[0]=nickName;
	    								members[1]="상담";
	    								System.out.println("!!!!!!!!!!in");
	    								cr.setVisible(true);
	    								break;
	    							}
	    						}
							}
							else if(index == -1)//이미 사용중인 채팅방 이름인 경우
							{
								//새로운 채팅방 생성
								roomList.add(inmsgs[0]);
								myRoom = new Room();      //새로운 채팅방 객체 생성    
	    						myRoom.room_title =inmsgs[0];//방제목         
	    						myRoom.count = 1;          //채팅방 참여 인원 
	    						myRoom.room_boss = nickName;   //채팅방 생성자 이름
	    						myRoom.limitcount = Integer.parseInt(inmsgs[1]); //채팅방에 참여가능한 최대인원
	    						myRoom.room_userV.add(this);  //채팅방에 접속한 사람 정보 저장
	    						roomV.add(myRoom); //개설된 채팅방 추가                               
	    						messageRoomMember("200|"+nickName);//방인원에게 입장 알림  
	    						makemsg=myRoom.room_title+"!!"+myRoom.limitcount;
	    						messageToone("202|"+ makemsg);  //채팅창 열기, 채팅방 정보전달
	    						System.out.println("새로운 채팅방 생성! "+myRoom.room_title+":"+myRoom.limitcount);
								
							}
							else {
								messageToone("700|used");
							}
    						break;
						case "170": //채팅방 리스트        
							messageToone("170|"+getRoomList());    
    						System.out.println("개설된 채팅방 리스트: "+getRoomList());
    						System.out.println("ok");
    						break;     
    					
    					case "175": //(대화방에서) 대화방 인원정보                
    						messageRoomMember("175|"+getRoomMember());    
    						System.out.println(myRoom.room_title+"의 현재 참여 인원: "+getRoomMember());
    						break;                       
            
    					case "200": //방들어가기 (대화방 입장) 
    						String entermsg;
    						System.out.println(msgs[1]);
    						int flag=0; //0:채팅방이 존재하지 않는 경우, 1:일치하는 채팅방을 찾은 경우 2:채팅방 인원수가 꽉찬 경우  
    						for(int i=0; i<roomV.size(); i++){//방이름 찾기        
    							Room r = roomV.get(i);                
    							if(r.room_title.equals(msgs[1])){//일치하는 방 찾음  
    								if(r.count<r.limitcount) {
    									myRoom = r;  
    									myRoom.count++;//인원수 1증가     
    									flag=1;
    									System.out.println("채팅방 입장! "+nickName+"->"+myRoom.room_title);
    									break;                
    								}
    								else {
    									flag=2;
    									messageToone("700|full");
    								}
    									
    							}               
    						}//for 
    						if(flag==1) { //채팅방 입장시
    							myRoom.room_userV.add(this);                                
    							messageRoomMember("200|"+nickName);//채팅방인원들에게 입장알람    
        						entermsg=myRoom.room_title+"!!"+myRoom.limitcount;
        						messageToone("202|"+ entermsg); //채팅방정보 전달, 채팅창 열기
    						}
    						else if(flag==0) //채팅방이 존재하지 않음
    							messageToone("700|none");
    						break;     
    						
    					case "300": //메시지               
    						if(myRoom.chatbot) 
    						{
    							chatbot("["+nickName +"]>> "+msgs[1]);
    							messageToone("300|["+nickName +"]>> "+msgs[1]);
    						}
    						else messageRoomMember("300|["+nickName +"]>> "+msgs[1]);
    						//클라이언트에게 메시지 보내기                
    						break;           
            
    					case "400": //대화방 퇴장        
    						if(myRoom.chatbot)
    						{
    							myRoom.room_userV.remove(this); 
    							myRoom.room_userV.remove(0);
    							chatbot("500|");
    							myRoom.count=0;
    						}
    						else
    						{
    							myRoom.count--;//인원수 감소                        
        						messageRoomMember("400|"+nickName);//방인원들에게 퇴장 알림    
        						myRoom.room_userV.remove(this);   
    						}
    						if(myRoom.count==0) //채팅방에 인원이 없을 시 채팅방 삭제
    							roomV.remove(myRoom);
    						System.out.println(nickName+" 채팅방 퇴장!");
    						break; 
    					
		    		}//서버 switch   
    			}//if 
    		}//while  
		
		}catch (IOException e) {  
			System.out.println("server★"); e.printStackTrace(); 
		}   
	} //run
		
	public String getRoomList(){//채팅방 정보 리스트             
	    String roomStr="";  //채팅방 정보 저장     ex)"방1(1/2),방2(1,5)"
	    for(int i=0; i<roomV.size(); i++){//방이름 찾기        
			Room r = roomV.get(i);  
			if(r.chatbot) continue;
			String roominfo = r.room_title+"("+r.count+"/"+r.limitcount+")";
			roomStr+=roominfo;        
			if(i<roomV.size()-1)roomStr += ",";    
		}//for 
	    return roomStr;
	}//getRoomList 
	
	public String getRoomMember(){//같은방의 인원정보               
	    String str="";  //채팅방 참여 닉네임 저장     ex)"사람1,사람2,사람3"
	    for(int i=0; i<myRoom.room_userV.size(); i++){        
	        ChatService ser= myRoom.room_userV.get(i);   
	        str += ser.nickName;      
	        if(i<myRoom.room_userV.size()-1)str += ",";    
	    }   
	    return str;   
	}//getRoomMember	
	
	public void messageRoomMember(String msg){//대화방사용자들에게 메세지보내기    
		if(myRoom.chatbot) {
			ChatService service = myRoom.room_userV.get(0);
			try {    
	            service.messageToone(msg);    
	        } catch (IOException e) {    //에러발생 ---> 클라이언트 접속 끊음   
	            myRoom.room_userV.remove(0); //접속 끊긴 클라이언트를 벡터에서 삭제
	            myRoom.room_userV.remove(1);
	            System.out.println("클라이언트 접속 끊음!!");    
	        }    
		}; //chatbot이라서 메세지 보낼 필요없
	    for(int i=0; i< myRoom.room_userV.size(); i++){//벡터 인덱스   
	    	ChatService service = myRoom.room_userV.get(i); //각각의 클라이언트 얻어오기    
	        try {    
	            service.messageToone(msg);    
	        } catch (IOException e) {    //에러발생 ---> 클라이언트 접속 끊음   
	            myRoom.room_userV.remove(i--); //접속 끊긴 클라이언트를 벡터에서 삭제
	            System.out.println("클라이언트 접속 끊음!!");    
	        }    
	    }   
	}//messageMember  
	
	public void messageToone(String msg) throws IOException{//특정 클라이언트에게 메시지 전달 (실제 서버--->클라이언트 메시지 전달)    
    	msg_out.write((msg+"\n").getBytes("UTF-8"));   
	}//mssageToone
	public static String padLeft(String s, int n) {
        return String.format("%" + n + "s", s);
    }
	public void chatbot(String msg) {
		
		for(int i=0; i<chatRoomV.size();i++)
		{
			LetsChatServer.ChatRoom cr = chatRoomV.get(i); 
			if(cr.clientIP.equals(socket.getInetAddress().getHostAddress()))
			{
				if(msg.indexOf("|")!=-1)
				{
					cr.ta_chat.setText("");
					cr.setVisible(false);
				}
				else
				{
					String smsgs=msg;
					String chatmsgs[] = smsgs.split("!!changline!!"); 
					int len = chatmsgs[0].indexOf(">>");
					cr.ta_chat.append(chatmsgs[0] + "\n");
					int con;
					//줄바꿈시 줄 간격 맞추기
					for(con=1; con<chatmsgs.length;con++)
					{
						String str=chatmsgs[i];
						int k = len + str.length();
						String msgline = padLeft(chatmsgs[con],k);
						cr.ta_chat.append(msgline + "\n");
					}
					cr.ta_chat.setCaretPosition(cr.ta_chat.getText().length());
				}
				
				break;
			}
		}
	}
	
}
