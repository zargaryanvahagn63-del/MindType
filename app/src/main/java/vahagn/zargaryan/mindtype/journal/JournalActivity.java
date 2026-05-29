package vahagn.zargaryan.mindtype.journal;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import vahagn.zargaryan.mindtype.R;

public class JournalActivity extends AppCompatActivity {

    private RecyclerView rvJournal;
    private Spinner monthSpinner;
    private DatabaseReference dbRef;
    private List<JournalEntry> allEntries = new ArrayList<>();
    private List<JournalEntry> filteredEntries = new ArrayList<>();
    private JournalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        rvJournal = findViewById(R.id.rvJournal);
        monthSpinner = findViewById(R.id.monthSpinner);

        dbRef = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getUid()).child("journal");

        setupRecyclerView();
        setupMonthSpinner();
        loadEntries();
    }

    private void setupRecyclerView() {
        rvJournal.setLayoutManager(new LinearLayoutManager(this));
        adapter = new JournalAdapter(filteredEntries, entry -> {
            // Вместо запуска новой Activity вызываем BottomSheet
            showDetailSheet(entry);
        });
        rvJournal.setAdapter(adapter);
    }

    private void loadEntries() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allEntries.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    JournalEntry entry = ds.getValue(JournalEntry.class);
                    if (entry != null) {
                        entry.dateKey = ds.getKey();
                        allEntries.add(entry);
                    }
                }
                filterByMonth(monthSpinner.getSelectedItem().toString());
            }
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    private void setupMonthSpinner() {
        String[] months = {"Весь период", "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(spinnerAdapter);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterByMonth(months[position]);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void filterByMonth(String monthName) {
        filteredEntries.clear();
        if (monthName.equals("Весь период")) {
            filteredEntries.addAll(allEntries);
        } else {
            // Маппинг названий месяцев в формат даты "MM"
            String monthIdx = getMonthIndex(monthName);
            for (JournalEntry e : allEntries) {
                if (e.dateKey.contains("-" + monthIdx + "-")) {
                    filteredEntries.add(e);
                }
            }
        }
        Collections.reverse(filteredEntries); // Сначала новые
        adapter.notifyDataSetChanged();
    }

    private String getMonthIndex(String name) {
        String[] m = {"", "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        for(int i=1; i<=12; i++) if(m[i].equals(name)) return String.format("%02d", i);
        return "01";
    }

    /**
     * Показывает диалоговое окно снизу (BottomSheetDialog) с полным текстом выбранной записи рефлексии.
     */
    private void showDetailSheet(JournalEntry entry) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);

        // Раздуваем разметку dialog_journal_detail.xml
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_journal_detail, null);

        // Привязываем элементы интерфейса шторки
        TextView tvDate = sheetView.findViewById(R.id.tvSheetDate);
        TextView tvText = sheetView.findViewById(R.id.tvSheetText);
        Button btnClose = sheetView.findViewById(R.id.btnSheetClose);

        // Заполняем данными
        if (tvDate != null) tvDate.setText(entry.dateKey);
        if (tvText != null) tvText.setText(entry.text);

        // Слушатель для кнопки закрытия
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.setContentView(sheetView);

        // Принудительно раскрываем BottomSheet полностью (фиксим баг обрезания контента при первом открытии)
        dialog.setOnShowListener(dialogInterface -> {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                com.google.android.material.bottomsheet.BottomSheetBehavior.from(bottomSheet)
                        .setState(com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        dialog.show();
    }
}