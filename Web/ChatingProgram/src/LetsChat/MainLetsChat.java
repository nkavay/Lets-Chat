package LetsChat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class MainLetsChat extends JFrame implements ActionListener, Runnable {

	JList<String> roomInfo, roomInwon, waitInfo;
	JScrollPane sp_roomInfo, sp_roomInwon, sp_waitInfo;
	JButton bt_enterChatBot, bt_enterCom, bt_exit;
	JLabel lb_title, lb_writename,lb_writeip;
	JTextField tf_username,tf_serverip;
	JOptionPane op;

	JPanel p, pb;
	CreateRoom cr;
	EnterRoom er;
	ChatRoom chr;
	Community cm;

	// 소켓 입출력객체
	Socket socket;
	BufferedReader msg_in;
	OutputStream msg_out;

	String selectedRoom;
	GridBagLayout gBag;
	
	String roomname;
	String strpeople;

	boolean flag=false;
	
	public void gbinsert(Component c, int x, int y, int w, int h) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gBag.setConstraints(c, gbc);
		this.add(c);
	}

	public MainLetsChat() {

		// 메인 화면 title
		setTitle("작은 무비");

		chr = new ChatRoom(); // chatting class 객체생성
		cr = new CreateRoom(); // 방만들기 class 객체생성
		er = new EnterRoom(); // 방입장하기 class 객체생성
		cm = new Community();

		op = new JOptionPane();  //경고창

		lb_title = new JLabel("작은 무비");
		lb_title.setFont(new Font("Serif", Font.BOLD, 40));
		
		lb_writeip = new JLabel("서버IP를 입력해주세요"); //닉네임 입력창 
		lb_writeip.setFont(new Font("Serif", Font.PLAIN, 12));
		
		tf_serverip = new JTextField(20);
		tf_serverip.setFont(new Font("Serif", Font.PLAIN, 15));
		
		lb_writename = new JLabel("닉네임을 입력해주세요"); //닉네임 입력창 
		lb_writename.setFont(new Font("Serif", Font.PLAIN, 12));
		
		tf_username = new JTextField(20);
		tf_username.setFont(new Font("Serif", Font.PLAIN, 15));
		
		bt_enterChatBot = new JButton("ChatBot과 예매하기"); //프로그램 종료
		bt_enterCom = new JButton("Community"); //프로그램 종료
		bt_exit = new JButton("종료하기"); //프로그램 종료

		p = new JPanel();
		gBag = new GridBagLayout();
		setLayout(gBag);

		gbinsert(lb_title, 1, 1, 1, 3);
		lb_title.setHorizontalAlignment(JLabel.CENTER);
		lb_title.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

		gbinsert(lb_writeip, 1, 4, 2, 1);
		lb_writeip.setHorizontalAlignment(JLabel.CENTER);
		lb_writeip.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 0));

		gbinsert(tf_serverip, 1, 5, 1, 2);
		tf_serverip.setHorizontalAlignment(JTextField.CENTER);
		
		gbinsert(lb_writename, 1, 8, 2, 1);
		lb_writename.setHorizontalAlignment(JLabel.CENTER);
		lb_writename.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

		gbinsert(tf_username, 1, 9, 1, 2);
		tf_username.setHorizontalAlignment(JTextField.CENTER);

		gbinsert(p, 1, 13, 1, 6);
		p.setLayout(new GridLayout(3, 1));
		p.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		p.add(bt_enterChatBot);
		bt_enterChatBot.setMargin(new Insets(5, 5, 5, 5));
		p.add(bt_enterCom);
		bt_enterCom.setMargin(new Insets(5, 5, 5, 5));
		p.add(bt_exit);
		bt_exit.setMargin(new Insets(5, 5, 5, 5));
		bt_exit.setForeground(Color.red);
		this.pack();
		this.getContentPane().setBackground(new Color(186,134,252));
		p.setBackground(new Color(186,134,252));

		setBounds(300, 200, 300, 400);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		//connect();// 서버연결시도 (in,out객체생성)
		//new Thread(this).start();// 서버메시지 대기
		eventUp();

	}

	public void connect() {// (소켓)서버연결 요청
		try {

			// Socket s = new Socket(String host<서버ip>, int port<서비스번호>); 10.101.15.85
			String hostip=tf_serverip.getText();
			
			socket = new Socket(hostip, 8888);// 연결시도

			msg_in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			// in: 서버메시지 읽기객체 서버-----msg------>클라이언트

			msg_out = socket.getOutputStream();
			// out: 메시지 보내기, 쓰기객체 클라이언트-----msg----->서버
			flag=true;

		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "IP를 정확히 입력했는지 확인해주세요.", "WARNING MESSAGE", JOptionPane.WARNING_MESSAGE);
			//e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

		}

	}// connect

	public void sendMsg(String msg) {// 서버에게 메시지 보내기

		try {
			msg_out.write((msg + "\n").getBytes("UTF-8"));

		} catch (IOException e) {
			e.printStackTrace();
   
		}

	}// sendMsg

	private void eventUp() {// 이벤트소스-이벤트처리부 연결

		
		// Main화면(MainLetsChat)
		bt_enterChatBot.addActionListener(this);//Chatbot띄우기 
		bt_enterCom.addActionListener(this); //community띄우기
		bt_exit.addActionListener(this);

		//commmunity
		cm.bt_rcreate.addActionListener(this);
		cm.bt_renter.addActionListener(this);
		
		// 방만들기화면(CreateRoom)
		cr.bt_create.addActionListener(this);
		// 방입장하기화면(EnterRoom)
		er.bt_enter.addActionListener(this);
		er.li_room.addMouseListener(new MouseAdapter() {

			   @Override

			   public void mouseClicked(MouseEvent e) {

				   String str = er.li_room.getSelectedValue(); //"자바방--1"

				   if(str==null)return;
				   System.out.println("STR="+str);
				   selectedRoom = str.substring(0, str.indexOf("("));
				   er.tf_roomname.setText(selectedRoom);

			   }  

		});
		
		// 대화방(ChatClient)
		chr.bt_send.addActionListener(this);
		chr.bt_exit.addActionListener(this);

	}
	

	public void actionPerformed(ActionEvent e) {

		Object ob = e.getSource();

		String nickName; //사용자 닉네임
		nickName = tf_username.getText(); 
		
		int peoplenum=0;
		if (ob == bt_exit) {// 나가기(프로그램종료) 요청
			try {
				System.exit(0);
			} catch (Exception e1) {
				System.out.println("Close 예외처리 : " + e1.getMessage());
			} finally {
				System.exit(0);
			}
		}
		if (nickName.equals("")) {
			JOptionPane.showMessageDialog(null, "닉네임을 입력해주세요.", "ERROR MESSAGE", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if(ob==bt_enterChatBot) {
			String ia="";
			if(flag==false) {
				connect();// 서버연결시도 (in,out객체생성)
				new Thread(this).start();// 서버메시지 대기
				return;
			}
			roomname="챗봇#";
			
			String msg = roomname+"!!"+2;//챗봇#뒤에두개
			
			sendMsg("150|" + nickName);// 닉네임 전달
			sendMsg("160|" + msg);
			
			
		}
		else if(ob==bt_enterCom) {
			if(flag==false) {
				connect();// 서버연결시도 (in,out객체생성)
				new Thread(this).start();// 서버메시지 대기
				return;
			}
			cm.setVisible(true);
		}
		else if (ob == cm.bt_rcreate) {// 방만들기 화면 띄워주기
			sendMsg("150|" + nickName);// 대화명 전달
			System.out.println(tf_username.getText());
			cr.setVisible(true);
		} else if (ob == cm.bt_renter) {// 방들어가기 요청
			sendMsg("150|" + nickName);// 대화명 전달
			sendMsg("170|");
			//er.setVisible(true);
		} else if (ob == cr.bt_create) { //채팅방 생성 요청
			roomname = cr.tf_roomname.getText(); //채팅방 이름
			strpeople = cr.tf_people.getText(); //채팅방 참여가능 인원수 
			if (roomname.equals("")) {
				JOptionPane.showMessageDialog(null, "채방방 이름을 입력주세요.", "WARNING MESSAGE", JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (strpeople.equals("")) {
				JOptionPane.showMessageDialog(null, "인원 수를 입력해주세요.", "WARNING MESSAGE", JOptionPane.WARNING_MESSAGE);
				return;
			}
			try { //인원수 
				peoplenum = Integer.parseInt(strpeople);
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
			}
			if(peoplenum<2) //채팅방 생성 최소 설정 인원수 제한
			{
				JOptionPane.showMessageDialog(null, "설정 가능한 최소 인원수는 2명입니다. 다시 입력해주세요.", "WARNING MESSAGE", JOptionPane.WARNING_MESSAGE);
				return;
			}
			String msg = roomname+"!!"+strpeople;
			sendMsg("150|" + nickName);// 닉네임 전달
			sendMsg("160|" + msg);
			//입력창 초기화
			cr.tf_roomname.setText(""); 
			cr.tf_people.setText("");
			chr.ta_chat.setText("");
			chr.la_roominfo.setText(roomname + "(1/" + strpeople + ")"); //채팅방 제목 설정


		} else if (ob == er.bt_enter) { //채팅방 입장요청
			roomname = er.tf_roomname.getText();
			if (roomname.equals("")) {
				JOptionPane.showMessageDialog(null, "채방방 이름을 입력주세요", "WARNING MESSAGE", JOptionPane.WARNING_MESSAGE);
				return;
			}
			sendMsg("150|" + nickName);// 대화명 전달
			sendMsg("200|" + roomname);
			
			//입력창 초기화
			er.tf_roomname.setText("");
			chr.ta_chat.setText("");

		} else if (ob == chr.bt_exit) {// 대화방 나가기 요청
			sendMsg("400|");
			chr.setVisible(false);
			setVisible(true);
			
		} else if (ob == chr.bt_send) {// (TextField입력)메시지 보내기 요청

			String msg = chr.ta_sendmsg.getText();
			if (msg.length() > 0) {
				msg = msg.replaceAll("(\r\n|\r|\n|\n\r)", "!!changline!!"); //줄바꿈 전송하기
				sendMsg("300|" + msg);
				chr.ta_sendmsg.setText("");
			}

		}
		
	}// actionPerformed
	
	//줄바꿈시 줄간격 맞추기
	public static String padLeft(String s, int n) {
        return String.format("%" + n + "s", s);
    }

	public void run() {// 서버가 보낸 메시지 읽기
		try {
			while (flag) {
				String msg = msg_in.readLine();// msg: 서버가 보낸 메시지

				// msg==> "300|안녕하세요" "160|'채팅방이름'!!'인원수'"
				String msgs[] = msg.split("\\|");
				String protocol = msgs[0];

				switch (protocol) {

				case "300":
					//채팅 여러줄 전송받기
					String smsgs=msgs[1];
					String chatmsgs[] = smsgs.split("!!changline!!"); 
					int len = chatmsgs[0].indexOf(">>");
					chr.ta_chat.append(chatmsgs[0] + "\n");
					int i;
					//줄바꿈시 줄 간격 맞추기
					for(i=1; i<chatmsgs.length;i++)
					{
						String str=chatmsgs[i];
						int k = len + str.length();
						String msgline = padLeft(chatmsgs[i],k);
						chr.ta_chat.append(msgline + "\n");
					}
					chr.ta_chat.setCaretPosition(chr.ta_chat.getText().length());
					break;

				case "160":// 방만들기
					// 방정보를 List에 뿌리기
					if (msgs.length > 1) {
						// 개설된 방이 한개 이상이었을때 실행
						// 개설된 방없음 ----> msg="160|" 였을때 에러
						String roomNames[] = msgs[1].split(",");
						// "자바방--1,오라클방--1,JDBC방--1"
						roomInfo.setListData(roomNames);
					}
					break;
				case "170":// 개설된 채팅 리스트 
					
					//if(msgs[1]=="") JOptionPane.showMessageDialog(null, "개설된 채팅방이 없습니다. 채팅방을 생성해주세요.", "WARNING MESSAGE", JOptionPane.WARNING_MESSAGE);
					String roomList[] = msgs[1].split(","); //채팅참가자 이름목록
					er.li_room.setListData(roomList);//채팅참가인원 리스트 재설정
					er.setVisible(true);
					break;

				case "175":// (대화방에서) 대화방 인원정보 리스트 
					String myRoomMembers[] = msgs[1].split(","); //채팅참가자 이름목록
					int nowppl=myRoomMembers.length; //방인원
					chr.la_roominfo.setText(roomname +"("+nowppl+"/"+strpeople+")");  //채팅참가인원 정보 재설정
					chr.li_member.setListData(myRoomMembers);//채팅참가인원 리스트 재설정
					break;
					
				case "200":// 대화방 입장
					chr.ta_chat.append("============[" + msgs[1] + "]님 입장============\n");
					chr.ta_chat.setCaretPosition(chr.ta_chat.getText().length());
					break;

				case "400":// 대화방 퇴장
					chr.ta_chat.append("============[" + msgs[1] + "]님 퇴장============\n");
					chr.ta_chat.setCaretPosition(chr.ta_chat.getText().length());
					
					sendMsg("175|");// 대화방내 인원정보 요청
					break;

				case "202":// 개설된 방의 타이틀 제목 얻기
					String roominfomsgs[]=msgs[1].split("!!");
					roomname = roominfomsgs[0];
					strpeople = roominfomsgs[1];
					sendMsg("175|"); //채팅방 인원 정보요청
					setVisible(false);
					cr.setVisible(false);
					er.setVisible(false);
					chr.setVisible(true);
					break;
					
				case "205":// 개설된 방의 타이틀 제목 얻기
					String chatbotroominfomsgs[]=msgs[1].split("!!");
					roomname = chatbotroominfomsgs[1];
					strpeople = chatbotroominfomsgs[2];
					String[] members=new String[2];
					members[0]=chatbotroominfomsgs[0];
					members[1]="상담";
					chr.li_member.setListData(members);//채팅참가인원 리스트 재설정
					setVisible(false);
					cr.setVisible(false);
					er.setVisible(false);
					chr.setVisible(true);
					break;
				case "700":
					if(msgs[1].equals("used")) //채팅방 이름 중복검사
						JOptionPane.showMessageDialog(null, "이미 사용중인 채팅방입니다. 다른 이름을 입력해주세요.", "WARNING MESSAGE", JOptionPane.WARNING_MESSAGE);
						
					else if(msgs[1].equals("full")) //채팅방 수용인원 초과
						JOptionPane.showMessageDialog(null, "수용 최대 인원을 초과하였습니다. 다른 채팅방을 이용해주세요.", "WARNING MESSAGE", JOptionPane.WARNING_MESSAGE);
						
					else if(msgs[1].equals("none")) //채팅방 검색 오류
						JOptionPane.showMessageDialog(null, "채팅방이 존재하지 않습니다. 입력을 확인해주세요", "WARNING MESSAGE", JOptionPane.WARNING_MESSAGE);
						
					break;
				case "800":
					sendMsg("400|");
					chr.setVisible(false);
					setVisible(true);

				}// 클라이언트 switch
			}

		} catch (IOException e) {
			e.printStackTrace();
		}catch (ArrayIndexOutOfBoundsException e1) {
			JOptionPane.showMessageDialog(null, "개설된 채팅방이 없습니다. 채팅방을 생성해주세요.", "WARNING MESSAGE", JOptionPane.WARNING_MESSAGE);
			
		}
	}// run

	public static void main(String[] args) {
		new MainLetsChat();
	}

}
