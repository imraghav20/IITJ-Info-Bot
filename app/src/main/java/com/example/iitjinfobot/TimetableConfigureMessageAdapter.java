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

public class TimetableConfigureMessageAdapter extends RecyclerView.Adapter<TimetableConfigureMessageAdapter.ViewHolder>{

    LayoutInflater inflater;
    List<Option> optionsList;
    String userId;
    String selectedSlot;

    public TimetableConfigureMessageAdapter(Context ctx, List<Option> optionsList, String userId, String selectedSlot) {
        this.inflater = LayoutInflater.from(ctx);
        this.optionsList = optionsList;
        this.userId = userId;
        this.selectedSlot = selectedSlot;
    }

    @NonNull
    @Override
    public TimetableConfigureMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.options_button, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableConfigureMessageAdapter.ViewHolder holder, int position) {
        String selected = optionsList.get(position).getOption_value();
        holder.option.setText(selected);

        holder.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ttConfigRef, timetableRef;
                ttConfigRef = FirebaseDatabase.getInstance().getReference().child("Configure Timetable").child(userId);
                timetableRef = FirebaseDatabase.getInstance().getReference().child("User Timetables").child(userId);

                final String messageKey = ttConfigRef.push().getKey();
                final String type = "sent";

                Calendar calForDate = Calendar.getInstance();
                SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd yyyy");
                String currentDate = currentDateFormat.format(calForDate.getTime());

                Calendar calForTime = Calendar.getInstance();
                SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                String currentTime = currentTimeFormat.format(calForTime.getTime());

                HashMap<String, Object> messageMap = new HashMap<>();
                messageMap.put("id", messageKey);
                messageMap.put("message", selected);
                messageMap.put("type", type);
                messageMap.put("date", currentDate);
                messageMap.put("time", currentTime);
                messageMap.put("hasOptions", false);

                if(! selectedSlot.isEmpty()){
                    timetableRef.child(selectedSlot).setValue(selected);
                }

                ttConfigRef.child(messageKey).updateChildren(messageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        final String messageKey = ttConfigRef.push().getKey();
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


                        if(selected.startsWith("Slot")){
                            messageMap.put("message", "Select your course");

                            DatabaseReference slotsRef = FirebaseDatabase.getInstance().getReference().child("Information Database").child("Timetable Slots").child(selected);
                            slotsRef.orderByChild("Order").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
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

                                    ttConfigRef.child(messageKey).updateChildren(messageMap);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else{
                            messageMap.put("message", "Select your slot");

                            DatabaseReference slotsRef = FirebaseDatabase.getInstance().getReference().child("Information Database").child("Timetable Slots");
                            slotsRef.orderByChild("Order").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
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

                                    ttConfigRef.child(messageKey).updateChildren(messageMap);
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