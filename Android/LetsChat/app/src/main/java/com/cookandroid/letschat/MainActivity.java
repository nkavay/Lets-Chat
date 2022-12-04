package com.cookandroid.letschat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editNickname = (EditText) findViewById(R.id.edit_main_nickname);
        Button btnCreate = (Button) findViewById(R.id.btn_main_create);
        Button btnEnter = (Button) findViewById(R.id.btn_main_enter);

        // 새로운 채팅방 정보 입력 페이지 이동
        btnCreate.setOnClickListener(view -> {
            String nickname = editNickname.getText().toString();

            if(!nickname.isEmpty()){
                Bundle bundle = new Bundle();
                bundle.putString("nickname", nickname);

                NewChatFragment newChatFragment = new NewChatFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();

                newChatFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.ll_main, newChatFragment).commit();
            } else {
                Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show();
            }
        });
        
        // 기존 채팅방 정보 입력 페이지 이동
        btnEnter.setOnClickListener(view -> {
            String nickname = editNickname.getText().toString();

            if(!nickname.isEmpty()){
                Bundle bundle = new Bundle();
                bundle.putString("nickname", nickname);

                EnterChatFragment enterChatFragment = new EnterChatFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();

                enterChatFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.ll_main, enterChatFragment).commit();
            } else {
                Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show();
            }
        });
    }

    interface onBackPressedListener {
        void onBackPressed();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for(Fragment fragment : fragmentList){
            if(fragment instanceof onBackPressedListener){
                ((onBackPressedListener)fragment).onBackPressed();
                return;
            }
        }
    }
}