package vahagn.zargaryan.mindtype.tasks.achievents;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import vahagn.zargaryan.mindtype.R;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    private List<Achievement> achievements = new ArrayList<>();

    public void setData(List<Achievement> list) {
        this.achievements = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_achievement, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Achievement item = achievements.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvDesc.setText(item.getDescription());
        holder.tvXp.setText("+" + item.getXpReward() + " XP");

        // Цветовое оформление по типу медали
        int color;
        switch (item.getMedalType()) {
            case "GOLD":
                color = Color.parseColor("#FFD700"); // Золото
                holder.ivMedal.setImageResource(R.drawable.ic_test_result); // Замените на иконку медали
                break;
            case "SILVER":
                color = Color.parseColor("#C0C0C0"); // Серебро
                holder.ivMedal.setImageResource(R.drawable.ic_test_result);
                break;
            default:
                color = Color.parseColor("#CD7F32"); // Бронза
                holder.ivMedal.setImageResource(R.drawable.ic_test_result);
                break;
        }

        holder.ivMedal.setImageTintList(ColorStateList.valueOf(color));

        // Эффект заблокированной/разблокированной ачивки
        if (item.isUnlocked()) {
            holder.card.setAlpha(1.0f);
            holder.card.setStrokeColor(color);
            holder.card.setStrokeWidth(3);
            holder.tvTitle.setTextColor(Color.WHITE);
        } else {
            holder.card.setAlpha(0.4f); // Затемняем заблокированную ачивку
            holder.card.setStrokeColor(Color.parseColor("#2C2C2C"));
            holder.card.setStrokeWidth(1);
            holder.tvTitle.setTextColor(Color.parseColor("#888888"));
        }
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        ImageView ivMedal;
        TextView tvTitle, tvDesc, tvXp;

        public ViewHolder(@NonNull View v) {
            super(v);
            card = (MaterialCardView) v;
            ivMedal = v.findViewById(R.id.iv_achievement_medal);
            tvTitle = v.findViewById(R.id.tv_achievement_title);
            tvDesc = v.findViewById(R.id.tv_achievement_desc);
            tvXp = v.findViewById(R.id.tv_achievement_xp);
        }
    }
}