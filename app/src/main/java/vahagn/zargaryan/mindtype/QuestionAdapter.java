package vahagn.zargaryan.mindtype;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.Holder> {

    private List<Question> questions;
    public int[] answers;
    private OnAnswer listener;

    public interface OnAnswer {
        void onAnswered(int count);
    }

    public QuestionAdapter(List<Question> questions, OnAnswer listener) {
        this.questions = questions;
        this.listener = listener;
        answers = new int[questions.size()];
        Arrays.fill(answers, -1); // -1 значит "еще не трогал"
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Question q = questions.get(position);
        holder.tvQuestion.setText(q.getText());

        // Сбрасываем слушатель
        holder.seekBar.setOnSeekBarChangeListener(null);

        int currentProgress = answers[position];
        holder.seekBar.setProgress(currentProgress == -1 ? 2 : currentProgress);

        // Сразу ставим эмодзи
        holder.tvEmoji.setText(getEmoji(holder.seekBar.getProgress()));

        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    answers[position] = progress;
                    holder.tvEmoji.setText(getEmoji(progress));
                    notifyAnsweredCount();
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    private void notifyAnsweredCount() {
        int count = 0;
        for (int a : answers) {
            if (a != -1) count++;
        }
        if (listener != null) {
            listener.onAnswered(count);
        }
    }

    private String getEmoji(int progress) {
        String[] emojis = {"😡", "🙁", "😐", "😊", "😁"};
        if (progress < 0) progress = 0;
        if (progress > 4) progress = 4;
        return emojis[progress];
    }

    public void clearAnswers() {
        Arrays.fill(answers, -1);
        notifyDataSetChanged();
        notifyAnsweredCount(); // Обнуляем счетчик в UI
    }

    public int getScoreForQuestion(int index) {
        if (index >= 0 && index < answers.length) {
            // Если не отвечали, возвращаем нейтральный ответ (2)
            return answers[index] == -1 ? 2 : answers[index];
        }
        return 2;
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