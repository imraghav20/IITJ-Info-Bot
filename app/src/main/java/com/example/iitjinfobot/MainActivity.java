package com.example.iitjinfobot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;

    private DatabaseReference usersRef;
    private DatabaseReference messageRef;
    private String userId = "";

    //    private TextView helloUser;
//    private LinearLayout chat_layout;
    private EditText message;
    private ImageButton send;
    private RecyclerView chat_view;

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

//        helloUser = findViewById(R.id.user_hello);
//        chat_layout = findViewById(R.id.chat_layout);
        chat_view = findViewById(R.id.chat_recycler_view);
        chat_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        message = findViewById(R.id.message);
        send = findViewById(R.id.send_button);

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

        } else {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        }

        messageRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(userId);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String messageText = message.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    Toast.makeText(MainActivity.this, "Please enter a message first.", Toast.LENGTH_SHORT).show();
                } else {
                    final String messageKey = messageRef.push().getKey();
                    final String type = "sent";

                    Calendar calForDate = Calendar.getInstance();
                    SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd yyyy");
                    String currentDate = currentDateFormat.format(calForDate.getTime());

                    Calendar calForTime = Calendar.getInstance();
                    SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                    String currentTime = currentTimeFormat.format(calForTime.getTime());

                    HashMap<String, Object> messageMap = new HashMap<>();
                    messageMap.put("id", messageKey);
                    messageMap.put("message", messageText);
                    messageMap.put("type", type);
                    messageMap.put("date", currentDate);
                    messageMap.put("time", currentTime);

                    messageRef.child(messageKey).updateChildren(messageMap);
                    message.setText("");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(messageRef, Message.class)
                .build();

        FirebaseRecyclerAdapter<Message, MessageViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i, @NonNull Message message) {
                final String msgId = getRef(i).getKey();

                messageRef.child(msgId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String msg = snapshot.child("message").getValue().toString();
                        String date = snapshot.child("date").getValue().toString();
                        String time = snapshot.child("time").getValue().toString();
                        String type = snapshot.child("type").getValue().toString();

                        if (type.equals("sent")) {
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

        firebaseRecyclerAdapter.startListening();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView message, date, time;
        ImageView botImg;
        LinearLayout messageLayout, messageBox;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.chat_message);
            date = itemView.findViewById(R.id.chat_date);
            time = itemView.findViewById(R.id.chat_time);
            botImg = itemView.findViewById(R.id.robotImage);
            messageLayout = itemView.findViewById(R.id.message_layout);
            messageBox = itemView.findViewById(R.id.message_box);
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