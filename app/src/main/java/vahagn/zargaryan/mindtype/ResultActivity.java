package vahagn.zargaryan.mindtype;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    TextView tvType, tvDesc;
    ProgressBar pbE;
    Button btnRetry;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_result);

        tvType = findViewById(R.id.tvType);
        tvDesc = findViewById(R.id.tvDesc);
        pbE = findViewById(R.id.pbE);
        btnRetry = findViewById(R.id.btnRetry);

        // получаем все шкалы
        int E = getIntent().getIntExtra("E", 0);
        int A = getIntent().getIntExtra("A", 0);
        int C = getIntent().getIntExtra("C", 0);
        int N = getIntent().getIntExtra("N", 0);
        int O = getIntent().getIntExtra("O", 0);

        pbE.setProgress(E);

        // тип
        String type;
        if (E > 60) type = "Экстраверт";
        else if (E < 40) type = "Интроверт";
        else type = "Амбиверт";

        tvType.setText(type);

        String result = PersonalityAnalyzer.analyze(E, A, C, N, O);
        tvDesc.setText(result);

        btnRetry.setOnClickListener(v -> {
            finish();
        });
    }


}
