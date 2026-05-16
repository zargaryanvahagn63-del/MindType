package vahagn.zargaryan.mindtype;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    // Объявляем переменные здесь, чтобы к ним был доступ из всех функций
    private View cardMBTI, cardEQ, cardVARK, cardDark3, cardBig5;
    private View btnMBTI, btnEQ, btnVARK, btnDarkTriad, btnBigFive;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Инициализируем все вьюхи
        initViews(view);

        // 2. Запускаем анимацию
        startEntranceAnimation();

        // 3. ЗАГРУЖАЕМ ДАННЫЕ ИЗ FIREBASE (Этот метод мы добавим ниже)
        loadUserData();

        // Слушатели кликов
        btnBigFive.setOnClickListener(v -> open("BIG5"));
        btnMBTI.setOnClickListener(v -> open("MBTI"));
        btnEQ.setOnClickListener(v -> open("EQ"));
        btnDarkTriad.setOnClickListener(v -> open("DARK3"));
        btnVARK.setOnClickListener(v -> open("VARK"));
    }

    private void initViews(View view) {
        btnBigFive = view.findViewById(R.id.btnBigFive);
        btnMBTI = view.findViewById(R.id.btnMBTI);
        btnEQ = view.findViewById(R.id.btnEQ);
        btnDarkTriad = view.findViewById(R.id.btnDarkTriad);
        btnVARK = view.findViewById(R.id.btnVARK);

        cardMBTI = view.findViewById(R.id.cardMBTI);
        cardEQ = view.findViewById(R.id.cardEQ);
        cardDark3 = view.findViewById(R.id.cardDark3);
        cardBig5 = view.findViewById(R.id.cardBig5);
        cardVARK = view.findViewById(R.id.cardVARK);
    }

    // --- НОВЫЙ МЕТОД: ПОДКЛЮЧАЕМСЯ К БАЗЕ ---
    private void loadUserData() {
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        com.google.firebase.database.FirebaseDatabase.getInstance().getReference("users")
                .child(uid)
                .addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null && isAdded()) {

                            // ЛОГИКА БЛОКИРОВКИ
                            boolean mbtiFinished = user.mbtiDone;

                            // Разрешаем или запрещаем клики
                            btnEQ.setEnabled(mbtiFinished);
                            btnVARK.setEnabled(mbtiFinished);
                            btnBigFive.setEnabled(mbtiFinished);
                            btnDarkTriad.setEnabled(mbtiFinished);

                            // Визуально «тушим» заблокированные карточки
                            float opacity = mbtiFinished ? 1.0f : 0.4f;
                            cardEQ.setAlpha(opacity);
                            cardVARK.setAlpha(opacity);
                            cardBig5.setAlpha(opacity);
                            cardDark3.setAlpha(opacity);

                            // Меняем текст кнопок (опционально, для стиля)
                            if (!mbtiFinished) {
                                if (btnEQ instanceof android.widget.Button) {
                                    ((android.widget.Button) btnEQ).setText("Сначала MBTI");
                                    ((android.widget.Button) btnVARK).setText("Сначала MBTI");
                                    ((android.widget.Button) btnBigFive).setText("Сначала MBTI");
                                    ((android.widget.Button) btnDarkTriad).setText("Сначала MBTI");
                                }
                            } else {
                                if (btnEQ instanceof android.widget.Button) {
                                    ((android.widget.Button) btnEQ).setText("Начать");
                                    ((android.widget.Button) btnVARK).setText("Начать");
                                    ((android.widget.Button) btnBigFive).setText("Начать");
                                    ((android.widget.Button) btnDarkTriad).setText("Начать");
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {}
                });
    }

    private void startEntranceAnimation() {
        View[] cards = {cardMBTI, cardEQ, cardVARK, cardDark3, cardBig5};
        for (View v : cards) {
            v.setAlpha(0f);
            v.setScaleX(0.8f);
            v.setScaleY(0.8f);
        }

        long delay = 200;
        for (View v : cards) {
            v.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(600)
                    .setStartDelay(delay)
                    .setInterpolator(new android.view.animation.OvershootInterpolator())
                    .start();
            delay += 150;
        }
    }

    void open(String type) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("type", type);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.stay_still);
        }
    }
}