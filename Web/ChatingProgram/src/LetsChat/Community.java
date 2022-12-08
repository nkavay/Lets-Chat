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

public class Community extends JFrame {
	JButton bt_rcreate, bt_renter;
	JPanel p;
	JLabel lb_title;
	GridBagLayout gBag;
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
	public Community() {
		// 메인 화면 title
		setTitle("작은 무비:Community");
		
		lb_title = new JLabel("Community");
		lb_title.setFont(new Font("Serif", Font.BOLD, 40));
		
		p = new JPanel();
		p.setBackground(new Color(186,134,252));
		
		bt_rcreate = new JButton("방만들기"); //채팅방 생성창 이동 버튼
		bt_renter = new JButton("방들어가기"); //채팅방 입장창 이동 버튼
		
		
		gBag = new GridBagLayout();
		setLayout(gBag);
		
		gbinsert(lb_title, 1, 1, 1, 3);
		lb_title.setHorizontalAlignment(JLabel.CENTER);
		lb_title.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

		gbinsert(bt_rcreate,1,4,1,2);
		bt_rcreate.setMargin(new Insets(5, 5, 5, 5));
		gbinsert(bt_renter,1,6,1,2);
		bt_renter.setMargin(new Insets(5, 5, 5, 5));
		
		this.pack();
		this.getContentPane().setBackground(new Color(186,134,252));
	    setBounds(300,200, 300, 300);
	}

}
