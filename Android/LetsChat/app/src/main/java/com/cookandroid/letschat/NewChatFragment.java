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

public class NewChatFragment extends Fragment implements com.cookandroid.letschat.MainActivity.onBackPressedListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_chat, container, false);

        Bundle extra = this.getArguments();
        String nickname = null;

        if(extra != null){
            nickname = extra.getString("nickname");
        }

        ImageView ivNewBack = view.findViewById(R.id.iv_new_back);
        Button btnCreate = view.findViewById(R.id.btn_new_create);
        EditText editRoomname = view.findViewById(R.id.edit_new_roomname);
        EditText editHeadcount = view.findViewById(R.id.edit_new_headcount);

        String finalNickname = nickname;

        // 이전 이미지 클릭 시
        ivNewBack.setOnClickListener(view1 -> {
            finishFragment();
        });

        // 새로운 채팅방 생성
        btnCreate.setOnClickListener(view1 -> {
            String roomname = editRoomname.getText().toString();
            String headcountStr = editHeadcount.getText().toString();
            Integer headcount = Integer.parseInt(headcountStr.isEmpty() ? "0" : headcountStr);

            if(!roomname.isEmpty() && headcount > 1){
                final Boolean[] connectSuccess = {false};
                startChatActivity(finalNickname, roomname, headcount);

            } else if(roomname.isEmpty()){
                Toast.makeText(getActivity(), "채팅방 제목을 입력해주세요", Toast.LENGTH_SHORT).show();
            } else if(headcount < 1){
                Toast.makeText(getActivity(), "1이상의 채팅 인원수를 입력해주세요", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void startChatActivity(String nickname, String roomname, Integer headcount){
        Intent intent = new Intent(getActivity(), com.cookandroid.letschat.ChatActivity.class);
        intent.putExtra("type", 1); // 신규 채팅방: 1, 기존 채팅방: 2
        intent.putExtra("nickname", nickname);
        intent.putExtra("roomname", roomname);
        intent.putExtra("headcount", headcount);
        startActivity(intent);
    }

    public void finishFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(NewChatFragment.this).commit();
        fragmentManager.popBackStack();
    }

    @Override
    public void onBackPressed() {
        finishFragment();
    }
}