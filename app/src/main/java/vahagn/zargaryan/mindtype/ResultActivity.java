package vahagn.zargaryan.mindtype;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    TextView tvDesc, tvType;
    Button btnCancel, btnRetry;
    BaseAnalyzer analyzer;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_result);

        tvDesc = findViewById(R.id.tvDesc);
        tvType = findViewById(R.id.tvType);
        btnCancel = findViewById(R.id.btnCancel);
        btnRetry = findViewById(R.id.btnRetry);

        String t = getIntent().getStringExtra("type");
        if (t == null) return;

        TestType type;
        try {
            type = TestType.valueOf(t);
        } catch (Exception e) {
            return;
        }

        switch (type) {
            case MBTI:
                analyzer = new MBTIAnalyzer();
                break;
            case EQ:
                analyzer = new EQAnalyzer();
                break;
            case DARK3:
                analyzer = new DarkTriadAnalyzer();
                break;
            case BIG5:
                analyzer = new PersonalityAnalyzer();
                break;

            default:
                break;
        }

        if (analyzer != null) {
            String resultText = analyzer.getAnalysis(getIntent());
            tvDesc.setText(resultText);
            tvType.setText(analyzer.getTitle(getIntent()));
        }

        btnCancel.setOnClickListener(v -> {
            startActivity(new Intent(this, StartActivity.class));
            finish();
        });

        btnRetry.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("type", type.name());
            startActivity(i);
            finish();
        });
    }
}