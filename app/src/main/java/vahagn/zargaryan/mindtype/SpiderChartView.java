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

import java.util.LinkedHashMap;
import java.util.Map;

public class SpiderChartView extends View {

    private Paint gridPaint, dataPaint, labelPaint, pointPaint;
    private Map<String, Integer> data = new LinkedHashMap<>(); // Название -> Процент (0-100)

    private float centerX, centerY, radius;
    private float animationProgress = 0f; // Для анимации появления (0.0 -> 1.0)

    public SpiderChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Сетка (Сама паутина)
        gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#44FFFFFF")); // Полупрозрачный белый
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(3f);
        gridPaint.setAntiAlias(true);

        // Область данных (Заливка)
        dataPaint = new Paint();
        dataPaint.setColor(Color.parseColor("#80BB86FC")); // Фиолетовый с прозрачностью 50%
        dataPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        dataPaint.setAntiAlias(true);

        // Точки на вершинах данных
        pointPaint = new Paint();
        pointPaint.setColor(Color.parseColor("#BB86FC"));
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setAntiAlias(true);

        // Текст (Названия шкал)
        labelPaint = new Paint();
        labelPaint.setColor(Color.WHITE);
        labelPaint.setTextSize(36f); // Размер текста
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setAntiAlias(true);
    }

    // Метод для передачи данных из Activity
    public void setData(Map<String, Integer> newData) {
        this.data = newData;
        startAnimation();
    }

    private void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1000); // 1 секунда
        animator.setInterpolator(new DecelerateInterpolator()); // Замедляется к концу
        animator.addUpdateListener(animation -> {
            animationProgress = (float) animation.getAnimatedValue();
            invalidate(); // Перерисовываем View на каждом кадре
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data.isEmpty()) return;

        centerX = getWidth() / 2f;
        centerY = getHeight() / 2f;
        // Оставляем место для текста по краям (отступ 15%)
        radius = Math.min(centerX, centerY) * 0.65f;

        int count = data.size();
        float angleStep = (float) (2 * Math.PI / count);

        // 1. Рисуем 5 уровней "паутины"
        for (int i = 1; i <= 5; i++) {
            float r = radius * (i / 5f);
            drawPolygon(canvas, r, count, angleStep, gridPaint);
        }

        // 2. Рисуем оси от центра к краям и подписи
        int index = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            float angle = index * angleStep - (float) Math.PI / 2;
            float endX = (float) (centerX + radius * Math.cos(angle));
            float endY = (float) (centerY + radius * Math.sin(angle));

            // Линия оси
            canvas.drawLine(centerX, centerY, endX, endY, gridPaint);

            // Подпись (чуть дальше радиуса, чтобы не накладывалась на сетку)
            float labelX = (float) (centerX + (radius + 60) * Math.cos(angle));
            float labelY = (float) (centerY + (radius + 60) * Math.sin(angle));

            // Центруем текст по вертикали
            canvas.drawText(entry.getKey(), labelX, labelY + 12, labelPaint);
            index++;
        }

        // 3. Рисуем результат (закрашенную фигуру)
        Path path = new Path();
        index = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            // Значение от 0 до 100 переводим в радиус, умножаем на прогресс анимации
            float valueRadius = radius * (entry.getValue() / 100f) * animationProgress;
            float angle = index * angleStep - (float) Math.PI / 2;

            float x = (float) (centerX + valueRadius * Math.cos(angle));
            float y = (float) (centerY + valueRadius * Math.sin(angle));

            if (index == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }

            // Рисуем точку на вершине для красоты
            canvas.drawCircle(x, y, 8f, pointPaint);

            index++;
        }
        path.close();
        canvas.drawPath(path, dataPaint);
    }

    private void drawPolygon(Canvas canvas, float r, int count, float angleStep, Paint paint) {
        Path path = new Path();
        for (int i = 0; i < count; i++) {
            // Начинаем сверху: вычитаем 90 градусов (PI/2)
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