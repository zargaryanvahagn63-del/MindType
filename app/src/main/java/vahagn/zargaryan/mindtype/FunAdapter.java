package vahagn.zargaryan.mindtype;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FunAdapter extends RecyclerView.Adapter<FunAdapter.Holder> {

    private final List<CharacterQuestion> list;
    public final int[] answers;
    private final OnAnswer listener;
    private final List<List<Choice>> shuffledList;

    public interface OnAnswer {
        void onAnswered(int count);
    }

    public FunAdapter(List<CharacterQuestion> list, OnAnswer l) {
        this.list = list;
        this.listener = l;

        answers = new int[list.size()];
        Arrays.fill(answers, -1);

        shuffledList = new ArrayList<>();

        for (CharacterQuestion q : list) {
            List<Choice> shuffled = new ArrayList<>(q.choices);
            Collections.shuffle(shuffled);
            shuffledList.add(shuffled);
        }
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fun, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder h, int position) {

        CharacterQuestion q = list.get(position);
        h.tvQuestion.setText(q.text);

        h.group.removeAllViews();

        List<Choice> shuffled = shuffledList.get(position);

        for (Choice choice : shuffled) {

            Button b = new Button(h.itemView.getContext());
            b.setText(choice.text);

            if (answers[position] == choice.characterIndex) {
                b.setAlpha(0.5f);
            } else {
                b.setAlpha(1f);
            }

            b.setOnClickListener(v -> {
                int pos = h.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {

                    answers[pos] = choice.characterIndex;

                    notifyItemChanged(pos);
                    notifyAnswered();
                }
            });

            h.group.addView(b);
        }
    }

    private void notifyAnswered() {
        int count = 0;
        for (int a : answers) {
            if (a != -1) count++;
        }
        listener.onAnswered(count);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        TextView tvQuestion;
        LinearLayout group;

        public Holder(View v) {
            super(v);
            tvQuestion = v.findViewById(R.id.tvQuestion);
            group = v.findViewById(R.id.container);
        }
    }
}