package vahagn.zargaryan.mindtype;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Кастомный View для отображения результатов тестов в виде паутинной диаграммы (Spider Chart).
 * Позволяет визуализировать несколько психологических характеристик на одной круговой шкале.
 */
public class SpiderChartView extends View {

    private Paint gridPaint, dataPaint, labelPaint, pointPaint;
    private Map<String, Integer> data = new LinkedHashMap<>(); // Карта: Название шкалы -> Процент (0-100)

    private float centerX, centerY, radius;
    private float animationProgress = 0f; // Переменная для анимации появления (от 0.0 до 1.0)

    public SpiderChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Инициализация инструментов рисования (Paint).
     */
    private void init() {
        // Настройка кисти для сетки (паутины)
        gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#44FFFFFF")); // Полупрозрачный белый
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(3f);
        gridPaint.setAntiAlias(true);

        // Настройка кисти для области данных (заливка фигуры)
        dataPaint = new Paint();
        dataPaint.setColor(Color.parseColor("#80BB86FC")); // Фиолетовый с прозрачностью 50%
        dataPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        dataPaint.setAntiAlias(true);

        // Настройка кисти для точек на вершинах
        pointPaint = new Paint();
        pointPaint.setColor(Color.parseColor("#BB86FC"));
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setAntiAlias(true);

        // Настройка кисти для текста (названия шкал)
        labelPaint = new Paint();
        labelPaint.setColor(Color.WHITE);
        labelPaint.setTextSize(36f);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setAntiAlias(true);
    }

    /**
     * Устанавливает новые данные для отображения и запускает анимацию.
     * @param newData Карта данных.
     */
    public void setData(Map<String, Integer> newData) {
        this.data = newData;
        startAnimation();
    }

    /**
     * Запускает плавную анимацию отрисовки диаграммы.
     */
    private void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1000); // Длительность 1 секунда
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            animationProgress = (float) animation.getAnimatedValue();
            invalidate(); // Запрос на перерисовку кадра
        });
        animator.start();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (data.isEmpty()) return;

        // Определение центра и базового радиуса диаграммы
        centerX = getWidth() / 2f;
        centerY = getHeight() / 2f;
        radius = Math.min(centerX, centerY) * 0.65f; // Резервируем место под подписи

        int count = data.size();
        float angleStep = (float) (2 * Math.PI / count); // Шаг угла между осями

        // 1. Отрисовка концентрических многоугольников (сетки)
        for (int i = 1; i <= 5; i++) {
            float r = radius * (i / 5f);
            drawPolygon(canvas, r, count, angleStep, gridPaint);
        }

        // 2. Отрисовка осей и подписей шкал
        int index = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            float angle = index * angleStep - (float) Math.PI / 2; // Начинаем рисовать сверху (-90 град)
            float endX = (float) (centerX + radius * Math.cos(angle));
            float endY = (float) (centerY + radius * Math.sin(angle));

            // Рисование линии от центра к краю
            canvas.drawLine(centerX, centerY, endX, endY, gridPaint);

            // Отрисовка текста подписи
            float labelX = (float) (centerX + (radius + 60) * Math.cos(angle));
            float labelY = (float) (centerY + (radius + 60) * Math.sin(angle));
            canvas.drawText(entry.getKey(), labelX, labelY + 12, labelPaint);
            index++;
        }

        // 3. Отрисовка области результатов (полигона данных)
        Path path = new Path();
        index = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            // Расчет радиуса для конкретного значения (с учетом анимации)
            float valueRadius = radius * (entry.getValue() / 100f) * animationProgress;
            float angle = index * angleStep - (float) Math.PI / 2;

            float x = (float) (centerX + valueRadius * Math.cos(angle));
            float y = (float) (centerY + valueRadius * Math.sin(angle));

            if (index == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }

            // Маленькая точка на вершине для акцента
            canvas.drawCircle(x, y, 8f, pointPaint);
            index++;
        }
        path.close();
        // Заливка полученной фигуры
        canvas.drawPath(path, dataPaint);
    }

    /**
     * Вспомогательный метод для рисования правильного многоугольника.
     */
    private void drawPolygon(Canvas canvas, float r, int count, float angleStep, Paint paint) {
        Path path = new Path();
        for (int i = 0; i < count; i++) {
            float angle = i * angleStep - (float) Math.PI / 2;
            float x = (float) (centerX + r * Math.cos(angle));
            float y = (float) (centerY + r * Math.sin(angle));
            if (i == 0) path.moveTo(x, y);
            else path.lineTo(x, y);
        }
        path.close();
        canvas.drawPath(path, paint);
    }
}
