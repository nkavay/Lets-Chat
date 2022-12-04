package LetsChatServer_final;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;


public class Server implements Runnable{  
	//Server클래스: 소켓을 통한 접속서비스, 접속클라이언트 관리

		Vector<Room> roomV; //개설된 대화방 Room-vs(Vector) : 대화방사용자

		public Server() { 
			roomV = new Vector<>();
			new Thread(this).start(); 
		}//생성자

		@Override public void run(){    
			try 
			{ 
				ServerSocket serversocket = new ServerSocket(8888); //현재 실행중인 ip + port
				System.out.println("!!!!!!!!!!!!Start Server!!!!!!!!!!!");    
				while(true){    
					Socket socket = serversocket.accept();//클라이언트 접속 대기    
					System.out.println("서버에 접속한 클라이언트 : " + socket); //socket: 접속한 클라이언트의 소켓정보    
					ChatService cs = new ChatService(socket, this);  //접속한 클라이언트 서버의 chatservice 객체 생성   
				}        
			} catch (IOException e) { 
				e.printStackTrace();   
			}     
		}//run 
				
		public static void main(String[] args) {    
			new Server();   
		}
	}
