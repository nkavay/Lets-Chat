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
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EnterRoom extends JFrame {
	
	JLabel la_searchroom,la_blank;
	JTextField tf_roomname;
	JButton bt_enter;   
	
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
		
		setTitle("Let's Chat: 채팅방 입장하기");
		la_searchroom = new JLabel("채팅방 이름을 검색해주세요");
		la_searchroom.setFont(new Font("SanSerif",Font.BOLD,15));
		tf_roomname = new JTextField(20);
		la_searchroom.setFont(new Font("SanSerif",Font.PLAIN,15));;
		bt_enter = new JButton("입장하기");
		la_blank = new JLabel("");
		
		gBag = new GridBagLayout();
		setLayout(gBag);
		
		la_searchroom.setHorizontalAlignment(JLabel.CENTER);
		gbinsert(la_searchroom,1,1,1,2);
		la_searchroom.setBorder(BorderFactory.createEmptyBorder(0 , 0 , 5 , 0));
		
		tf_roomname.setHorizontalAlignment(JTextField.CENTER);
		gbinsert(tf_roomname,1,3,1,3);
		
		gbinsert(la_blank,1,6,1,3);
		la_blank.setBorder(BorderFactory.createEmptyBorder(15 , 0 , 0 , 0));
		
		gbinsert(bt_enter,1,9,1,3);
		bt_enter.setMargin(new Insets(5,5,5,5));
		
		this.pack();
        this.getContentPane().setBackground(Color.orange);
	    setBounds(300,200, 300, 200);
	}
}
