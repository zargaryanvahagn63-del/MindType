package vahagn.zargaryan.mindtype.journal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vahagn.zargaryan.mindtype.R;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.VH> {
    private List<JournalEntry> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener { void onItemClick(JournalEntry entry); }

    public JournalAdapter(List<JournalEntry> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup p, int t) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_journal_entry, p, false));
    }

    @Override
    public void onBindViewHolder(VH h, int p) {
        JournalEntry e = list.get(p);
        h.date.setText(e.dateKey);
        h.preview.setText(e.text);
        h.mood.setText(String.format("%.1f", e.moodScore));
        // Цвет плашки в зависимости от настроения
        h.mood.getBackground().setTint(e.moodScore > 3.5 ? 0xFFBB86FC : 0xFF757575);
        h.itemView.setOnClickListener(v -> listener.onItemClick(e));
    }

    @Override public int getItemCount() { return list.size(); }

    class VH extends RecyclerView.ViewHolder {
        TextView date, preview, mood;
        VH(View v) {
            super(v);
            date = v.findViewById(R.id.tvDate);
            preview = v.findViewById(R.id.tvPreview);
            mood = v.findViewById(R.id.tvMoodBadge);
        }
    }
}