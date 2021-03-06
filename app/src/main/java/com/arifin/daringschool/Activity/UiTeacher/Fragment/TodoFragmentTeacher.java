package com.arifin.daringschool.Activity.UiTeacher.Fragment;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.arifin.daringschool.Activity.UiStudent.Fragment.TodoFragment;
import com.arifin.daringschool.Activity.UiTeacher.Adapter.TaskAdapterTeacher;
import com.arifin.daringschool.Activity.UiStudent.Adapter.CreateTaskBottomSheetFragmentStudent;
import com.arifin.daringschool.Activity.UiTeacher.Adapter.CreateTaskBottomSheetFragmentTeacher;
import com.arifin.daringschool.CalendarBottomSheetFragment.ShowCalenderViewBottomSheet;
import com.arifin.daringschool.CalendarBottomSheetFragment.AlarmBroadcastReceiver;
import com.arifin.daringschool.Database.AppDatabase;
import com.arifin.daringschool.Database.DatabaseClient;
import com.arifin.daringschool.Model.Task;
import com.arifin.daringschool.R;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TodoFragmentTeacher extends Fragment implements CreateTaskBottomSheetFragmentTeacher.setRefreshListener {

    @BindView(R.id.taskRecycler)
    RecyclerView taskRecycler;
    @BindView(R.id.addTask)
    TextView addTask;
    TaskAdapterTeacher taskAdapterTeacher;
    List<Task> tasks = new ArrayList<>();
    @BindView(R.id.noDataImage)
    ImageView noDataImage;
    @BindView(R.id.calendar)
    ImageView calendar;
    private TodoFragment fragment;
    private AppDatabase appDatabase;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view1 = inflater.inflate(R.layout.fragment_todo_teacher, container, false);
        ButterKnife.bind(this, view1);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ComponentName receiver = new ComponentName(getActivity(), AlarmBroadcastReceiver.class);
        PackageManager pm = getActivity().getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        Glide.with(getActivity().getApplicationContext()).load(R.drawable.bgnotodo).into(noDataImage);

        addTask.setOnClickListener(view -> {
            CreateTaskBottomSheetFragmentTeacher createTaskBottomSheetFragmentTeacher = new CreateTaskBottomSheetFragmentTeacher();
            createTaskBottomSheetFragmentTeacher.setTaskId(0, false, this, TodoFragmentTeacher.this);
            createTaskBottomSheetFragmentTeacher.show(getActivity().getSupportFragmentManager(), createTaskBottomSheetFragmentTeacher.getTag());
        });

        getSavedTasks();

        calendar.setOnClickListener(view -> {
            ShowCalenderViewBottomSheet showCalendarViewBottomSheet = new ShowCalenderViewBottomSheet();
            showCalendarViewBottomSheet.show(getActivity().getSupportFragmentManager(), showCalendarViewBottomSheet.getTag());
        });

        return view1;
    }
    public void setUpAdapter() {
        taskAdapterTeacher = new TaskAdapterTeacher(this, tasks, this);
        taskRecycler.setLayoutManager(new LinearLayoutManager( this.getActivity()));
        taskRecycler.setAdapter(taskAdapterTeacher);
        taskAdapterTeacher.notifyDataSetChanged();
    }

    private void getSavedTasks() {
        ExecutorService executors = Executors.newSingleThreadExecutor();

        executors.execute(new Runnable() {
            @Override
            public void run() {

            }
        });


        class GetSavedTasks extends AsyncTask<Void, Void, List<Task>> {
            @Override
            protected List<Task> doInBackground(Void... voids) {
                tasks = DatabaseClient
                        .getInstance(getActivity().getApplicationContext())
                        .getAppDatabase()
                        .dataBaseAction()
                        .getAllTasksList();
                return tasks;
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                noDataImage.setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);
                setUpAdapter();
            }
        }

        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute();
    }

    @Override
    public void refresh() {
        getSavedTasks();
    }
}