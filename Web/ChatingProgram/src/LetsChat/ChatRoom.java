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
		
		/*
		cards = new JPanel(new CardLayout());
		
		card1 = new JPanel();
		card1.setLayout(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.fill=GridBagConstraints.BOTH;
         //x,y축 다채움
        //gbc.weightx=0.2;// 비율이 0.2:0.1이므로 버튼의 크기는 가로축으로 2배
		gbc.weighty=0.1;
        gbc.gridx=0;  
        gbc.gridy=0;   //버튼이 두개로 0,0 기준으로 생성
		la_roominfo.setHorizontalAlignment(JLabel.CENTER);
		card1.add(la_roominfo,gbc);
		
		gbc.weighty=1;
		gbc.gridx=0;  
        gbc.gridy=0;   //버튼이 두개로 0,0 기준으로 생성
		card1.add(ta_chat,gbc);
		
		gbc.weighty=0.15;
		gbc.gridx=0;  
        gbc.gridy=0;   //버튼이 두개로 0,0 기준으로 생성
		card1.add(la_roominfo,gbc);
		
		card2 = new JPanel();
		gbl2 = new GridBagLayout();
		card2.setLayout(gbl2);
		
		
		cards.add(card1,"Pannel1");
		cards.add(card2, "Pannel2");
		add(card1);
		
		CardLayout cl = (CardLayout) cards.getLayout();
		bt_info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.next(cards);
			}
			
		});*/
		
		
		p = new JPanel();
		la_roominfo.setBounds(170,10,100,20); 
		sp_ta.setBounds(10,30,340,450); 
		sp_send.setBounds(10,490,265,60); 
		//tf_sendmsg.setBounds(8,520,270,30); 
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
		//p.add(tf_sendmsg);
		p.add(la_member);
		p.add(sp_list);
		
		p.add(bt_exit);

		add(p);
		
		this.pack();
		setBounds(300,200,470,600);
		//setDefaultCloseOperation(EXIT_ON_CLOSE); 

		//setVisible(true);
		ta_sendmsg.requestFocus();   
		//tf_sendmsg.requestFocus();   
		
		
	}

}
