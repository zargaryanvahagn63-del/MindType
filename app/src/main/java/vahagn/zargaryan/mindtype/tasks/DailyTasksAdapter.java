package vahagn.zargaryan.mindtype.tasks;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import vahagn.zargaryan.mindtype.*;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import vahagn.zargaryan.mindtype.R;

/**
 * Адаптер для отображения списка заданий в стиле Material 3
 */
class DailyTasksAdapter extends RecyclerView.Adapter<DailyTasksAdapter.TaskViewHolder> {

    private List<DailyTask> tasks = new ArrayList<>();

    public void setData(List<DailyTask> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daily_task, parent, false);
        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        DailyTask task = tasks.get(position);

        holder.tvTitle.setText(task.getTitle());
        holder.tvDesc.setText(task.isLocked() ? task.getLockReason() : task.getDescription());

        // Оформление в зависимости от состояния квеста (Заблокирован / Пройден / Доступен)
        if (task.isLocked()) {
            holder.card.setAlpha(0.4f);
            holder.card.setStrokeColor(Color.parseColor("#2C2C2C"));
            holder.ivStatus.setImageResource(R.drawable.ic_lock); // Замените на иконку замка в drawable
            holder.ivStatus.setImageTintList(ColorStateList.valueOf(Color.parseColor("#888888")));
            holder.tvTitle.setTextColor(Color.parseColor("#888888"));
        } else if (task.isCompleted()) {
            holder.card.setAlpha(0.9f);
            holder.card.setStrokeColor(Color.parseColor("#00E676")); // Зеленая каемка выполненного задания
            holder.card.setStrokeWidth(2);
            holder.ivStatus.setImageResource(R.drawable.ic_check); // Иконка галочки
            holder.ivStatus.setImageTintList(ColorStateList.valueOf(Color.parseColor("#00E676")));
            holder.tvTitle.setTextColor(Color.WHITE);
        } else {
            holder.card.setAlpha(1.0f);
            holder.card.setStrokeColor(Color.parseColor("#BB86FC")); // Фиолетовый акцент готового квеста
            holder.card.setStrokeWidth(3);
            holder.ivStatus.setImageResource(R.drawable.ic_arrow_right); // Иконка "начать"
            holder.ivStatus.setImageTintList(ColorStateList.valueOf(Color.parseColor("#BB86FC")));
            holder.tvTitle.setTextColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        ImageView ivStatus;
        TextView tvTitle, tvDesc;

        public TaskViewHolder(@NonNull View v) {
            super(v);
            card = (MaterialCardView) v;
            ivStatus = v.findViewById(R.id.iv_task_status);
            tvTitle = v.findViewById(R.id.tv_task_title);
            tvDesc = v.findViewById(R.id.tv_task_desc);
        }
    }
}