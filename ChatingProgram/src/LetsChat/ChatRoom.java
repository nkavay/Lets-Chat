package LetsChat;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatRoom extends JFrame{
	
	//채팅방
    JTextField tf_sendmsg;
    JLabel la_member,la_roominfo;
	JTextArea ta_chat,ta_sendmsg;
	JScrollPane sp_ta,sp_list,sp_send;    
	JList<String> li_inwon;
	JButton bt_exit, bt_send, bt_info;   
	JPanel p, cards, card1,card2; 
	
	FlowLayout fl;
	
	GridBagLayout gbl1, gbl2;
	

	
	public ChatRoom() {
		setTitle("채팅방");
		
		la_roominfo = new JLabel("채팅방");
		ta_sendmsg = new JTextArea();
		ta_sendmsg.setLineWrap(true);
		//tf_sendmsg = new JTextField(15);   
		bt_send = new JButton("전송");
		
		ta_chat = new JTextArea(); //chatting 내용 
		ta_chat.setLineWrap(true);//TextArea 가로길이를 벗어나는 text발생시 자동 줄바꿈
		ta_chat.setEditable(false);
		
		la_member = new JLabel("대화 참여자");
		li_inwon = new JList<String>();

		sp_ta = new JScrollPane(ta_chat);
		sp_list = new JScrollPane(li_inwon);
		sp_send = new JScrollPane(ta_sendmsg);
		
		bt_info = new JButton("정보");
		bt_exit = new JButton("나가기");
		
		
		p = new JPanel();
		la_roominfo.setBounds(170,10,100,20); 
		sp_ta.setBounds(10,30,340,450); 
		sp_send.setBounds(10,490,265,60); 
		bt_send.setBounds(275, 490, 75, 60);

		la_member.setBounds(370, 10, 100, 20);
		sp_list.setBounds(360,30,100,450); 
		bt_exit.setBounds(360,490,100,60); 
		
		p.setLayout(null);
		p.setBackground(Color.orange);
		p.add(la_roominfo);
		p.add(sp_ta);
		p.add(bt_send);
		p.add(sp_send);
		p.add(la_member);
		p.add(sp_list);
		
		p.add(bt_exit);

		add(p);
		
		this.pack();
		setBounds(300,200,470,600);
		//setDefaultCloseOperation(EXIT_ON_CLOSE); 

		ta_sendmsg.requestFocus();   
	}

}
