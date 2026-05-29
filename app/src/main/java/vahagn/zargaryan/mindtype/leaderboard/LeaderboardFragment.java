package vahagn.zargaryan.mindtype.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vahagn.zargaryan.mindtype.R;
import vahagn.zargaryan.mindtype.User;

/**
 * Фрагмент экрана таблицы лидеров.
 * Отображает список пользователей, отсортированный по количеству набранного опыта (XP).
 */
public class LeaderboardFragment extends Fragment {

    private RecyclerView recyclerView; // Список для отображения лидеров
    private List<User> userList = new ArrayList<>(); // Список объектов пользователей
    private LeaderboardAdapter adapter; // Адаптер для управления элементами списка

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Инфлейтим (разворачиваем) XML разметку фрагмента
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        // Инициализация RecyclerView
        recyclerView = view.findViewById(R.id.leaderboard_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Создание и установка адаптера
        adapter = new LeaderboardAdapter(userList);
        recyclerView.setAdapter(adapter);

        // Загрузка данных из Firebase
        loadLeaderboard();

        return view;
    }

    /**
     * Загружает данные лидеров через LeaderboardManager.
     */
    private void loadLeaderboard() {
        new LeaderboardManager().fetchTopUsers(new LeaderboardManager.LeaderboardCallback() {
            @Override
            public void onDataLoaded(List<User> list) {
                // Если фрагмент не прикреплен к активности, ничего не делаем
                if (getActivity() == null) return;

                // Обновление UI должно происходить в основном потоке
                getActivity().runOnUiThread(() -> {
                    userList.clear();
                    userList.addAll(list);
                    // Уведомляем адаптер об изменении данных
                    adapter.notifyDataSetChanged();

                    if (userList.isEmpty()) {
                        Toast.makeText(getContext(), "Лидерборд пока пуст", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Ошибка загрузки лидеров: " + error, Toast.LENGTH_LONG).show()
                );
            }
        });
    }
}