package com.sososhopping.customer.chat.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sososhopping.customer.R;
import com.sososhopping.customer.chat.Chat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter {

    Context context;
    String storeName;
    String userUid;
    ArrayList<Chat> chatList;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy/MM/dd HH:mm");

    public ChatAdapter(Context context, String storeName, String userUid, ArrayList<Chat> chatList) {
        this.context = context;
        this.storeName = storeName;
        this.userUid = userUid;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        if (viewType == 1)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_chat, parent, false);
        else
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_chat, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        if (chatList.get(position).senderUid.equals(userUid))
            return 1;
        else
            return 2;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (!chatList.get(position).senderUid.equals(userUid)) {
            ((ViewHolder) holder).chatCardViewNickName.setText(storeName);
        } else {
            ((ViewHolder) holder).chatCardViewNickName.setText("나");
        }

        String timeStamp = chatList.get(position).timeStamp.toString();
        Long time = Long.parseLong(timeStamp);
        Date date = new Date(time);

        ((ViewHolder)holder).chatCardViewContent.setText(chatList.get(position).content);
        ((ViewHolder)holder).chatCardViewTime.setText(simpleDateFormat.format(date));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView chatCardViewNickName;
        public TextView chatCardViewTime;
        public TextView chatCardViewContent;
        public CardView chatCardView;

        public ViewHolder(@NonNull View v) {
            super(v);
            this.chatCardViewNickName = v.findViewById(R.id.chatCardViewNickName);
            this.chatCardViewTime = v.findViewById(R.id.chatCardViewTime);
            this.chatCardViewContent = v.findViewById(R.id.chatCardViewContent);
            this.chatCardView = v.findViewById(R.id.chatCardView);
        }
    }

}
