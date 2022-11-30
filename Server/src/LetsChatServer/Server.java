package LetsChatServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;


public class Server implements Runnable{  
	//Server클래스: 소켓을 통한 접속서비스, 접속클라이언트 관리

		//Vector<ChatService> allV; //모든 사용자(대기실사용자 + 대화방사용자) 
		//Vector<ChatService> waitV; //대기실 사용자     
		Vector<Room> roomV; //개설된 대화방 Room-vs(Vector) : 대화방사용자

	public Server() {
			//allV = new Vector<>(); 
			roomV = new Vector<>();
			//Thread t = new Thread(run메소드의 위치);  
			//t.start(); 
			new Thread(this).start(); 
		}//생성자

		@Override public void run(){    
			try 
			{ 
				ServerSocket ss = new ServerSocket(8888); //현재 실행중인 ip + 명시된 port ----> 소켓서비스   
				System.out.println("Start Server.......");    
				while(true){    
					Socket s = ss.accept();//클라이언트 접속 대기     
					//s: 접속한 클라이언트의 소켓정보    
					ChatService cs = new ChatService(s, this);    
					//allV.add(cs);//전체사용자에 등록    
					//waitV.add(cs);//대기실사용자에 등록    
				}        
			} catch (IOException e) { 
				e.printStackTrace();   
			}     
		}//run 
				
		
		public static void main(String[] args) {    
			new Server();   
		}
	}
