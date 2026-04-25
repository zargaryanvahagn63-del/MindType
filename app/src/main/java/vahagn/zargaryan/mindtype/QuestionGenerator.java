package vahagn.zargaryan.mindtype;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class QuestionGenerator {

    private static final Random r = new Random();

    public enum Level {
        EASY,
        NORMAL,
        HARD
    }

    // --- словари ---
    static String[] actions = {
            "общаться", "разговаривать", "взаимодействовать", "проводить время"
    };

    static String[] ideas = {
            "идеи", "новые концепции", "факты", "реальные вещи"
    };

    static String[] logic = {
            "логику", "разум", "чувства", "эмоции"
    };

    static String[] plans = {
            "планировать", "заранее решать", "действовать спонтанно", "импровизировать"
    };

    static String[] neg = {
            "не люблю", "избегаю", "не предпочитаю"
    };

    // --- шаблоны ---
    static String[] EI_EASY = {
            "Мне нравится {action} с людьми",
            "Я люблю {action} с людьми"
    };

    static String[] EI_HARD = {
            "Я получаю энергию от {action} с людьми",
            "Я склонен искать {action} с людьми"
    };

    static String[] SN = {
            "Мне интереснее {idea}",
            "Я чаще думаю о {idea}"
    };

    static String[] TF = {
            "Я выбираю {logic} при решениях",
            "Я ориентируюсь на {logic}"
    };

    static String[] JP = {
            "Я предпочитаю {plan}",
            "Мне комфортнее {plan}"
    };

    // --- главный метод ---
    public static List<Question> generateMBTI(int countPerTrait, Level level) {

        List<Question> list = new ArrayList<>();
        Set<String> used = new HashSet<>();

        list.addAll(genBlock(pickSet(level, EI_EASY, EI_HARD), 0, countPerTrait, used));
        list.addAll(genBlock(SN, 1, countPerTrait, used));
        list.addAll(genBlock(TF, 2, countPerTrait, used));
        list.addAll(genBlock(JP, 3, countPerTrait, used));

        Collections.shuffle(list);
        return list;
    }

    // --- генерация блока ---
    private static List<Question> genBlock(String[] base, int trait, int count, Set<String> used) {

        List<Question> out = new ArrayList<>();

        while (out.size() < count) {

            String text = mutate(base[r.nextInt(base.length)]);

            if (used.contains(text)) continue;

            used.add(text);

            boolean reverse = r.nextBoolean();

            out.add(new Question(text, trait, reverse));
        }

        return out;
    }

    // --- мутация ---
    private static String mutate(String s) {

        String result = s
                .replace("{action}", pick(actions))
                .replace("{idea}", pick(ideas))
                .replace("{logic}", pick(logic))
                .replace("{plan}", pick(plans));

        if (r.nextInt(100) < 30) {
            result = invert(result);
        }

        return result;
    }

    // --- инверсия ---
    private static String invert(String s) {
        return s
                .replace("нравится", pick(neg))
                .replace("люблю", pick(neg))
                .replace("предпочитаю", pick(neg));
    }

    // --- выбор уровня ---
    private static String[] pickSet(Level level, String[] easy, String[] hard) {
        if (level == Level.EASY) return easy;
        if (level == Level.HARD) return hard;
        return r.nextBoolean() ? easy : hard;
    }

    private static String pick(String[] arr) {
        return arr[r.nextInt(arr.length)];
    }
}