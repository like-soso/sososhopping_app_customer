package com.sososhopping.customer.chat.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.R;
import com.sososhopping.customer.chat.ChatroomInfor;
import com.sososhopping.customer.chat.view.adapter.ChatroomAdapter;
import com.sososhopping.customer.databinding.ChatListBinding;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    public static ChatFragment newInstance() {return new ChatFragment();}
    ChatListBinding binding;

    //for firebase
    private String userUid;
    private DatabaseReference ref;
    private static final String CHATROOMINFOR = "ChatroomInfor";

    //for view
    ArrayList<ChatroomInfor> chatroomInforList;
    private RecyclerView chatRoomRecyclerView;
    private ChatroomAdapter adapter;
    private RecyclerView.LayoutManager chatRoomLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //메뉴 변경 확인
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_top_none, menu);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        //binding 설정
        binding = ChatListBinding.inflate(inflater, container, false);

        chatroomInforList = new ArrayList<>();
        chatRoomLayoutManager = new LinearLayoutManager(getContext());
        adapter = new ChatroomAdapter(getContext(), chatroomInforList);

        chatRoomRecyclerView = binding.chatRoomRecyclerView;
        chatRoomRecyclerView.setLayoutManager(chatRoomLayoutManager);
        chatRoomRecyclerView.scrollToPosition(0);
        chatRoomRecyclerView.setAdapter(adapter);

        if (((HomeActivity) getActivity()).authResultTask.isSuccessful() == true && ((HomeActivity) getActivity()).user != null) {
            Log.d("authResultTask", "authResultTask.isSuccessful() = true");
            setChatroomList();
        } else {
            ((HomeActivity) getActivity()).authResultTask.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Log.d("authResultTask", "authResultTask.isSuccessful() = false");
                    setChatroomList();
                }
            });
        }

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        //상단바
        ((HomeActivity)getActivity()).showTopAppBar();
        ((HomeActivity)getActivity()).setTopAppBarHome("채팅");

        //하단바
        ((HomeActivity)getActivity()).showBottomNavigation();
        super.onResume();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setChatroomList() {
        userUid = ((HomeActivity) getActivity()).user.getUid();
        ref = ((HomeActivity) getActivity()).ref;

        ref.child(CHATROOMINFOR).child(userUid)
                .orderByChild("lastMessageTimestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chatroomInforList.clear();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            ChatroomInfor chatroomInfor = data.getValue(ChatroomInfor.class);
                            chatroomInforList.add(0, chatroomInfor);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
