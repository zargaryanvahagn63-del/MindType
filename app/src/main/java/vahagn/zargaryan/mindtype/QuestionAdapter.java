package vahagn.zargaryan.mindtype;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.VH> {

    public int[] answers;
    private final List<Question> list;
    private final OnAnswerListener listener;

    public interface OnAnswerListener {
        void onChanged(int answered);
    }

    public QuestionAdapter(List<Question> list, OnAnswerListener l) {
        this.list = list;
        this.listener = l;
        answers = new int[list.size()];
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        Question q = list.get(i);
        h.text.setText(q.text);

        // Reset listener to avoid triggering it when binding
        h.seek.setOnSeekBarChangeListener(null);
        // Restore progress if already set
        h.seek.setProgress(answers[i] > 0 ? answers[i] - 1 : 0);

        h.seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar s, int v, boolean b) {
                if (b) { // only if changed by user
                    answers[h.getAdapterPosition()] = v + 1;
                    listener.onChanged(countAnswered());
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar s) {}
            @Override
            public void onStopTrackingTouch(SeekBar s) {}
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private int countAnswered() {
        int c = 0;
        for (int a : answers) if (a != 0) c++;
        return c;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView text;
        SeekBar seek;

        VH(View v) {
            super(v);
            text = v.findViewById(R.id.qText);
            seek = v.findViewById(R.id.qSeek);
        }
    }
}
