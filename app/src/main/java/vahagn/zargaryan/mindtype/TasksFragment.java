package vahagn.zargaryan.mindtype;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

public class TasksFragment extends Fragment {

    private RecyclerView rvTasks;
    private DailyTasksAdapter adapter;
    private TextView tvQuoteText, tvQuoteAuthor, tvProgressText, tvTitle;
    private LinearProgressIndicator progressBar;

    // Менеджеры
    private QuoteManager quoteManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Используем твой XML с темной темой и карточками
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();

        adapter.setOnTaskClickListener(task -> {
            TaskType type = TaskType.fromId(task.id);

            if (type.testType != null) {
                // Если задача привязана к тесту — запускаем MainActivity
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("type", type.testType);
                startActivity(intent);
            } else if (type == TaskType.SHARE_QUOTE) {
                // Если это задача на шеринг цитаты
                shareQuoteAction();
            }
        });

        // Загружаем данные
        loadDailyQuote();
        loadDailyTasks();
    }

    private void initViews(View v) {
        rvTasks = v.findViewById(R.id.rvDailyTasks);
        tvQuoteText = v.findViewById(R.id.tvQuoteText);
        tvQuoteAuthor = v.findViewById(R.id.tvQuoteAuthor);
        tvProgressText = v.findViewById(R.id.tvDailyProgress);
        tvTitle = v.findViewById(R.id.dailyTitle);
        progressBar = v.findViewById(R.id.dailyProgressBar);

        quoteManager = new QuoteManager();
    }

    private void setupRecyclerView() {
        adapter = new DailyTasksAdapter();
        rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTasks.setAdapter(adapter);
        // Отключаем вложенный скролл, если весь фрагмент находится в NestedScrollView
        rvTasks.setNestedScrollingEnabled(false);
    }

    private void loadDailyQuote() {
        quoteManager.fetchDailyQuote(new QuoteManager.OnQuoteFetched() {
            @Override
            public void onSuccess(String text, String author) {
                if (isAdded()) { // Проверка, что фрагмент еще прикреплен к активити
                    tvQuoteText.setText("«" + text.trim() + "»");
                    tvQuoteAuthor.setText(author.isEmpty() ? "— Неизвестный" : "— " + author);
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded()) {
                    tvQuoteText.setText("Великие мысли приходят тем, кто их ищет.");
                    tvQuoteAuthor.setText("— MindType");
                }
            }
        });
    }

    private void loadDailyTasks() {
        DailyTasksManager.checkAndResetTasks(tasks -> {
            if (isAdded() && tasks != null) {
                adapter.setTasks(tasks);
                updateProgressUI(tasks);
            }
        });
    }
    private void updateProgressUI(List<DailyTask> tasks) {
        int total = tasks.size();
        int completed = 0;

        for (DailyTask task : tasks) {
            if (task.isCompleted) completed++;
        }

        // Обновляем текст (напр. "2 / 3")
        tvProgressText.setText(completed + " / " + total);

        // Рассчитываем процент для ProgressBar
        int progressPercent = (total > 0) ? (completed * 100 / total) : 0;
        progressBar.setProgress(progressPercent);

        // Меняем заголовок, если всё готово
        if (completed == total && total > 0) {
            tvTitle.setText("Все цели достигнуты! ✨");
        } else {
            tvTitle.setText("Ежедневные задачи");
        }
    }

    private void shareQuoteAction() {
        String quote = tvQuoteText.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, quote + " — Найдено в MindType");
        startActivity(Intent.createChooser(intent, "Поделиться"));

        // Засчитываем задачу сразу после вызова интента
        DailyTasksManager.completeTask(TaskType.SHARE_QUOTE.id, (reward, leveled) -> {
            loadDailyTasks(); // Перезагружаем список, чтобы увидеть галочку
        });
    }
}