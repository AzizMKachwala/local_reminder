package com.example.localreminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpcomingFragment extends Fragment {

    RecyclerView upcomingListRecyclerView;
    FloatingActionButton btnAdd;
    UpcomingAdapter upcomingAdapter;
    Button btnSave, btnCancel;
    TextView txtDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText etvDateSelect, etvTimeSelect, etvDesc;
    private ArrayList<MyDbDataModel> dataList;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);

        upcomingListRecyclerView = view.findViewById(R.id.upcomingListRecyclerView);
        btnAdd = view.findViewById(R.id.btnAdd);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        MyDataBaseHandler myDatabaseHandler = new MyDataBaseHandler(requireContext());
        dataList = myDatabaseHandler.getAllData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ArrayList<MyDbDataModel> newDataList = myDatabaseHandler.getAllData();
                upcomingAdapter.updateData(newDataList);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "akChannel";
            String desc = "Channel for Alarm Manager";
            int imp = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("androidKnowledge", name, imp);
            channel.setDescription(desc);
            NotificationManager notificationManager = (NotificationManager) requireContext().
                    getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        upcomingAdapter = new UpcomingAdapter(dataList,
                new UpcomingAdapter.OnEditItemClickListener() {
                    @Override
                    public void onEditClick(int position) {
                        Toast.makeText(getContext(), "Edit Clicked", Toast.LENGTH_SHORT).show();
                        MyDbDataModel clickedItem = dataList.get(position);
                        showEditDialog(clickedItem);
                    }
                },
                new UpcomingAdapter.OnDeleteItemClickListener() {
                    @Override
                    public void onDeleteClick(int position) {
                        Toast.makeText(getContext(), "Delete Clicked", Toast.LENGTH_SHORT).show();
                        MyDbDataModel clickedItem = dataList.get(position);
                        showDeleteConfirmationDialog(clickedItem.getId(), position);
                    }
                });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.add_dialog_item, null);
                builder.setView(dialogView);
                builder.setCancelable(false);

                AlertDialog dialog = builder.create();
                dialog.show();

                btnSave = dialogView.findViewById(R.id.btnSave);
                btnCancel = dialogView.findViewById(R.id.btnCancel);
                etvDateSelect = dialogView.findViewById(R.id.etvDateSelect);
                etvTimeSelect = dialogView.findViewById(R.id.etvTimeSelect);
                etvDesc = dialogView.findViewById(R.id.etvDesc);
                txtDialog = dialogView.findViewById(R.id.txtDialog);

                txtDialog.setText("ADD DIALOG...");

                etvDateSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(getContext(), "ETV DATE", Toast.LENGTH_SHORT).show();
                        showDatePickerDialog();
                    }
                });

                etvTimeSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(getContext(), "ETV TIME", Toast.LENGTH_SHORT).show();
                        showTimePickerDialog();
                    }
                });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
                        String date = etvDateSelect.getText().toString();
                        String time = etvTimeSelect.getText().toString();
                        String description = etvDesc.getText().toString();

                        if (!date.isEmpty() && !time.isEmpty() && !description.isEmpty()) {
                            MyDataBaseHandler myDatabaseHandler = new MyDataBaseHandler(requireContext());
                            myDatabaseHandler.insertData(date, time, description);

                            ArrayList<MyDbDataModel> newDataModelArrayList = myDatabaseHandler.getAllData();
                            upcomingAdapter.updateData(newDataModelArrayList);
                            Toast.makeText(getContext(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
                            scheduleAlarm(date, time, description);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
//                        Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        upcomingListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        upcomingListRecyclerView.setAdapter(upcomingAdapter);
        return view;
    }

    private void scheduleAlarm(String date, String time, String desc) {
        // Parse the date and time strings into a Calendar object
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        try {
            Date dateTime = sdf.parse(date + " " + time);
            calendar.setTime(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Create an Intent for the AlarmReceiver
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        intent.putExtra("description", desc);
        intent.putExtra("time", time);
        intent.putExtra("date", date);

        // Create a PendingIntent
        int alarmId = (int) System.currentTimeMillis(); // Unique ID for each alarm
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), alarmId, intent, PendingIntent.FLAG_IMMUTABLE);

        // Get the AlarmManager
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        // Set the alarm
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        Toast.makeText(requireContext(), "Alarm set", Toast.LENGTH_SHORT).show();
    }

    private void showEditDialog(MyDbDataModel clickedItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.add_dialog_item, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave = dialogView.findViewById(R.id.btnSave);
        btnCancel = dialogView.findViewById(R.id.btnCancel);
        etvDateSelect = dialogView.findViewById(R.id.etvDateSelect);
        etvTimeSelect = dialogView.findViewById(R.id.etvTimeSelect);
        etvDesc = dialogView.findViewById(R.id.etvDesc);
        txtDialog = dialogView.findViewById(R.id.txtDialog);

        txtDialog.setText("EDIT DIALOG...");

        etvDateSelect.setText(clickedItem.getDate());
        etvTimeSelect.setText(clickedItem.getTime());
        etvDesc.setText(clickedItem.getDescription());

        etvDateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                        Toast.makeText(getContext(), "ETV DATE", Toast.LENGTH_SHORT).show();
                showDatePickerDialog();
            }
        });

        etvTimeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                        Toast.makeText(getContext(), "ETV TIME", Toast.LENGTH_SHORT).show();
                showTimePickerDialog();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                        Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
                String date = etvDateSelect.getText().toString();
                String time = etvTimeSelect.getText().toString();
                String description = etvDesc.getText().toString();

                if (!date.isEmpty() && !time.isEmpty() && !description.isEmpty()) {
                    MyDataBaseHandler myDatabaseHandler = new MyDataBaseHandler(requireContext());
                    myDatabaseHandler.updateData(clickedItem.getId(), date, time, description);

                    ArrayList<MyDbDataModel> newDataModelArrayList = myDatabaseHandler.getAllData();
                    upcomingAdapter.updateData(newDataModelArrayList);
                    Toast.makeText(getContext(), "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                    scheduleAlarm(date, time, description);
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
//                        Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showDeleteConfirmationDialog(int itemId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Item");
        builder.setMessage("Are you sure you want to delete this Item?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyDataBaseHandler myDataBaseHandler = new MyDataBaseHandler(requireContext());
                myDataBaseHandler.deleteData(itemId);
                dataList.remove(position);
                upcomingAdapter.notifyItemRemoved(position);
                Toast.makeText(getContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showTimePickerDialog() {

        Calendar currentTime = Calendar.getInstance();
//        Toast.makeText(getContext(), currentTime.toString().trim(), Toast.LENGTH_SHORT).show();

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

                        Calendar selectedTimeCalendar = Calendar.getInstance();
                        selectedTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTimeCalendar.set(Calendar.MINUTE, minute);

                        if (selectedTimeCalendar.get(Calendar.DAY_OF_YEAR) == currentTime.get(Calendar.DAY_OF_YEAR)
                                && selectedTimeCalendar.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR)
                                && selectedTimeCalendar.before(currentTime)) {
                            Toast.makeText(requireContext(), "Please select a future time", Toast.LENGTH_SHORT).show();
                        } else {
                            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay % 12, minute);
                            if (hourOfDay >= 12) {
                                etvTimeSelect.setText(selectedTime + " PM");
                            } else {
                                etvTimeSelect.setText(selectedTime + " AM");
                            }
                        }
                    }
                },
                currentTime.get(Calendar.HOUR_OF_DAY),
                currentTime.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.updateTime(currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE));
        timePickerDialog.show();
    }

    private void showDatePickerDialog() {

        Calendar currentDate = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        etvDateSelect.setText(selectedDate);
                    }
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(currentDate.getTimeInMillis());
        datePickerDialog.show();
    }
}