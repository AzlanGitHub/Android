package net.protodo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTask;
    private Button buttonAdd;
    private ListView listViewTasks;
    private ArrayList<Task> taskList;
    private TaskAdapter taskAdapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        editTextTask = findViewById(R.id.editTextTask);
        buttonAdd = findViewById(R.id.buttonAdd);
        listViewTasks = findViewById(R.id.listViewTasks);

        taskList = dbHelper.getAllTasks();
        taskAdapter = new TaskAdapter(this, taskList);
        listViewTasks.setAdapter(taskAdapter);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskText = editTextTask.getText().toString();
                if (!taskText.isEmpty()) {
                    Task task = new Task(taskText);
                    dbHelper.addTask(task);
                    taskList.add(task);
                    taskAdapter.notifyDataSetChanged();
                    editTextTask.setText("");
                }
            }
        });
    }

    private class TaskAdapter extends ArrayAdapter<Task> {
        private final ArrayList<Task> tasks;

        public TaskAdapter(MainActivity context, ArrayList<Task> tasks) {
            super(context, 0, tasks);
            this.tasks = tasks;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_list_item, parent, false);
            }

            TextView textViewTask = convertView.findViewById(R.id.textViewTask);
            Button buttonEdit = convertView.findViewById(R.id.buttonEdit);
            Button buttonDelete = convertView.findViewById(R.id.buttonDelete);

            final Task task = tasks.get(position);
            textViewTask.setText(task.getTaskText());

            buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditTaskDialog(task, position);
                }
            });

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbHelper.deleteTask(task.getId());
                    tasks.remove(position);
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }

        private void showEditTaskDialog(final Task task, final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Aufgabe bearbeiten");

            final EditText input = new EditText(MainActivity.this);
            input.setText(task.getTaskText());
            builder.setView(input);

            builder.setPositiveButton("Speichern", (dialog, which) -> {
                String newTaskText = input.getText().toString();
                dbHelper.updateTask(task.getId(), newTaskText);
                task.setTaskText(newTaskText);
                notifyDataSetChanged();
            });

            builder.setNegativeButton("Abbrechen", (dialog, which) -> dialog.cancel());

            builder.show();
        }
    }
}
