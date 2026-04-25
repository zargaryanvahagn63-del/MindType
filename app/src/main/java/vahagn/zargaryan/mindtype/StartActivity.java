package vahagn.zargaryan.mindtype;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_start);

        findViewById(R.id.btnBigFive).setOnClickListener(v -> open("BIG5"));
        findViewById(R.id.btnMBTI).setOnClickListener(v -> open("MBTI"));
        findViewById(R.id.btnFun).setOnClickListener(v -> open("FUN"));
        findViewById(R.id.btnDarkTriad).setOnClickListener(v -> open("DARK3"));
    }

    void open(String type) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("type", type);
        startActivity(i);
    }
}