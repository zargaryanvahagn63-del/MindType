package vahagn.zargaryan.mindtype;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.Holder> {

    List<Question> list;
    int[] answers;
    OnAnswer listener;

    public interface OnAnswer {
        void onAnswered(int count);
    }

    public QuestionAdapter(List<Question> list, OnAnswer listener) {
        this.list = list;
        this.listener = listener;
        answers = new int[list.size()];
        Arrays.fill(answers, -1);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder h, int pos) {

        Question q = list.get(pos);

        h.tvQuestion.setText(q.text);

        // ВАЖНО: убрать listener
        h.seekBar.setOnSeekBarChangeListener(null);

        h.seekBar.setMax(4);

        // всегда ставим текущее значение
        if (answers[pos] == -1) {
            answers[pos] = 2; // центр
        }
        h.seekBar.setProgress(answers[pos]);

        h.tvEmoji.setText(getEmoji(answers[pos]));

        h.seekBar.setProgress(answers[pos]);

        h.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int pos = h.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    answers[pos] = progress;
                    notifyAnsweredCount();
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private void notifyAnsweredCount() {
        int count = 0;
        for (int a : answers) {
            if (a != -1) count++;
        }
        listener.onAnswered(count);
    }

    private String getEmoji(int p) {
        String[] emojis = {"😡","🙁","😐","😊","😁"};
        if (p < 0 || p >= emojis.length) return "🙂";
        return emojis[p];
    }

    public void clearAnswers() {
        Arrays.fill(answers, 3);
        notifyDataSetChanged();
    }

    static class Holder extends RecyclerView.ViewHolder {

        TextView tvQuestion, tvEmoji;
        SeekBar seekBar;

        public Holder(View v) {
            super(v);
            tvQuestion = v.findViewById(R.id.tvQuestion);
            tvEmoji = v.findViewById(R.id.tvEmoji);
            seekBar = v.findViewById(R.id.seekBar);
        }
    }
}