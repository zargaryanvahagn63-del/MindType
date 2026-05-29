package vahagn.zargaryan.mindtype.tests;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vahagn.zargaryan.mindtype.R;

/**
 * Фрагмент главного экрана.
 * Управляет отображением тестов, их блокировкой в зависимости от прогресса
 * и кулдауном в 24 часа после последнего прохождения.
 */
public class HomeFragment extends Fragment {

    // Элементы интерфейса
    private View cardMBTI, cardEQ, cardBig5, cardVARK, cardDark3;
    private Button btnMBTI, btnEQ, btnBigFive, btnVARK, btnDarkTriad;

    // Firebase
    private DatabaseReference userRef;
    private ValueEventListener syncListener;

    // Таймер для кулдауна
    private CountDownTimer cooldownTimer;
    private static final long COOLDOWN_DURATION =(long) (0.5 * 60L * 60L * 1000L);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(v);

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
            setupProgressSync();
        }
        return v;
    }

    private void initViews(View v) {
        btnMBTI = v.findViewById(R.id.btnMBTI);
        btnEQ = v.findViewById(R.id.btnEQ);
        btnBigFive = v.findViewById(R.id.btnBigFive);
        btnVARK = v.findViewById(R.id.btnVARK);
        btnDarkTriad = v.findViewById(R.id.btnDarkTriad);

        cardMBTI = v.findViewById(R.id.cardMBTI);
        cardEQ = v.findViewById(R.id.cardEQ);
        cardBig5 = v.findViewById(R.id.cardBig5);
        cardVARK = v.findViewById(R.id.cardVARK);
        cardDark3 = v.findViewById(R.id.cardDark3);
    }

    /**
     * Слушает изменения в Firebase: прогресс (step) и время последнего теста.
     */
    private void setupProgressSync() {
        syncListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;

                int step = snapshot.hasChild("currentStep") ? snapshot.child("currentStep").getValue(Integer.class) : 0;
                long lastTestTimestamp = snapshot.hasChild("lastTestTimestamp") ? snapshot.child("lastTestTimestamp").getValue(Long.class) : 0;

                checkCooldownAndRefreshUI(step, lastTestTimestamp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        userRef.addValueEventListener(syncListener);
    }

    /**
     * Проверяет, прошел ли период кулдауна.
     */
    private void checkCooldownAndRefreshUI(int step, long lastTestTimestamp) {
        long timePassed = System.currentTimeMillis() - lastTestTimestamp;

        // Если кулдаун еще идет (и это не первый запуск, где timestamp = 0)
        if (lastTestTimestamp != 0 && timePassed < COOLDOWN_DURATION) {
            startCooldownTimer(COOLDOWN_DURATION - timePassed, step);
        } else {
            // Кулдаун истек или еще не начинался — обновляем доступность
            updateTestsAvailability(step);
        }
    }

    private void startCooldownTimer(long timeLeft, int step) {
        if (cooldownTimer != null) cooldownTimer.cancel();

        cooldownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millis) {
                // Блокируем кнопки и показываем время ожидания
                String msg = "Доступ через " + formatTime(millis);
                disableAllButtons(msg);
            }
            @Override
            public void onFinish() {
                updateTestsAvailability(step);
            }
        }.start();
    }

    /**
     * Логика разблокировки тестов на основе прогресса (step).
     */
    private void updateTestsAvailability(int step) {
        resetUI();

        // Если все тесты пройдены (step >= 5), открываем доступ ко всему
        if (step >= 5) {
            unlockTest(cardMBTI, btnMBTI, TestType.MBTI, true);
            unlockTest(cardEQ, btnEQ, TestType.EQ, true);
            unlockTest(cardBig5, btnBigFive, TestType.BIG5, true);
            unlockTest(cardVARK, btnVARK, TestType.VARK, true);
            unlockTest(cardDark3, btnDarkTriad, TestType.DARK3, true);
            return;
        }

        // Поэтапная разблокировка
        unlockTest(cardMBTI, btnMBTI, TestType.MBTI, step > 0);

        if (step >= 1) unlockTest(cardEQ, btnEQ, TestType.EQ, step > 1);
        else setLockedState(cardEQ, btnEQ, "Сначала MBTI");

        if (step == 2) {
            unlockTest(cardBig5, btnBigFive, TestType.BIG5_PART1, false);
            btnBigFive.setText("Начать (Часть 1)");
        } else if (step == 3) {
            unlockTest(cardBig5, btnBigFive, TestType.BIG5_PART1, true);
            unlockTest(cardVARK, btnVARK, TestType.VARK, false);
        } else if (step == 4) {
            unlockTest(cardBig5, btnBigFive, TestType.BIG5_PART2, false);
            unlockTest(cardVARK, btnVARK, TestType.VARK, true);
        } else {
            setLockedState(cardBig5, btnBigFive, "Сначала EQ");
            setLockedState(cardVARK, btnVARK, "Сначала Big Five");
        }

        if (step >= 5) unlockTest(cardDark3, btnDarkTriad, TestType.DARK3, step > 5);
        else setLockedState(cardDark3, btnDarkTriad, "Сначала Big Five ч.2");
    }

    private void unlockTest(View card, Button btn, TestType type, boolean isPassed) {
        card.setAlpha(1.0f);
        btn.setEnabled(true);
        btn.setAlpha(1.0f);
        btn.setText(isPassed ? "Пройти заново" : "Начать тест");
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("type", type.name());
            startActivity(intent);
        });
    }

    private void setLockedState(View card, Button btn, String message) {
        card.setAlpha(0.3f);
        btn.setEnabled(false);
        btn.setAlpha(0.5f);
        btn.setText(message);
    }

    private void disableAllButtons(String message) {
        View[] cards = {cardMBTI, cardEQ, cardBig5, cardVARK, cardDark3};
        Button[] btns = {btnMBTI, btnEQ, btnBigFive, btnVARK, btnDarkTriad};
        for(View c : cards) if(c != null) c.setAlpha(0.3f);
        for(Button b : btns) if(b != null) {
            b.setEnabled(false);
            b.setText(message);
        }
    }

    private void resetUI() {
        // Убираем блокировку по кулдауну, если она была
        View[] cards = {cardMBTI, cardEQ, cardBig5, cardVARK, cardDark3};
        for(View c : cards) if(c != null) c.setAlpha(1.0f);
    }

    private String formatTime(long millis) {
        long hours = millis / 3600000;
        long minutes = (millis % 3600000) / 60000;
        long seconds = (millis % 60000) / 1000;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cooldownTimer != null) cooldownTimer.cancel();
        if (userRef != null && syncListener != null) userRef.removeEventListener(syncListener);
    }
}