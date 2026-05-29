package vahagn.zargaryan.mindtype.result;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vahagn.zargaryan.mindtype.R;

/**
 * Адаптер для отображения архива пройденных тестов в профиле.
 * Реализует отображение названия теста и его краткого RPG-результата (Headline).
 * Оптимизирован для работы с вертикальными цепочками (Chains) в ConstraintLayout.
 */
public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultViewHolder> {

    private List<TestResult> results = new ArrayList<>();
    private OnResultClickListener listener;

    /**
     * Интерфейс для обработки нажатий на карточку результата.
     */
    public interface OnResultClickListener {
        void onResultClick(TestResult result);
    }

    /**
     * Загружает данные в список и обновляет интерфейс.
     * @param data Список объектов TestResult.
     * @param listener Слушатель для обработки кликов.
     */
    public void setData(List<TestResult> data, OnResultClickListener listener) {
        this.results = data;
        this.listener = listener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создание View на основе нашего исправленного XML с вертикальным стеком
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result_card, parent, false);
        return new ResultViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        TestResult res = results.get(position);

        // Установка названия теста (например, "Эмоциональный интеллект")
        holder.tvTitle.setText(res.testName);

        // ОБРАБОТКА RPG-СТАТА (Headline)
        // Если результат существует, показываем его (например, "EQ: 75%")
        if (res.resultHeadline != null && !res.resultHeadline.trim().isEmpty()) {
            holder.tvHeadline.setText(res.resultHeadline);
            holder.tvHeadline.setVisibility(View.VISIBLE);
        } else {
            // Если данных нет (старые тесты), скрываем View.
            // Благодаря этому заголовок tvTitle автоматически центрируется по вертикали.
            holder.tvHeadline.setVisibility(View.GONE);
        }

        // Обработка клика по карточке
        holder.itemView.setOnClickListener(v -> {
            // Используем getBindingAdapterPosition для получения актуального индекса
            int currentPos = holder.getBindingAdapterPosition();
            if (listener != null && currentPos != RecyclerView.NO_POSITION) {
                listener.onResultClick(results.get(currentPos));
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    /**
     * Оптимизированный ViewHolder для хранения ссылок на элементы карточки.
     */
    static class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvHeadline;

        public ResultViewHolder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvResultTitle);
            tvHeadline = v.findViewById(R.id.tvResultHeadline);
        }
    }
}