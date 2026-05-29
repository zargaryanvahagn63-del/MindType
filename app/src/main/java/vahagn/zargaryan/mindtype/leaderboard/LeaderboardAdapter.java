package vahagn.zargaryan.mindtype.leaderboard;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import vahagn.zargaryan.mindtype.R;
import vahagn.zargaryan.mindtype.User;

/**
 * Адаптер для отображения списка лидеров (Leaderboard).
 * Отвечает за визуальное выделение текущего пользователя в общем списке.
 */
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private final List<User> data; // Список пользователей для отображения

    public LeaderboardAdapter(List<User> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создание элемента списка из разметки item_leaderboard
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = data.get(position);

        // Получаем ID текущего авторизованного пользователя
        String currentUid = FirebaseAuth.getInstance().getUid();

        // Если ID пользователя в списке совпадает с текущим пользователем — подсвечиваем его
        if (user.uid != null && user.uid.equals(currentUid)) {
            holder.itemView.setBackgroundColor(Color.parseColor("#2C2C2C")); // Светлый фон для акцента
            holder.name.setTextColor(Color.parseColor("#BB86FC")); // Фиолетовое имя
        } else {
            // Стандартное оформление для остальных участников
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.name.setTextColor(Color.WHITE);
        }

        // Установка данных в текстовые поля
        holder.tvRank.setText(String.valueOf(position + 1)); // Номер места
        holder.name.setText(user.username);                  // Имя
        holder.xp.setText(user.xp + " XP");                // Количество опыта
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * ViewHolder для элементов списка лидеров.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, name, xp;

        ViewHolder(View v) {
            super(v);
            tvRank = v.findViewById(R.id.tv_rank);
            name = v.findViewById(R.id.tv_username);
            xp = v.findViewById(R.id.tv_xp);
        }
    }
}