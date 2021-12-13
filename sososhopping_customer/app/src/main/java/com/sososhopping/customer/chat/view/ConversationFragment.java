package com.sososhopping.customer.chat.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.chat.Chat;
import com.sososhopping.customer.chat.view.adapter.ChatAdapter;
import com.sososhopping.customer.databinding.ConversationBinding;

import java.util.ArrayList;

public class ConversationFragment extends Fragment {
    ConversationBinding binding;

    private static final String CHATROOMID = "chatroomId";
    private static final String STORENAME = "storeName";
    private static final String CHATROOMMESSAGESPATH = "ChatroomMessages";
    private static final String CHATROOMINFOR = "ChatroomInfor";

    private String storeId;
    private String chatroomId;
    private String storeName;
    private String ownerUid;
    private String userUid;

    private RecyclerView chatRecyclerView;
    private ChatAdapter adapter;
    private RecyclerView.LayoutManager chatLayoutManager;
    private EditText chatroomInputEditText;
    private Button chatroomInputSendButton;

    ArrayList<Chat> chatList = new ArrayList<>();

    private DatabaseReference ref;

    public ConversationFragment() {
    }

    public static ConversationFragment newInstance(String chatroomId, String storeName) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle args = new Bundle();
        args.putString(CHATROOMID, chatroomId);
        args.putString(STORENAME, storeName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chatroomId = ConversationFragmentArgs.fromBundle(getArguments()).getChatroomId();
            storeName = ConversationFragmentArgs.fromBundle(getArguments()).getStoreName();
        }

        String[] split = chatroomId.split("@");

        storeId = split[0];
        ownerUid = split[1];
        userUid = split[2];

        ref = ((HomeActivity)getActivity()).ref;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ConversationBinding.inflate(inflater, container, false);

        chatroomInputEditText = binding.chatroomInputEditText;
        chatroomInputSendButton = binding.chatroomInputSendButton;

        chatLayoutManager = new LinearLayoutManager(getContext());
        adapter = new ChatAdapter(getContext(), storeName, userUid, chatList);
        setChatList();

        chatRecyclerView = binding.chatroom;
        chatRecyclerView.setLayoutManager(chatLayoutManager);
        chatRecyclerView.scrollToPosition(chatList.size() - 1);
        chatRecyclerView.setAdapter(adapter);

        chatroomInputSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chatroomInputEditText.getText().toString().equals("")) {
                    String content = chatroomInputEditText.getText().toString();
                    Chat chat = new Chat(userUid, "text", content, null);

                    //메시지 추가
                    ref.child(CHATROOMMESSAGESPATH).child(chatroomId).push().setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            chatroomInputEditText.setText("");

                            //점포 측 채팅방에 메시지 최신화
                            ref.child(CHATROOMINFOR).child(storeId).child(chatroomId).child("lastMessage").setValue(content);
                            ref.child(CHATROOMINFOR).child(storeId).child(chatroomId).child("lastMessageTimestamp").setValue(ServerValue.TIMESTAMP);

                            //자신 채팅방에 메시지 최신화
                            ref.child(CHATROOMINFOR).child(userUid).child(chatroomId).child("lastMessage").setValue(content);
                            ref.child(CHATROOMINFOR).child(userUid).child(chatroomId).child("lastMessageTimestamp").setValue(ServerValue.TIMESTAMP);
                        }
                    });
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        //상단바
        ((HomeActivity)getActivity()).showTopAppBar();
        ((HomeActivity)getActivity()).setTopAppBarHome(storeName);
        ((HomeActivity)getActivity()).setTopAppBarNotHome(true);

        //하단바 숨기기
        ((HomeActivity)getActivity()).hideBottomNavigation();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ref.child(CHATROOMINFOR).child(userUid).child(chatroomId).child("leaveChatroomTimestamp").setValue(ServerValue.TIMESTAMP);
        binding = null;
    }

    private void setChatList() {
        ref.child(CHATROOMMESSAGESPATH).child(chatroomId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Chat chat = snapshot.getValue(Chat.class);
                chatList.add(chat);
                adapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(chatList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}