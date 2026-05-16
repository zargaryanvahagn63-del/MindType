package vahagn.zargaryan.mindtype;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvRank, tvXp, tvTestsCount, tvMbtiCode, tvMbtiName, tvMbtiDesc;
    private ProgressBar xpProgressBar;
    private View avatarCircle;
    private MaterialButton btnLogout;

    private MBTIAnalyzer mbtiAnalyzer;
    private DatabaseReference userRef;
    private ValueEventListener userValueListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle b) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mbtiAnalyzer = new MBTIAnalyzer();
        initViews(view);
        setupClickListeners();
        loadUserData();

        return view;
    }

    private void initViews(View v) {
        tvUsername = v.findViewById(R.id.tv_username);
        tvRank = v.findViewById(R.id.tv_rank);
        tvXp = v.findViewById(R.id.tv_xp);
        tvTestsCount = v.findViewById(R.id.tv_tests_count);
        tvMbtiCode = v.findViewById(R.id.tv_mbti_code);
        tvMbtiName = v.findViewById(R.id.tv_mbti_name);
        tvMbtiDesc = v.findViewById(R.id.tv_mbti_desc);
        xpProgressBar = v.findViewById(R.id.xpProgressBar);
        avatarCircle = v.findViewById(R.id.avatarCircle);
        btnLogout = v.findViewById(R.id.btn_logout);
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> {
            if (getActivity() != null) {
                FirebaseAuth.getInstance().signOut();
                // Если у тебя LoginActivity называется иначе, поменяй здесь
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void loadUserData() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // ЕСЛИ НОВЫЙ АККАУНТ И ДАННЫХ НЕТ
                    User emptyUser = new User();
                    emptyUser.username = "Новый пользователь";
                    emptyUser.xp = 0;
                    emptyUser.testsCount = 0;
                    emptyUser.mbtiType = "";
                    updateUI(emptyUser);
                    return;
                }

                User user = snapshot.getValue(User.class);
                if (user != null) {
                    updateUI(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        userRef.addValueEventListener(userValueListener);
    }

    private void updateUI(User user) {
        // Защита от null
        if (user.username != null && !user.username.isEmpty()) {
            tvUsername.setText(user.username);
        } else {
            tvUsername.setText("Пользователь");
        }

        tvTestsCount.setText("Тестов: " + user.testsCount);
        tvXp.setText(user.xp + " XP");
        tvRank.setText(calculateRank(user.xp));

        xpProgressBar.setMax(1000);
        xpProgressBar.setProgress(user.xp);

        if (user.mbtiType != null && !user.mbtiType.isEmpty()) {
            tvMbtiCode.setText(user.mbtiType);
            tvMbtiName.setText(mbtiAnalyzer.getTypeName(user.mbtiType));
            tvMbtiDesc.setText(mbtiAnalyzer.getDetailedSummary(user.mbtiType));
        } else {
            tvMbtiCode.setText("????");
            tvMbtiName.setText("Тест не пройден");
            tvMbtiDesc.setText("Пройдите тест, чтобы узнать свой тип личности.");
        }
    }

    private String calculateRank(int xp) {
        if (xp < 100) return "Новичок";
        if (xp < 300) return "Исследователь";
        if (xp < 600) return "Аналитик";
        if (xp < 1000) return "Мастер разума";
        return "Гуру";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userRef != null && userValueListener != null) {
            userRef.removeEventListener(userValueListener);
        }
    }
}