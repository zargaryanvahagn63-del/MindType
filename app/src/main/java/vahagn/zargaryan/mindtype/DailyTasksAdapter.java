package vahagn.zargaryan.mindtype;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DailyTasksAdapter extends RecyclerView.Adapter<DailyTasksAdapter.TaskViewHolder> {

    private List<DailyTask> tasks = new ArrayList<>();

    // 1. ОПРЕДЕЛЯЕМ ИНТЕРФЕЙС
    public interface OnTaskClickListener {
        void onTaskClick(DailyTask task);
    }

    private OnTaskClickListener listener;

    // 2. МЕТОД ДЛЯ УСТАНОВКИ СЛУШАТЕЛЯ (теперь ошибка в фрагменте исчезнет)
    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<DailyTask> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        DailyTask task = tasks.get(position);

        // Получаем данные о стиле из нашего Enum
        TaskType typeInfo = TaskType.fromId(task.id);

        holder.tvTitle.setText(task.title);
        holder.tvReward.setText("+" + task.xpReward + " XP");
        holder.checkBox.setChecked(task.isCompleted);

        // Устанавливаем цвет награды и индикатора из Enum
        int accentColor = android.graphics.Color.parseColor(typeInfo.colorHex);
        holder.tvReward.setTextColor(accentColor);
        // Если у тебя есть вьюшка-индикатор слева:
        // holder.viewIndicator.setBackgroundColor(accentColor);

        if (task.isCompleted) {
            holder.itemView.setAlpha(0.4f);
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.itemView.setAlpha(1.0f);
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));

            // 3. ВЕШАЕМ КЛИК (только если задача не выполнена)
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onTaskClick(task);
            });
        }

        holder.checkBox.setClickable(false);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvReward;
        CheckBox checkBox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvReward = itemView.findViewById(R.id.tvTaskReward);
            checkBox = itemView.findViewById(R.id.taskCheckbox);
        }
    }
}