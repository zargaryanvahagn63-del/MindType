package vahagn.zargaryan.mindtype.result;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import vahagn.zargaryan.mindtype.R;
import vahagn.zargaryan.mindtype.SpiderChartView;

/**
 * Диалоговое окно для детального просмотра результатов теста.
 * Использует безопасный паттерн передачи данных через Arguments,
 * что предотвращает потерю данных при повороте экрана или смене темы.
 */
public class ResultDetailDialog extends DialogFragment {

    private TestResult result; // Объект с данными результата

    /**
     * Пустой конструктор обязателен для системы Android (для пересоздания фрагмента).
     */
    public ResultDetailDialog() {
        // Оставляем пустым
    }

    /**
     * Статический фабричный метод для создания диалога.
     * Это единственный правильный способ передачи данных во фрагменты.
     */
    public static ResultDetailDialog newInstance(TestResult result) {
        ResultDetailDialog fragment = new ResultDetailDialog();
        Bundle args = new Bundle();
        // Передаем объект как Serializable (не забудь добавить implements Serializable в TestResult)
        args.putSerializable("extra_result", result);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Извлекаем данные из Bundle при создании
        if (getArguments() != null) {
            result = (TestResult) getArguments().getSerializable("extra_result");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Инфлейтим разметку диалога
        View v = inflater.inflate(R.layout.dialog_result_detail, container, false);

        TextView title = v.findViewById(R.id.detailTitle);
        TextView desc = v.findViewById(R.id.detailDesc);
        SpiderChartView chart = v.findViewById(R.id.detailChart);
        Button btnClose = v.findViewById(R.id.btnDetailClose);

        // Наполнение данными
        if (result != null) {
            title.setText(result.testName);
            // Если в истории есть Headline (стат), можно добавить его к заголовку или описанию
            desc.setText(result.analysisText);

            // Визуализация графика, если данные присутствуют
            if (chart != null && result.chartData != null) {
                chart.setData(result.chartData);
            }
        }

        // Закрытие диалога
        btnClose.setOnClickListener(v1 -> dismiss());

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Принудительная настройка геометрии окна диалога
        if (getDialog() != null && getDialog().getWindow() != null) {
            // Растягиваем на всю ширину (с учетом отступов в XML)
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            // Убираем стандартный белый фон окна, чтобы работали наши скругления из фона карточки
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}