package LetsChatServer;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class Server extends JFrame implements ActionListener, Runnable{  
	//Server클래스: 소켓을 통한 접속서비스, 접속클라이언트 관리

		Vector<ChatService> allV; //모든 사용자(대기실사용자 + 대화방사용자) 
		Vector<Room> roomV; //개설된 대화방 Room-vs(Vector) : 대화방사용자
		Vector<ChatRoom> chatRoomV;
		ChatService serverCS ;
		JPanel p;
		JTextArea ta_log;
		JList<String> li_clientIP;
		JScrollPane sp_ta,sp_list;
		JButton bt_showMovList, bt_send;
		JLabel la_client;
		ChatRoom cr;
		
		public Server() { 
			allV = new Vector<>(); 
			roomV = new Vector<>();
			chatRoomV = new Vector<>();
			new Thread(this).start(); 
			 
			
			setTitle("작은무비:서버");
			ta_log = new JTextArea();
			ta_log.setLineWrap(true);
			ta_log.setEditable(false);
			
			li_clientIP = new JList<String>(); 
			la_client = new JLabel("client");
			
			sp_ta = new JScrollPane(ta_log);
			sp_list = new JScrollPane(li_clientIP);
			
			p = new JPanel();
			
			bt_showMovList = new JButton("예매내역 보기"); 
			//bt_send = new JButton("보내기"); 
			
			sp_ta.setBounds(10,30,340,450); 
			la_client.setBounds(370, 10, 100, 20);
			sp_list.setBounds(360,30,100,450);
			bt_showMovList.setBounds(360,490,100,60); 
			
			p.setLayout(null);
			p.setBackground(new Color(186,134,252));
			p.add(sp_ta);
			p.add(la_client);
			p.add(sp_list);
			p.add(bt_showMovList);

			add(p);
			
			this.pack();
			
			setBounds(300,200,470,600);
			setVisible(true);
			
			
		}//생성자
		
		private void eventUp() {// 이벤트소스-이벤트처리부 연결

			cr.bt_send.addActionListener(this);
			cr.bt_exit.addActionListener(this);

		}
		public static String padLeft(String s, int n) {
	        return String.format("%" + n + "s", s);
	    }
		public void actionPerformed(ActionEvent e) {

			Object ob = e.getSource();
			
			if(ob==cr.bt_send) {//172.20.16.148
				String msg = cr.ta_sendmsg.getText();
				if (msg.length() > 0) {
					msg = msg.replaceAll("(\r\n|\r|\n|\n\r)", "!!changline!!"); //줄바꿈 전송하기 
					for(int i=0;i<allV.size();i++)
					{
						ChatService cs = allV.get(i);
						if(cs.socket.getInetAddress().getHostAddress().equals(cr.clientIP))
						{
							try {    
								cs.messageToone("300|[무비봇]>> "+msg); 
					        } catch (IOException e1) {   
					        	e1.printStackTrace();
					        }   
							String smsgs=msg;
							String chatmsgs[] = smsgs.split("!!changline!!"); 
							int len = 7;
							cr.ta_chat.append("[무비봇]>>"+chatmsgs[0] + "\n");
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
							break;
						}
					}
			        
					cr.ta_sendmsg.setText("");
				}
			}
			else if(ob==cr.bt_exit) {
				for(int i=0;i<allV.size();i++)
				{
					ChatService cs = allV.get(i);
					if(cs.socket.getInetAddress().getHostAddress().equals(cr.clientIP))
					{
						try {    
							cs.messageToone("800|"); 
				        } catch (IOException e1) {   
				        	e1.printStackTrace();
				        }   
						break;
					}
				}
				cr.setVisible(false);
			}
		}

		@Override public void run(){    
			try 
			{ 
				//ServerSocket
				ServerSocket serversocket = new ServerSocket(8888); //현재 실행중인 ip + 명시된 port ----> 소켓서비스   
				serverCS = new ChatService();   
				System.out.println("!!!!!!!!!!!!Start Server!!!!!!!!!!!");    
				while(true){    
					Socket socket = serversocket.accept();//클라이언트 접속 대기     
					//socket: 접속한 클라이언트의 소켓정보    
					ChatService cs = new ChatService(socket, this);   
					cr = new ChatRoom(socket);
					chatRoomV.add(cr);
					allV.add(cs);//전체사용자에 등록 
					eventUp();
				}        
			} catch (IOException e) { 
				e.printStackTrace();  
			}     
		}//run 
				
		
		public static void main(String[] args) {    
			new Server();   
		}
	}
