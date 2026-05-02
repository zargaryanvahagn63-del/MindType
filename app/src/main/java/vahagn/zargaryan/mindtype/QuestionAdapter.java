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

    // Выносим константы, чтобы не мусорить в памяти
    private static final String[] GRADES = {"Совсем нет", "Скорее нет", "Нейтрально", "Скорее да", "Полностью!"};
    private static final String[] EMOJIS = {"😡", "😟", "😐", "🙂", "😁"};

    private final List<Question> questions;
    protected final int[] answers;
    private final OnAnswer listener;

    public interface OnAnswer {
        void onAnswered(int count);
    }

    public QuestionAdapter(List<Question> questions, OnAnswer listener) {
        this.questions = questions;
        this.listener = listener;
        this.answers = new int[questions.size()];
        Arrays.fill(answers, -1);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Question question = questions.get(position);
        holder.tvQuestion.setText(question.getText());

        int savedProgress = answers[position];
        // Если ответ не давался (-1), ставим нейтралочку (2)
        int displayProgress = (savedProgress == -1) ? 2 : savedProgress;

        // Важно: обнуляем слушатель перед установкой прогресса, чтобы не триггерить лишний раз
        holder.seekBar.setOnSeekBarChangeListener(null);

        holder.tvAnswerGrade.setText(GRADES[displayProgress]);
        holder.tvEmoji.setText(EMOJIS[displayProgress]);
        holder.seekBar.setProgress(displayProgress);

        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    holder.tvAnswerGrade.setText(GRADES[progress]);
                    holder.tvEmoji.setText(EMOJIS[progress]);

                    answers[holder.getBindingAdapterPosition()] = progress;

                    // Уведомляем листенер
                    if (listener != null) {
                        listener.onAnswered(countAnswered());
                    }
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

    // Тот самый метод, который ты забыл добавить
    public int countAnswered() {
        int count = 0;
        for (int a : answers) {
            if (a != -1) count++;
        }
        return count;
    }

    public void clearAnswers() {
        Arrays.fill(answers, -1);
        notifyDataSetChanged();
        if (listener != null) listener.onAnswered(0);
    }

    public int getScoreForQuestion(int index) {
        if (index >= 0 && index < answers.length) {
            return answers[index] == -1 ? 2 : answers[index];
        }
        return 2;
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvEmoji, tvAnswerGrade; // Добавил tvAnswerGrade
        SeekBar seekBar;

        public Holder(View v) {
            super(v);
            tvQuestion = v.findViewById(R.id.tvQuestion);
            tvEmoji = v.findViewById(R.id.tvEmoji);
            tvAnswerGrade = v.findViewById(R.id.tvAnswerGrade); // Инициализация
            seekBar = v.findViewById(R.id.seekBar);
        }
    }
}