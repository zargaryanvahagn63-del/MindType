package vahagn.zargaryan.mindtype.tests;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;

import vahagn.zargaryan.mindtype.R;

/**
 * Адаптер для отображения списка вопросов в RecyclerView.
 * Управляет вводом пользователя через SeekBar и визуальной обратной связью (смайлики, подписи).
 */
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.Holder> {

    // Текстовые подписи уровней согласия
    private static final String[] GRADES = {"Совсем нет", "Скорее нет", "Нейтрально", "Скорее да", "Полностью!"};
    // Эмодзи, соответствующие уровням прогресса
    private static final String[] EMOJIS = {"😡", "😟", "😐", "🙂", "😁"};

    private final List<Question> questions; // Список объектов вопросов
    protected final int[] answers;          // Массив для хранения выбранных значений (0-4)
    private final OnAnswer listener;        // Слушатель для уведомления об изменении прогресса

    /**
     * Интерфейс для отслеживания количества отвеченных вопросов.
     */
    public interface OnAnswer {
        void onAnswered(int count);
    }

    public QuestionAdapter(List<Question> questions, OnAnswer listener) {
        this.questions = questions;
        this.listener = listener;
        this.answers = new int[questions.size()];
        // Инициализируем массив значением -1 (вопрос не отвечен)
        Arrays.fill(answers, -1);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создание новой карточки вопроса из XML-разметки
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Question question = questions.get(position);
        holder.tvQuestion.setText(question.getText());

        // Подсветка карточки, если на этот вопрос пользователь забыл ответить
        if (question.showHighlight) {
            holder.setCardBackgroundColor(Color.parseColor("#3D1D1D")); // Тёмно-красный фон
        } else {
            holder.setCardBackgroundColor(Color.parseColor("#1A1A1A")); // Стандартный фон
        }

        // Восстановление состояния из сохраненных ответов
        int savedProgress = answers[position];
        // Если ответа еще нет, визуально ставим ползунок в нейтральное положение (2)
        int displayProgress = (savedProgress == -1) ? 2 : savedProgress;

        // Отключаем слушатель перед программной установкой прогресса, чтобы избежать зацикливания
        holder.seekBar.setOnSeekBarChangeListener(null);

        holder.tvAnswerGrade.setText(GRADES[displayProgress]);
        holder.tvEmoji.setText(EMOJIS[displayProgress]);
        holder.seekBar.setProgress(displayProgress);

        // Установка слушателя для обработки действий пользователя
        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Обновляем текстовую и визуальную индикацию в реальном времени
                    holder.tvAnswerGrade.setText(GRADES[progress]);
                    holder.tvEmoji.setText(EMOJIS[progress]);

                    int currentPos = holder.getBindingAdapterPosition();
                    if (currentPos == RecyclerView.NO_POSITION) return;

                    // Сохраняем ответ
                    answers[currentPos] = progress;
                    question.selectedValue = progress;
                    question.isTouched = true;

                    // Снимаем подсветку ошибки при взаимодействии
                    question.showHighlight = false;
                    holder.setCardBackgroundColor(Color.parseColor("#1A1A1A"));

                    // Уведомляем активность об изменении общего прогресса теста
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

    /**
     * Считает количество вопросов с установленными ответами.
     */
    public int countAnswered() {
        int count = 0;
        for (int a : answers) {
            if (a != -1) count++;
        }
        return count;
    }

    /**
     * Сброс всех ответов и обновление интерфейса.
     */
    public void clearAnswers() {
        Arrays.fill(answers, -1);
        for (Question q : questions) {
            q.isTouched = false;
            q.selectedValue = -1;
            q.showHighlight = false;
        }
        notifyDataSetChanged();
        if (listener != null) listener.onAnswered(0);
    }

    /**
     * Вспомогательный ViewHolder для хранения ссылок на View-компоненты элемента списка.
     */
    static class Holder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvEmoji, tvAnswerGrade;
        SeekBar seekBar;
        androidx.cardview.widget.CardView cardView;

        public Holder(View v) {
            super(v);
            tvQuestion = v.findViewById(R.id.tvQuestion);
            tvEmoji = v.findViewById(R.id.tvEmoji);
            tvAnswerGrade = v.findViewById(R.id.tvAnswerGrade);
            seekBar = v.findViewById(R.id.seekBar);
            // Корневой элемент разметки item_question - это CardView
            cardView = (androidx.cardview.widget.CardView) v;
        }

        public void setCardBackgroundColor(int color) {
            cardView.setCardBackgroundColor(color);
        }
    }
}
