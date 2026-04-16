package vahagn.zargaryan.mindtype;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    TextView tvType;
    ProgressBar pbE;
    Button btnRetry;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_result);

        tvType = findViewById(R.id.tvType);
        pbE = findViewById(R.id.pbE);
        btnRetry = findViewById(R.id.btnRetry);

        int e = getIntent().getIntExtra("E", 0);

        pbE.setProgress(e);

        String type;
        if (e > 60) type = "Экстраверт";
        else if (e < 40) type = "Интроверт";
        else type = "Амбиверт";

        tvType.setText(type);

        btnRetry.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}