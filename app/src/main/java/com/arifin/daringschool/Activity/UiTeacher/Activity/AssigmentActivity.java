package com.arifin.daringschool.Activity.UiTeacher.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arifin.daringschool.Activity.UiTeacher.Adapter.AssigmentCourseAdapter;
import com.arifin.daringschool.Activity.UiTeacher.Adapter.EBookAdapter;
import com.arifin.daringschool.Activity.UiTeacher.Model.Assignment;
import com.arifin.daringschool.Activity.UiTeacher.Model.EBook;
import com.arifin.daringschool.Model.Course;
import com.arifin.daringschool.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssigmentActivity extends AppCompatActivity {

    @BindView(R.id.action_bar_duty)
    Toolbar actionBar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.fab1)
    FloatingActionButton fab1;
    @BindView(R.id.rv_assigment)
    RecyclerView rvAssigment;
    @BindView(R.id.progress_circle)
    ProgressBar mProgressCircle;

    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;
    private AssigmentCourseAdapter assigmentAdapter;
    private List<Assignment> assignmentList;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duty);
        ButterKnife.bind(this);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_right1);
        getSupportActionBar().setTitle("Tugas");

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                Intent i = new Intent(AssigmentActivity.this, CreateAssignmentActivity.class);
                startActivity(i);
                finish();
            }
        });

        assignmentList = new ArrayList<>();

        rvAssigment.setHasFixedSize(true);
        rvAssigment.setLayoutManager(new LinearLayoutManager(this));

        assigmentAdapter = new AssigmentCourseAdapter( AssigmentActivity.this, assignmentList);
        rvAssigment.setAdapter(assigmentAdapter);



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

                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AssigmentActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });


    }

    private void animateFab() {
        if (isOpen) {
            fab.startAnimation(rotateForward);
            fab1.startAnimation(fabClose);
            fab1.setClickable(false);
            isOpen=false;
        } else {
            fab.startAnimation(rotateForward);
            fab1.startAnimation(fabOpen);
            fab1.setClickable(true);
            isOpen=true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }
}