package com.cookandroid.letschat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class EnterChatFragment extends Fragment implements com.cookandroid.letschat.MainActivity.onBackPressedListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enter_chat, container, false);

        Bundle extra = this.getArguments();
        String nickname = null;

        if(extra != null){
             nickname = extra.getString("nickname");
        }

        ImageView ivEnterBack = view.findViewById(R.id.iv_enter_back);
        Button btnEnter = view.findViewById(R.id.btn_enter_enter);
        EditText editRoomname = view.findViewById(R.id.edit_enter_roomname);

        String finalNickname = nickname;

        // 이전 이미지 클릭 시
        ivEnterBack.setOnClickListener(view1 -> {
            finishFragment();
        });

        // 기존 채팅방 입장
        btnEnter.setOnClickListener(view1 -> {
            String roomname = editRoomname.getText().toString();

            if(!roomname.isEmpty()){
                startChatActivity(finalNickname, roomname);
            } else {
                Toast.makeText(getActivity(), "채팅방 제목을 입력해주세요", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void startChatActivity(String nickname, String roomname){
        Intent intent = new Intent(getActivity(), com.cookandroid.letschat.ChatActivity.class);
        intent.putExtra("type", 2); // 신규 채팅방: 1, 기존 채팅방: 2
        intent.putExtra("nickname", nickname);
        intent.putExtra("roomname", roomname);
        startActivity(intent);
    }

    public void finishFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(EnterChatFragment.this).commit();
        fragmentManager.popBackStack();
    }

    @Override
    public void onBackPressed() {
        finishFragment();
    }
}