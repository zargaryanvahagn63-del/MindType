package vahagn.zargaryan.mindtype;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView logo = findViewById(R.id.logo);
        TextView text = findViewById(R.id.appName);

        logo.animate()
                .alpha(1f)
                .setDuration(1000)
                .start();

        text.animate()
                .alpha(1f)
                .setStartDelay(500)
                .setDuration(1000)
                .start();

        logo.setScaleX(0.5f);
        logo.setScaleY(0.5f);

        logo.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(1000)
                .start();

// переход
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, StartActivity.class));
            finish();
        }, 2000);
    }
}