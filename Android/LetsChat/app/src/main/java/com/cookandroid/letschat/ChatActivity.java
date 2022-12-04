package com.cookandroid.letschat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbarChat;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    LinearLayout llChatChat;

    BufferedReader in;
    OutputStream out;

    Handler mHandler;
    Socket s;

    String nickname;
    String roomname;
    Integer type;
    Integer headcount;
    Integer headcountBe = 0;
    Boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mHandler = new Handler();

        setSupportActionBar(toolbarChat);
        toolbarChat = findViewById(R.id.toolbar_chat);

        drawerLayout = findViewById(R.id.drawer_chat);
        navigationView = findViewById(R.id.navigation_view);
        llChatChat = findViewById(R.id.ll_chat_chat);
        Button btnSend = findViewById(R.id.btn_chat_send);
        EditText editMsg = findViewById(R.id.edit_chat_msg);

        Intent intent = getIntent();
        type = intent.getExtras().getInt("type");
        nickname = intent.getExtras().getString("nickname");
        roomname = intent.getExtras().getString("roomname");

        if(type == 1) {
            headcount = intent.getExtras().getInt("headcount");
        }

        setSupportActionBar(toolbarChat);

        new Thread(() -> connect()).start();

        btnSend.setOnClickListener(view -> {
            String msg = editMsg.getText().toString();
            editMsg.setText("");
            if(!msg.isEmpty()){
                new Thread(() -> sendMsg("300|" + msg)).start();
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_leave:
                    new Thread(() -> sendMsg("400|")).start();// 대화방 나가기 요청
                    Intent mainIntent = new Intent(ChatActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
            }
            return false;
        });
    }

    class msgUpdate implements Runnable{
        private String msg;
        public msgUpdate(String msg) {
            this.msg = msg;
        }

        // 프로토콜별 실행처리
        @Override
        public void run() {
            // msg==> "300|안녕하세요" "160|자바방--1,오라클방--1,JDBC방--1"
            String msgs[] = msg.split("\\|");
            String protocol = msgs[0];

            switch (protocol) {
                case "300":
                    //채팅 여러줄 전송받기
                    String smsgs = msgs[1];
                    String chatmsgs[] = smsgs.split("!!changline!!");

                    int i;
                    for (i = 0; i < chatmsgs.length; i++) {
                        String str[] = chatmsgs[i].split(">> ");
                        str[0] = str[0].substring(1, str[0].length()-1);
                        addMsg(str[0], str[1]);
                    }
                    break;

                case "160":// 방만들기
                    if (msgs.length > 1) {
                        // 개설된 방이 한개 이상이었을때 실행
                        // 개설된 방없음 ----> msg="160|" 였을때 에러
                        String roomnames[] = msgs[1].split(",");
                    }
                    break;

                case "175":// (대화방에서) 대화방 인원정보 리스트
                    String roomCandis[] = msgs[1].split(","); //채팅참가자 이름목록
                    setCandi(roomCandis);
                    int nowppl = roomCandis.length; //방인원
                    toolbarChat.setTitle(roomname +" ("+nowppl+"/"+headcount+")");
                    break;

                case "200":// 대화방 입장
                    addInfo(msgs[1] + "님이 입장하셨습니다");
                    break;

                case "400":// 대화방 퇴장
                    addInfo(msgs[1] + "님이 퇴장하셨습니다");
                    new Thread(() -> sendMsg("175|")).start();  // 대화방내 인원정보 요청
                    break;

                case "202":// 개설된 방의 타이틀 제목 얻기
                    String roominfomsgs[] = msgs[1].split("!!");
                    roomname = roominfomsgs[0];
                    headcount = Integer.parseInt(roominfomsgs[1]);
                    new Thread(() -> sendMsg("175|")).start();
                    break;

                case "700":
                    String warningMsg = "\" is not found.";
                    if(msgs[1].equals("full")){
                        warningMsg = "\" is already full.";
                    } else if(msgs[1].equals("used")){
                        warningMsg = "\" is already used.";
                    }

                    new AlertDialog.Builder(ChatActivity.this)
                            .setTitle("warning")
                            .setMessage("Room named \"" + roomname + warningMsg)
                            .setPositiveButton("ok", (dialogInterface, i1) -> finish())
                            .setCancelable(false)
                            .create()
                            .show();
            }   // 클라이언트 switch
        }
    }

    // 소켓 연결
    public void connect(){
        try {
            s = new Socket("172.30.1.76", 8888);
            connected = true;
            in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
            out = s.getOutputStream();

            // 새로운 방 생성, 기존 방 입장 구분
            sendMsg("150|" + nickname);
            if(type == 1) sendMsg("160|" + roomname + "!!" + headcount);
            else if(type == 2) sendMsg("200|" + roomname);

            while(true) {
                String read = in.readLine();
                if(!read.isEmpty()){
                    mHandler.post(new msgUpdate(read));
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 메시지 전송
    public void sendMsg(String msg){
        try {
            out.write((msg + "\n").getBytes("UTF-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 채팅 참여 인원 리스트 관리
    public void setCandi(String roomCandi[]){
        Menu menu = navigationView.getMenu();
        MenuItem cate = menu.getItem(0);
        SubMenu subMenu = cate.getSubMenu();

        int i;
        if(roomCandi.length > headcountBe){ // 인원이 증가했을 경우
            for(i=0; i<headcountBe; i++){
                subMenu.getItem(i).setTitle(roomCandi[i]);
            }
            for(i=headcountBe; i<roomCandi.length; i++){
                subMenu.add(0, i, 0, roomCandi[i]);
                subMenu.getItem(i).setEnabled(false);
            }
        } else if(roomCandi.length < headcountBe) { // 인원이 감소했을 경우
            for(i=0; i<roomCandi.length; i++){
                subMenu.getItem(i).setTitle(roomCandi[i]);
            }
            for(i=roomCandi.length; i<headcountBe; i++){
                subMenu.removeItem(i);
            }
        } else {    // 인원이 같을 경우
            for(i=0; i<roomCandi.length; i++){
                subMenu.getItem(i).setTitle(roomCandi[i]);
            }
        }

        headcountBe = roomCandi.length;
    }

    // 메시지 UI
    public void addMsg(String nickname, String msg){
        LinearLayout llMsg  = new LinearLayout(getApplicationContext());
        TextView tvNickname = new TextView(getApplicationContext());
        TextView tvNewMsg = new TextView(getApplicationContext());

        LinearLayout.LayoutParams llMsgParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llMsgParams.setMargins(50,10,50,30);
        llMsg.setLayoutParams(llMsgParams);
        llMsg.setOrientation(LinearLayout.VERTICAL);

        llMsg.setGravity(this.nickname.equals(nickname) ? Gravity.RIGHT : Gravity.LEFT);

        LinearLayout.LayoutParams tvNicknameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tvNicknameParams.setMargins(10,0,10,8);
        tvNickname.setLayoutParams(tvNicknameParams);
        tvNickname.setText(nickname);

        LinearLayout.LayoutParams tvNewMsgParams = new LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.WRAP_CONTENT);
        tvNewMsg.setLayoutParams(tvNewMsgParams);
        tvNewMsg.setBackgroundResource(R.drawable.bg_msg_textview);
        tvNewMsg.setText(msg);

        llMsg.addView(tvNickname);
        llMsg.addView(tvNewMsg);
        llChatChat.addView(llMsg);
    }

    // 입장, 퇴장 정보 UI
    public void addInfo(String msg){
        TextView tvNewInfo = new TextView(getApplicationContext());
        tvNewInfo.setText(msg);
        tvNewInfo.setBackgroundResource(R.drawable.bg_info_textview);
        tvNewInfo.setTextColor(Color.WHITE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,50,10,50);
        tvNewInfo.setLayoutParams(params);

        llChatChat.addView(tvNewInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate (R.menu.toolbar_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_menu: // 왼쪽 상단 버튼 눌렀을 때
                Log.d("item clicked", "");
                drawerLayout.openDrawer(GravityCompat.END);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() { //뒤로가기 했을 때
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            new Thread(() -> sendMsg("400|")).start();// 대화방 나가기 요청
            Intent mainIntent = new Intent(ChatActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(connected){
            try {
                out.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

