package LetsChat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class EnterRoom extends JFrame {
	
	JLabel la_searchroom,la_blank;
	JTextField tf_roomname;
	JList<String> li_room;
	JButton bt_enter;   
	JScrollPane sp_roomlist; 
	
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
    
	public EnterRoom() {
		
		setTitle("Let's Chat: 채팅방 입장하기"); //채팅방 입력창 title
		
		la_searchroom = new JLabel("채팅방 이름을 검색해주세요");
		la_searchroom.setFont(new Font("SanSerif",Font.BOLD,15));
		
		tf_roomname = new JTextField(20); //입장할 채팅방 이름 입력창
		
		li_room = new JList<String>(); //채팅방 리스트
		sp_roomlist = new JScrollPane(li_room);
		
		bt_enter = new JButton("입장하기"); //채팅방 입장 버튼
		la_blank = new JLabel("");
		
		gBag = new GridBagLayout();
		setLayout(gBag);
		
		la_searchroom.setHorizontalAlignment(JLabel.CENTER);
		gbinsert(la_searchroom,1,1,1,2);
		la_searchroom.setBorder(BorderFactory.createEmptyBorder(10 , 0 , 5 , 0)); //위 왼쪽 아래 오른쪽
		
		tf_roomname.setHorizontalAlignment(JTextField.CENTER);
		gbinsert(tf_roomname,1,3,1,3);
		
		gbinsert(sp_roomlist,1,6,1,10);
		
		gbinsert(la_blank,1,18,1,3);
		la_blank.setBorder(BorderFactory.createEmptyBorder(15 , 0 , 0 , 0));
		
		gbinsert(bt_enter,1,21,1,3);
		bt_enter.setMargin(new Insets(5,5,5,5));
		
		this.pack();
		this.getContentPane().setBackground(new Color(186,134,252));
	    setBounds(300,200, 300, 500);
	}
}
