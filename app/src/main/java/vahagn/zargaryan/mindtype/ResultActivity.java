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
            case FUN:
                showFun();
                tvType.setText("");
                tvDesc.setText("");
                break;
            case MBTI:
                int E = getIntent().getIntExtra("E", 0);
                int S = getIntent().getIntExtra("S", 0);
                int T = getIntent().getIntExtra("T", 0);
                int J = getIntent().getIntExtra("J", 0);

                MBTIResult r = MBTIAnalyzer.analyze(E, S, T, J);

                tvType.setText(r.type);
                tvDesc.setText(r.desc);
                break;
            case BIG5:
                int e = getIntent().getIntExtra("E", 0);
                int a = getIntent().getIntExtra("A", 0);
                int c = getIntent().getIntExtra("C", 0);
                int n = getIntent().getIntExtra("N", 0);
                int o = getIntent().getIntExtra("O", 0);

                tvDesc.setText(PersonalityAnalyzer.analyze(e, a, c, n, o));
                tvType.setText("");
                break;
            case DARK3:
                int N = getIntent().getIntExtra("NARC", 0);
                int M = getIntent().getIntExtra("MACH", 0);
                int P = getIntent().getIntExtra("PSY", 0);

                tvDesc.setText(
                        "Нарциссизм: " + N + "% (" + level(N) + ")\n" +
                                narcText(N) + "\n\n" +

                                "Макиавеллизм: " + M + "% (" + level(M) + ")\n" +
                                machText(M) + "\n\n" +

                                "Психопатия: " + P + "% (" + level(P) + ")\n" +
                                psyText(P)
                );
                tvType.setText("");
                break;
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

    private void showFun() {
        String[] names = getIntent().getStringArrayExtra("names");
        int[] percents = getIntent().getIntArrayExtra("percents");
        int[] images = getIntent().getIntArrayExtra("images");

        LinearLayout container = findViewById(R.id.containerResults);

        for (int i = 0; i < names.length; i++) {
            View item = getLayoutInflater().inflate(R.layout.item_result, container, false);

            ((TextView) item.findViewById(R.id.tvName))
                    .setText(names[i] + " - " + percents[i] + "%");

            ((ProgressBar) item.findViewById(R.id.progressBar))
                    .setProgress(percents[i]);

            ((ImageView) item.findViewById(R.id.imgChar))
                    .setImageResource(images[i]);

            container.addView(item);
        }
    }

    private String level(int v) {
        if (v < 30) return "низкий";
        if (v < 60) return "средний";
        return "высокий";
    }

    private String narcText(int v) {
        if (v > 70) return pick(
                "Ты стремишься к признанию.",
                "Тебе важно внимание.",
                "Ты уверен в себе."
        );
        if (v < 30) return pick(
                "Ты скромен.",
                "Ты не любишь внимание.",
                "Ты сдержан."
        );
        return "Баланс уверенности и скромности.";
    }

    private String machText(int v) {
        if (v > 70) return pick(
                "Ты стратегичен.",
                "Ты умеешь влиять на людей.",
                "Ты действуешь расчетливо."
        );
        if (v < 30) return pick(
                "Ты честен.",
                "Ты открыт.",
                "Ты не используешь других."
        );
        return "Ты гибок в общении.";
    }

    private String psyText(int v) {
        if (v > 70) return pick(
                "Ты импульсивен.",
                "Ты склонен к риску.",
                "Ты хладнокровен."
        );
        if (v < 30) return pick(
                "Ты эмпатичен.",
                "Ты контролируешь себя.",
                "Ты осторожен."
        );
        return "Умеренный уровень самоконтроля.";
    }

    private String pick(String... arr) {
        return arr[(int) (Math.random() * arr.length)];
    }
}