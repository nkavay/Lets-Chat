package LetsChatServer_final;

import java.util.Vector;

public class Room {//대화방의 정보표현 객체

     String room_title;//방제목
     int count;//현재방 인원수
     int limitcount; //채팅방 최대 인원수
     String room_boss;//방장(방 개설자)
     Vector<ChatService> room_userV;//userV: 같은 방에 접속한 Client정보 저장                      

     public Room() {
    	 room_userV = new Vector<>();
     } 
}
