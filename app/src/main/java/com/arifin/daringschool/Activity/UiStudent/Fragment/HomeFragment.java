package com.arifin.daringschool.Activity.UiStudent.Fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arifin.daringschool.Activity.UiStudent.Activity.EBookActivityStudent;
import com.arifin.daringschool.Activity.UiStudent.Activity.ExaminationActivity;
import com.arifin.daringschool.Activity.UiStudent.Activity.GradeActivity;
import com.arifin.daringschool.Activity.UiStudent.Activity.HistoryAbsenActivity;
import com.arifin.daringschool.Activity.UiStudent.Adapter.ViewScheduleAdpater;
import com.arifin.daringschool.Activity.UiTeacher.Activity.ScheduleTeacherActivity;
import com.arifin.daringschool.Activity.UiTeacher.Adapter.ScheduleHomeTeacherAdapter;
import com.arifin.daringschool.Activity.UiTeacher.Model.Assignment;
import com.arifin.daringschool.Model.Course;
import com.arifin.daringschool.R;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    @BindView(R.id.compactcalendar_view)
    CompactCalendarView mCompactCalendarView;
    @BindView(R.id.txt_month)
    TextView tvMonth;
    @BindView(R.id.txt_year)
    TextView tvYear;
    @BindView(R.id.tv_date_now)
    TextView tvDateNow;
    @BindView(R.id.img_arrow_left)
    ImageView imgArrowLeft;
    @BindView(R.id.img_arrow_right)
    ImageView imgArrowRight;
    @BindView(R.id.img_fingerprint)
    CardView imgFingerprint;
    @BindView(R.id.img_attendance_history)
    CardView imgAttendanceHistory;
    @BindView(R.id.cv_eBook)
    CardView cvEBook;
    @BindView(R.id.cv_schedule)
    CardView cvSchedule;
    @BindView(R.id.cv_examination)
    CardView cvExam;
    @BindView(R.id.cv_grade)
    CardView cvGrade;
    @BindView(R.id.img_profile_dashboard)
    CircleImageView imgProfile;
    @BindView(R.id.tv_code_profile)
    TextView tvNameDashboard;
    @BindView(R.id.tv_semester_profile)
    TextView tvSemester;
    @BindView(R.id.tv_class_profile)
    TextView tvClassProfile;
    @BindView(R.id.rv_schedule)
    RecyclerView rvSchedule;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy", Locale.getDefault());
    SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd", Locale.getDefault());
    SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMMM", Locale.getDefault());
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    int mYear, mMonth, mDay;
    int mHour, mMinute;
    TextView urlProfile;

    DatabaseReference studentRef;
    DatabaseReference studentRefDashboard;
    private ScheduleHomeTeacherAdapter assigmentAdapter;
    private List<Assignment> assignmentList;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        mAuth = FirebaseAuth.getInstance();


        tvMonth.setText(simpleDateFormat.format(mCompactCalendarView.getFirstDayOfCurrentMonth()));
        tvYear.setText(simpleDateFormat2.format(mCompactCalendarView.getFirstDayOfCurrentMonth()));


        studentRef = FirebaseDatabase.getInstance().getReference("login/Fahmi Wijaya").child("Absen");

        studentRefDashboard = FirebaseDatabase.getInstance().getReference("login/Fahmi Wijaya").child("Profile");

        studentRefDashboard.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Picasso.get().load(ds.child("imgProfile").getValue().toString()).into(imgProfile);
                    tvNameDashboard.setText(ds.child("nameProfile").getValue().toString());
                    tvSemester.setText(ds.child("semester").getValue().toString());
                    tvClassProfile.setText(ds.child("class").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        assignmentList = new ArrayList<>();

        rvSchedule.setHasFixedSize(true);
        rvSchedule.setLayoutManager(new LinearLayoutManager(getActivity()));

        assigmentAdapter = new ScheduleHomeTeacherAdapter( getActivity(), assignmentList);
        rvSchedule.setAdapter(assigmentAdapter);



        mDatabaseRef = FirebaseDatabase.getInstance().getReference("login/Rahman").child("Assignment");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                assignmentList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Assignment upload = postSnapshot.getValue(Assignment.class);
                    upload.setKey(postSnapshot.getKey());
                    assignmentList.add(upload);
                }

                assigmentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        imgArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCompactCalendarView.scrollRight();
            }
        });
        imgArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCompactCalendarView.scrollLeft();
            }
        });

        mCompactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                tvDateNow.setText(dateFormatDay.format(dateClicked) + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + dateFormatForMonth.format(dateClicked) + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + simpleDateFormat2.format(dateClicked));
                tvDateNow.setVisibility(View.VISIBLE);

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

            }
        });

        cvEBook.setOnClickListener(v -> {
            Intent i = new Intent(HomeFragment.this.getActivity(), EBookActivityStudent.class);
            startActivity(i);
        });

        cvSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeFragment.this.getActivity(), ScheduleTeacherActivity.class);
                startActivity(i);
            }
        });

        cvExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeFragment.this.getActivity(), ExaminationActivity.class);
                startActivity(i);
            }
        });

        cvGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeFragment.this.getActivity(), GradeActivity.class);
                startActivity(i);
            }
        });

        imgAttendanceHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeFragment.this.getActivity(), HistoryAbsenActivity.class);
                startActivity(i);
            }
        });

        imgFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(HomeFragment.this.getActivity(), R.style.AppTheme_Dialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_absen_student);
                dialog.setCanceledOnTouchOutside(true);
                Button etClockIn =  dialog.findViewById(R.id.clockIn);
                Button etDateIn =  dialog.findViewById(R.id.dateIn);
                Spinner spKeterangan =  dialog.findViewById(R.id.spKeterangan);
                Spinner spKelas =  dialog.findViewById(R.id.spKelas);
                Button btnCancel =  dialog.findViewById(R.id.btnCancel);
                Button btnYes =  dialog.findViewById(R.id.btnYes);



                etClockIn.setOnTouchListener((view, motionEvent) -> {
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        // Get Current Time
                        final Calendar c = Calendar.getInstance();
                        mHour = c.get(Calendar.HOUR_OF_DAY);
                        mMinute = c.get(Calendar.MINUTE);

                        // Launch Time Picker Dialog
                        timePickerDialog = new TimePickerDialog(HomeFragment.this.getContext(),
                                (view12, hourOfDay, minute) -> {
                                    etClockIn.setText(hourOfDay + ":" + minute);
                                    timePickerDialog.dismiss();
                                }, mHour, mMinute, false);
                        timePickerDialog.show();

                    }
                    return true;
                });

                etDateIn.setOnTouchListener((view, motionEvent) -> {
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        final Calendar c = Calendar.getInstance();
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);
                        datePickerDialog = new DatePickerDialog(HomeFragment.this.getContext(),
                                (view1, year, monthOfYear, dayOfMonth) -> {
                                    etDateIn.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                    datePickerDialog.dismiss();
                                }, mYear, mMonth, mDay);
                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                        datePickerDialog.show();
                    }
                    return true;
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        HashMap<String, String> userMap = new HashMap<>();

                        String keterangan =  spKeterangan.getSelectedItem().toString();
                        String kelas =  spKelas.getSelectedItem().toString();
                        String tanggal =  etDateIn.getText().toString();
                        String jam =  etClockIn.getText().toString();

                        userMap.put("absenPadaJam", jam);
                        userMap.put("tanggal", tanggal);
                        userMap.put("keterangan", keterangan);
                        userMap.put("kelas", kelas);

                        studentRef.push().setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                Toast.makeText(getActivity(), "Data Inseret!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }
                });

                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            }

        });

        return view;
    }

}