package com.example.iitjinfobot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ConfigureTimetableActivity extends AppCompatActivity {

    private String userId;
    private DatabaseReference ttConfigRef;
    private String selectedSlot = "";

    private RecyclerView configTtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_timetable);

        userId = getIntent().getExtras().getString("userId");

        ttConfigRef = FirebaseDatabase.getInstance().getReference().child("Configure Timetable").child(userId);

        configTtView = findViewById(R.id.config_tt_recycler_view);
        configTtView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        final String messageKey = ttConfigRef.push().getKey();
        final String type = "received";
        final String messageText = "Select your slot";

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd yyyy");
        String currentDate = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        String currentTime = currentTimeFormat.format(calForTime.getTime());

        List<String> options = new ArrayList<String>();
        options.add("Slot A");
        options.add("Slot B");
        options.add("Slot C");
        options.add("Slot D");
        options.add("Slot E");
        options.add("Slot F");
        options.add("Slot G");
        options.add("Slot H");
        options.add("Slot I");
        options.add("Slot J");

        HashMap<String, Object> messageMap = new HashMap<>();
        messageMap.put("id", messageKey);
        messageMap.put("message", messageText);
        messageMap.put("type", type);
        messageMap.put("date", currentDate);
        messageMap.put("time", currentTime);
        messageMap.put("hasOptions", true);
        messageMap.put("options", options);

        ttConfigRef.child(messageKey).updateChildren(messageMap);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(ttConfigRef, Message.class)
                .build();

        FirebaseRecyclerAdapter<Message, MessageViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i, @NonNull Message message) {
                final String msgId = getRef(i).getKey();

                ttConfigRef.child(msgId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String msg = snapshot.child("message").getValue().toString();
                        String date = snapshot.child("date").getValue().toString();
                        String time = snapshot.child("time").getValue().toString();
                        String type = snapshot.child("type").getValue().toString();
                        Boolean hasOptions = (Boolean) snapshot.child("hasOptions").getValue();

                        if(msg.equals("Select your slot")){
                            selectedSlot = "";
                        }

                        if (hasOptions) {
                            List<String> options = (List<String>) snapshot.child("options").getValue();
                            List<Option> optionList = new ArrayList<Option>();
                            for (int i = 0; i < options.size(); i++) {
                                Option option = new Option();
                                option.setOption_value(options.get(i));
                                optionList.add(option);
                            }
                            messageViewHolder.optionsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            TimetableConfigureMessageAdapter timetableConfigureMessageAdapter = new TimetableConfigureMessageAdapter(getApplication(), optionList, userId, selectedSlot);
                            messageViewHolder.optionsRecyclerView.setAdapter(timetableConfigureMessageAdapter);
                        }

                        if (type.equals("sent")) {
                            if(msg.startsWith("Slot")){
                                selectedSlot = msg;
                            }

                            messageViewHolder.messageLayout.setGravity(Gravity.RIGHT);
                            messageViewHolder.botImg.setVisibility(View.INVISIBLE);
                            messageViewHolder.messageBox.setBackgroundResource(R.drawable.user_text);
                            messageViewHolder.message.setTextColor(Color.parseColor("#ffffff"));
                            messageViewHolder.date.setTextColor(Color.parseColor("#ffffff"));
                            messageViewHolder.time.setTextColor(Color.parseColor("#ffffff"));
                        }

                        messageViewHolder.message.setText(msg);
                        messageViewHolder.date.setText(date);
                        messageViewHolder.time.setText(time);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);
                MessageViewHolder viewHolder = new MessageViewHolder(view);
                return viewHolder;
            }
        };

        configTtView.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView message, date, time;
        ImageView botImg;
        LinearLayout messageLayout, messageBox;
        RecyclerView optionsRecyclerView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            this.setIsRecyclable(false);

            message = itemView.findViewById(R.id.chat_message);
            date = itemView.findViewById(R.id.chat_date);
            time = itemView.findViewById(R.id.chat_time);
            botImg = itemView.findViewById(R.id.robotImage);
            messageLayout = itemView.findViewById(R.id.message_layout);
            messageBox = itemView.findViewById(R.id.message_box);
            optionsRecyclerView = itemView.findViewById(R.id.options_recycler_view);
        }
    }
}