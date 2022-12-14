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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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


public class MainLetsChat extends JFrame implements ActionListener, Runnable{

	JList<String> roomInfo,roomInwon,waitInfo;
    JScrollPane sp_roomInfo, sp_roomInwon, sp_waitInfo;
    JButton bt_rcreate, bt_renter, bt_exit;
    JLabel lb_title, lb_writename;
    JTextField tf_username;
   

    JPanel p,pb;
    CreateRoom cr;
    EnterRoom er;
    ChatRoom chr;

    //소켓 입출력객체
    BufferedReader in;
    OutputStream out;

    String selectedRoom;
    GridBagLayout gBag;
    public void gbinsert(Component c, int x, int y, int w, int h){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill= GridBagConstraints.BOTH;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        gBag.setConstraints(c,gbc);
        this.add(c);
    }
    
    public MainLetsChat(){
    	
    	//메인 화면 
    	setTitle("Let's Chat");
    	
    	chr = new ChatRoom(); //chatting class 객체생성 
    	cr = new CreateRoom(); //방만들기  class 객체생성 
    	er = new EnterRoom(); //방입장하기 class 객체생성 
    	
    	lb_title = new JLabel("Let's Chat");
    	lb_title.setFont(new Font("Serif",Font.BOLD,40));
    	lb_writename = new JLabel("닉네임을 입력해주세요"); 
    	lb_writename.setFont(new Font("Serif",Font.PLAIN,12));
    	tf_username = new JTextField(20);
    	tf_username.setFont(new Font("Serif",Font.PLAIN,15));
    	bt_rcreate = new JButton("방만들기");
        bt_renter = new JButton("방들어가기");
        bt_exit = new JButton("종료하기"); 
        
        p = new JPanel();
        gBag = new GridBagLayout();
        setLayout(gBag);
        
        gbinsert(lb_title,1,1,1,3);
        lb_title.setHorizontalAlignment(JLabel.CENTER);
        lb_title.setBorder(BorderFactory.createEmptyBorder(0 , 0 , 30 , 0));
        
        gbinsert(lb_writename,1,4,2,1);
        lb_writename.setHorizontalAlignment(JLabel.CENTER);
        lb_writename.setBorder(BorderFactory.createEmptyBorder(0 , 0 , 5 , 0));
        
        gbinsert(tf_username,1,5,1,2);
        tf_username.setHorizontalAlignment(JTextField.CENTER);
        //tf_username.setPreferredSize(new Dimension(20, 30));
        
        gbinsert(p,1,9,1,6);
        p.setLayout(new GridLayout(3,1));
        p.setBorder(BorderFactory.createEmptyBorder(10 , 0 , 0, 0));
        p.add(bt_rcreate);
        bt_rcreate.setMargin(new Insets(5,5,5,5));
        p.add(bt_renter);
        bt_renter.setMargin(new Insets(5,5,5,5));
        p.add(bt_exit);
        bt_exit.setMargin(new Insets(5,5,5,5));
        this.pack();
        this.getContentPane().setBackground(Color.orange);
        p.setBackground(Color.orange);
        //

        setBounds(300,200, 300, 400);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE); 
        
        connect();//서버연결시도 (in,out객체생성)
        new Thread(this).start();//서버메시지 대기
        //String nickName = tf_username.getText();//JOptionPane.showInputDialog(this,"대화명:");
        //System.out.println(nickName);
        

        eventUp();
        
    }
    public void connect(){//(소켓)서버연결 요청
    	try {

    		//Socket s = new Socket(String host<서버ip>, int port<서비스번호>); 
    		Socket s = new Socket("192.168.45.140", 8888);//연결시도
    		
    		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    		//in: 서버메시지 읽기객체    서버-----msg------>클라이언트

    		out = s.getOutputStream();
    		//out: 메시지 보내기, 쓰기객체    클라이언트-----msg----->서버

    	} 
    	catch (UnknownHostException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();

    	}  

    }//connect
    
    public void sendMsg(String msg){//서버에게 메시지 보내기

    	try {
    		out.write(  (msg+"\n").getBytes()  );

    	}catch (IOException e) {
    		e.printStackTrace();

    	}

    }//sendMsg
    private void eventUp(){//이벤트소스-이벤트처리부 연결

	    //Main화면(MainLetsChat)
    	bt_rcreate.addActionListener(this);
	    bt_renter.addActionListener(this);
	    bt_exit.addActionListener(this);

	    //방만들기화면(CreateRoom)
	    cr.bt_create.addActionListener(this);
    
	    //방입장하기화면(EnterRoom)
	    er.bt_enter.addActionListener(this);
	    
	    //대화방(ChatClient)
	    chr.tf_sendmsg.addActionListener(this);
	    chr.bt_send.addActionListener(this);
	    chr.bt_exit.addActionListener(this);

    }
    
    public void actionPerformed(ActionEvent e) {

    	Object ob = e.getSource();
    	String roomname;
    	String strpeople;
    	String nickName;
    	nickName = tf_username.getText();


    	if(ob==bt_rcreate){//방만들기 화면 띄워주기
    		sendMsg("150|"+nickName);//대화명 전달
    		System.out.println(tf_username.getText());
    		cr.setVisible(true);
    		//String title = JOptionPane.showInputDialog(this,"방제목:");
    	}
    	else if(ob==bt_renter){//방들어가기 요청
    		sendMsg("150|"+nickName);//대화명 전달
    		er.setVisible(true);
    	}

    	else if(ob==cr.bt_create)
    	{
    		//방제목을 서버에게 전달
    		roomname = cr.tf_roomname.getText();
    		strpeople = cr.tf_people.getText();
    		try{
                int people = Integer.parseInt(strpeople);
                System.out.println(people);
            }
            catch (NumberFormatException ex){
                ex.printStackTrace();
            }
    		System.out.println(roomname);
    		sendMsg("150|"+nickName);//대화명 전달
    		sendMsg("160|"+roomname);


    		chr.la_roominfo.setText(roomname+"("+strpeople+")");

    		sendMsg("175|");//대화방내 인원정보 요청

			////start (주석처리함)
//    		setVisible(false);
//    		cr.setVisible(false);
//    		chr.setVisible(true); //대화방이동
			////end
    	}
    	else if(ob==er.bt_enter) 
    	{
    		roomname = er.tf_roomname.getText();
    		
    		chr.la_roominfo.setText(roomname);//+"("+strpeople+")");
    		sendMsg("200|"+ roomname);
			sendMsg("150|"+nickName);//대화명 전달
			sendMsg("175|");//대화방내 인원정보 요청

			////start (주석처리함)
//    		setVisible(false);
//    		er.setVisible(false);
//    		chr.setVisible(true);
			////end
    	}

    	else if(ob==chr.bt_exit){//대화방 나가기 요청
    		
    		sendMsg("400|");
    		chr.setVisible(false);
    		setVisible(true); 
    	}
    	else if(ob==chr.bt_send){//(TextField입력)메시지 보내기 요청

    		String msg = chr.tf_sendmsg.getText();
    		
    		if(msg.length()>0){
    			sendMsg("300|"+msg); 
    			//chr.ta_chat.append(msg);
    			//chr.ta_chat.setCaretPosition(chr.ta_chat.getText().length());
    			chr.tf_sendmsg.setText("");
    		}

    	}
    	else if(ob==bt_exit){//나가기(프로그램종료) 요청

    		System.exit(0);//현재 응용프로그램 종료하기

    	}  
    }//actionPerformed 
    
   

    
    public void run(){//서버가 보낸 메시지 읽기
        //왜 run메소드 사용? GUI프로그램실행에 영향 미치지않는 코드 작성.
        //메소드호출은 순차적인 실행!!  스레드메소드는 동시실행(기다리지 않는 별도 실행)!!
        try {
        	while(true){

        		String msg = in.readLine();//msg: 서버가 보낸 메시지

        		//msg==> "300|안녕하세요"  "160|자바방--1,오라클방--1,JDBC방--1"
        		String msgs[] = msg.split("\\|");
        		String protocol = msgs[0];

        		switch(protocol){

        			case "300":
        				chr.ta_chat.append(msgs[1]+"\n");
        				System.out.println(msgs[1]+"\n");
        				chr.ta_chat.setCaretPosition(chr.ta_chat.getText().length());
        				break;

        					
        			case "160"://방만들기

        				//방정보를 List에 뿌리기
        				if(msgs.length > 1){

        					//개설된 방이 한개 이상이었을때 실행
        					//개설된 방없음 ---->  msg="160|" 였을때 에러 
        					String roomNames[] = msgs[1].split(",");
        					//"자바방--1,오라클방--1,JDBC방--1"
        					roomInfo.setListData(roomNames);
        				}
        				break;
          
        			/*case "170"://(대기실에서) 대화방 인원정보
        				String roomInwons[] = msgs[1].split(",");
        				roomInwon.setListData(roomInwons);
        				break;*/

        			case "175"://(대화방에서) 대화방 인원정보
        				String myRoomInwons[] = msgs[1].split(",");
        				chr.li_inwon.setListData(myRoomInwons);
        				break;

        			/*case "180"://대기실 인원정보
        				String waitNames[] = msgs[1].split(",");
        				waitInfo.setListData(waitNames);
        				break;*/

					////start (코드 추가)
					case "210":
						er.setVisible(false);
						chr.setVisible(true);
						break;

					case "161":
						setVisible(false);
						cr.setVisible(false);
    					chr.setVisible(true); //대화방이동
						break;
					////end

					case "200"://대화방 입장
        				chr.ta_chat.append("=========["+msgs[1]+"]님 입장=========\n");
        				chr.ta_chat.setCaretPosition(chr.ta_chat.getText().length());
        				break;

        			case "400"://대화방 퇴장
        				chr.ta_chat.append("=========["+msgs[1]+"]님 퇴장=========\n");
        				chr.ta_chat.setCaretPosition(chr.ta_chat.getText().length());
        				break;

        			case "202"://개설된 방의 타이틀 제목 얻기
        				chr.la_roominfo.setText("채팅방-["+msgs[1]+"]");
        				break;


        		}//클라이언트 switch
        	}

        }catch (IOException e) {
        	e.printStackTrace();
        }
    }//run

    public static void main(String[] args) {
    	new MainLetsChat();
    }
	
}
