package LetsChat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CreateRoom extends JFrame {

	JLabel la_roomname, la_people, la_blank;
	JTextField tf_roomname,tf_people;
	JButton bt_create;   
	JPanel p; 
	IntegerDocument id;
	GridBagLayout gBag;
	GridLayout gl;
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
    
	public CreateRoom() {
		
		setTitle("Let's Chat: 채팅방 만들기");
		
		la_roomname = new JLabel("채팅방 이름");
		la_roomname.setFont(new Font("SanSerif",Font.BOLD,15));
		tf_roomname = new JTextField(15);
		la_people = new JLabel("인원 수");
		la_people.setFont(new Font("SanSerif",Font.BOLD,15));
		tf_people = new JTextField(10);
		id = new IntegerDocument();
		tf_people.setDocument(id);
		bt_create = new JButton("생성하기");
		la_blank = new JLabel("");
		
		
		p = new JPanel();
		gBag = new GridBagLayout();
        setLayout(gBag);
        
        la_roomname.setHorizontalAlignment(JLabel.CENTER);
        gbinsert(la_roomname,1,1,1,2);
        la_roomname.setBorder(BorderFactory.createEmptyBorder(0 , 0 , 5 , 0));
        
        tf_roomname.setHorizontalAlignment(JTextField.CENTER);
        gbinsert(tf_roomname,1,3,1,3);
        
        la_people.setHorizontalAlignment(JLabel.CENTER);
        gbinsert(la_people,1,6,1,2);
        la_people.setBorder(BorderFactory.createEmptyBorder(15 , 0 , 5 , 0));
        
        tf_people.setHorizontalAlignment(JTextField.CENTER);
        gbinsert(tf_people,1,8,1,3);
        
        gbinsert(la_blank,1,12,1,3);
        la_blank.setBorder(BorderFactory.createEmptyBorder(15 , 0 , 0 , 0));
        
        gbinsert(bt_create,1,15,1,3);
        bt_create.setMargin(new Insets(5,5,5,5));
       
	    
        this.pack();
        this.getContentPane().setBackground(Color.orange);
        setBounds(300,200, 300, 300);
	}
}
