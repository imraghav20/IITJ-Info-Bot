package com.example.iitjinfobot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
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

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {
    LayoutInflater inflater;
    List<Option> optionsList;
    String userId;
    List<String> commands;

    public OptionsAdapter(Context ctx, List<Option> optionsList, String userId, List<String> commands) {
        this.inflater = LayoutInflater.from(ctx);
        this.optionsList = optionsList;
        this.userId = userId;
        this.commands = commands;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.options_button, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String selected = optionsList.get(position).getOption_value();
        holder.option.setText(selected);

        holder.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference messageRef;
                messageRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(userId);

                String msg = "/";
                for (int i = 0; i < commands.size(); i++) {
                    msg = msg + commands.get(i) + "-";
                }
                msg = msg + selected.replaceAll("\\s", "");

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
                messageMap.put("message", msg);
                messageMap.put("type", type);
                messageMap.put("date", currentDate);
                messageMap.put("time", currentTime);
                messageMap.put("hasOptions", false);

                String sentMsg = msg;
                messageRef.child(messageKey).updateChildren(messageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DatabaseReference infoRef, timetableRef;
                        infoRef = FirebaseDatabase.getInstance().getReference().child("Information Database");
                        timetableRef = FirebaseDatabase.getInstance().getReference().child("User Timetables");

                        if (sentMsg.startsWith("/Timetable")) {
                            if (sentMsg.equals("/Timetable")) {
                                timetableRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild(userId)) {
                                            String ttMsg = "Which day time table you want to see?";
                                            final String messageKey = messageRef.push().getKey();
                                            final String type = "received";

                                            Calendar calForDate = Calendar.getInstance();
                                            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd yyyy");
                                            String currentDate = currentDateFormat.format(calForDate.getTime());

                                            Calendar calForTime = Calendar.getInstance();
                                            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                                            String currentTime = currentTimeFormat.format(calForTime.getTime());

                                            List<String> options = new ArrayList<String>();
                                            options.add("Monday");
                                            options.add("Tuesday");
                                            options.add("Wednesday");
                                            options.add("Thursday");
                                            options.add("Friday");

                                            HashMap<String, Object> messageMap = new HashMap<>();
                                            messageMap.put("id", messageKey);
                                            messageMap.put("message", ttMsg);
                                            messageMap.put("type", type);
                                            messageMap.put("date", currentDate);
                                            messageMap.put("time", currentTime);
                                            messageMap.put("hasOptions", true);
                                            messageMap.put("options", options);

                                            messageRef.child(messageKey).updateChildren(messageMap);
                                        } else {
                                            String ttMsg = "Looks like you have not configured your timetable. Configure your timetable from top right menu.";
                                            final String messageKey = messageRef.push().getKey();
                                            final String type = "received";

                                            Calendar calForDate = Calendar.getInstance();
                                            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd yyyy");
                                            String currentDate = currentDateFormat.format(calForDate.getTime());

                                            Calendar calForTime = Calendar.getInstance();
                                            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                                            String currentTime = currentTimeFormat.format(calForTime.getTime());

                                            HashMap<String, Object> messageMap = new HashMap<>();
                                            messageMap.put("id", messageKey);
                                            messageMap.put("message", ttMsg);
                                            messageMap.put("type", type);
                                            messageMap.put("date", currentDate);
                                            messageMap.put("time", currentTime);
                                            messageMap.put("hasOptions", false);

                                            messageRef.child(messageKey).updateChildren(messageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    final String messageKey = messageRef.push().getKey();
                                                    final String type = "received";
                                                    final String messageText = "Hello! I am your IITJ Assistant. How can I help you?\nI can help you with: ";

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
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else if (sentMsg.equals("/Timetable-Monday") || sentMsg.equals("/Timetable-Tuesday") || sentMsg.equals("/Timetable-Wednesday") || sentMsg.equals("/Timetable-Thursday") || sentMsg.equals("/Timetable-Friday")) {
                                DatabaseReference finalInfoRef = infoRef;
                                timetableRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Iterable<DataSnapshot> slots = snapshot.getChildren();

                                        for (DataSnapshot dataSnapshot : slots) {
                                            finalInfoRef.child("Timetable Slots").child(dataSnapshot.getKey()).child("Timing").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String ttMsg = "";
                                                    ttMsg = ttMsg + snapshot.getValue() + " ";
                                                    ttMsg = ttMsg + dataSnapshot.getValue();


                                                    final String messageKey = messageRef.push().getKey();
                                                    final String type = "received";

                                                    Calendar calForDate = Calendar.getInstance();
                                                    SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd yyyy");
                                                    String currentDate = currentDateFormat.format(calForDate.getTime());

                                                    Calendar calForTime = Calendar.getInstance();
                                                    SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                                                    String currentTime = currentTimeFormat.format(calForTime.getTime());

                                                    HashMap<String, Object> messageMap = new HashMap<>();
                                                    messageMap.put("id", messageKey);
                                                    messageMap.put("message", ttMsg);
                                                    messageMap.put("type", type);
                                                    messageMap.put("date", currentDate);
                                                    messageMap.put("time", currentTime);
                                                    messageMap.put("hasOptions", false);

                                                    messageRef.child(messageKey).updateChildren(messageMap);

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            else{
                                String ttMsg = "We received some issue. Please try again.";
                                final String messageKey = messageRef.push().getKey();
                                final String type = "received";

                                Calendar calForDate = Calendar.getInstance();
                                SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd yyyy");
                                String currentDate = currentDateFormat.format(calForDate.getTime());

                                Calendar calForTime = Calendar.getInstance();
                                SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                                String currentTime = currentTimeFormat.format(calForTime.getTime());

                                HashMap<String, Object> messageMap = new HashMap<>();
                                messageMap.put("id", messageKey);
                                messageMap.put("message", ttMsg);
                                messageMap.put("type", type);
                                messageMap.put("date", currentDate);
                                messageMap.put("time", currentTime);
                                messageMap.put("hasOptions", false);

                                messageRef.child(messageKey).updateChildren(messageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        final String messageKey = messageRef.push().getKey();
                                        final String type = "received";
                                        final String messageText = "Hello! I am your IITJ Assistant. How can I help you?\nI can help you with: ";

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
                                    }
                                });
                            }
                        } else {
                            for (int i = 0; i < commands.size(); i++) {
                                String retrieveLabel = commands.get(i).replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");
                                infoRef = infoRef.child(retrieveLabel);
                            }

                            final String messageKey = messageRef.push().getKey();
                            final String type = "received";

                            Calendar calForDate = Calendar.getInstance();
                            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd yyyy");
                            String currentDate = currentDateFormat.format(calForDate.getTime());

                            Calendar calForTime = Calendar.getInstance();
                            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                            String currentTime = currentTimeFormat.format(calForTime.getTime());

                            HashMap<String, Object> messageMap = new HashMap<>();
                            messageMap.put("id", messageKey);
                            messageMap.put("type", type);
                            messageMap.put("date", currentDate);
                            messageMap.put("time", currentTime);

                            infoRef.orderByChild("Order").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.hasChild("Message")) {
                                        messageMap.put("message", snapshot.child("Message").getValue().toString());
                                    }
                                    else if (snapshot.hasChild("Value")) {
                                        messageMap.put("message", snapshot.child("Value").getValue().toString());
                                        messageMap.put("hasOptions", false);
                                    }
                                    else{
                                        messageMap.put("message", "We received some issue. Please try again.");
                                        messageMap.put("hasOptions", false);
                                    }

                                    Iterable<DataSnapshot> snapshotChildren = snapshot.getChildren();

                                    List<String> options = new ArrayList<String>();

                                    for (DataSnapshot dataSnapshot : snapshotChildren) {
                                        if (dataSnapshot.hasChildren()) {
                                            options.add(dataSnapshot.getKey());
                                        }
                                    }

                                    if (options.size() > 0) {
                                        messageMap.put("hasOptions", true);
                                        messageMap.put("options", options);
                                    } else {
                                        messageMap.put("hasOptions", false);
                                    }

                                    messageRef.child(messageKey).updateChildren(messageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (! snapshot.hasChild("Message")) {
                                                final String messageKey = messageRef.push().getKey();
                                                final String type = "received";
                                                final String messageText = "Hello! I am your IITJ Assistant. How can I help you?\nI can help you with: ";

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
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return optionsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button option;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.setIsRecyclable(false);

            option = itemView.findViewById(R.id.option);
        }
    }
}
