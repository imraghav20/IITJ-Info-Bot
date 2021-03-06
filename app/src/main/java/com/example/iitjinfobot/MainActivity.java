package com.example.iitjinfobot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MainActivity extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;

    private DatabaseReference usersRef;
    private DatabaseReference messageRef;
    private String userId = "";
    private List<String> commands = new ArrayList<>();

    private RecyclerView chat_view;

    private FirebaseRecyclerOptions options;
    FirebaseRecyclerAdapter<Message, MessageViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        chat_view = findViewById(R.id.chat_recycler_view);
        chat_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            userId = personId;

            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("userName", personName);
            profileMap.put("userGivenName", personGivenName);
            profileMap.put("userFamilyName", personFamilyName);
            profileMap.put("userEmail", personEmail);
            profileMap.put("userID", personId);

            usersRef.child(personId).updateChildren(profileMap);

            messageRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(userId);

            final String messageKey = messageRef.push().getKey();
            final String type = "received";
            final String messageText = "Hello " + personGivenName + "! I am your IITJ Assistant. How can I help you?\nI can help you with: ";

            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd yyyy");
            String currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            String currentTime = currentTimeFormat.format(calForTime.getTime());

            List<String> options = new ArrayList<String>();
            options.add("Mess Menu");
            options.add("Bus Schedule");
            options.add("Timetable");
            options.add("Gymkhana Details");
            options.add("Faculty Details");

            HashMap<String, Object> messageMap = new HashMap<>();
            messageMap.put("id", messageKey);
            messageMap.put("message", messageText);
            messageMap.put("type", type);
            messageMap.put("date", currentDate);
            messageMap.put("time", currentTime);
            messageMap.put("hasOptions", true);
            messageMap.put("options", options);

            messageRef.child(messageKey).updateChildren(messageMap);

        } else {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        }

        messageRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(userId);

        options = new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(messageRef, Message.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i, @NonNull Message message) {
                final String msgId = getRef(i).getKey();

                messageRef.child(msgId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String msg = "";
                        Boolean hasOptions = (Boolean) snapshot.child("hasOptions").getValue();
                        if(snapshot.hasChild("message")){
                            msg = snapshot.child("message").getValue().toString();
                        }
                        else{
                            msg = "We received some issue. Please try again.";
                            commands = new ArrayList<>();
                            hasOptions = false;
                        }
                        String date = snapshot.child("date").getValue().toString();
                        String time = snapshot.child("time").getValue().toString();
                        String type = snapshot.child("type").getValue().toString();

                        if (hasOptions) {
                            List<String> options = (List<String>) snapshot.child("options").getValue();
                            List<Option> optionList = new ArrayList<Option>();
                            for (int i = 0; i < options.size(); i++) {
                                Option option = new Option();
                                option.setOption_value(options.get(i));
                                optionList.add(option);
                            }
                            messageViewHolder.optionsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            OptionsAdapter optionsAdapter = new OptionsAdapter(getApplication(), optionList, userId, commands);
                            messageViewHolder.optionsRecyclerView.setAdapter(optionsAdapter);
                        } else {
                            if (type.equals("received")) {
                                commands = new ArrayList<>();
                            }
                        }

                        if (type.equals("sent")) {
                            String cmd = msg.replaceAll("/", "");
                            String[] cmds = cmd.split("-");
                            cmd = cmds[cmds.length - 1];
                            commands.add(cmd);

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

        chat_view.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseRecyclerAdapter.startListening();

        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                chat_view.smoothScrollToPosition(firebaseRecyclerAdapter.getItemCount() - 1);
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.side_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.configure_timetable) {
            Intent configureTimetableIntent = new Intent(MainActivity.this, ConfigureTimetableActivity.class);
            configureTimetableIntent.putExtra("userId", userId);
            startActivity(configureTimetableIntent);
        }
        if (item.getItemId() == R.id.dev_info) {
            Intent developerIntent = new Intent(MainActivity.this, DeveloperInfoActivity.class);
            startActivity(developerIntent);
        }
        if (item.getItemId() == R.id.signout) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(loginIntent);
                                finish();
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        return true;
    }
}