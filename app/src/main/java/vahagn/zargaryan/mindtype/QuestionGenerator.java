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
        EASY, NORMAL, HARD
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

    // --- Словари для EQ ---
    static String[] eqEmotions = {
            "эмоции", "чувства", "переживания", "настроение"
    };
    static String[] eqPeople = {
            "окружающих", "других людей", "близких", "собеседников"
    };

    // --- Шаблоны EQ ---
    static String[] EQ_SELF = {
            "Я всегда понимаю свои {eq_emotion}",
            "Мне легко контролировать свои {eq_emotion}"
    };
    static String[] EQ_SOCIAL = {
            "Я умею находить общий язык с {eqPeople}",
            "Мне легко общаться с {eqPeople}"
    };
    static String[] EQ_EMPATHY = {
            "Я хорошо чувствую {eq_emotion} {eqPeople}",
            "Я сопереживаю, когда вижу чужие {eq_emotion}"
    };

    // --- Словари для Темной Триады ---
    static String[] dtPower = {
            "власть", "контроль", "превосходство", "влияние"
    };
    static String[] dtManipulate = {
            "манипулировать", "управлять", "использовать в своих целях"
    };

    // --- Шаблоны Dark Triad ---
    static String[] DT_NARC = {
            "Я заслуживаю {dt_power} больше, чем другие",
            "Я чувствую свое {dt_power} над остальными"
    };
    static String[] DT_MACH = {
            "Я готов {dtManipulate} людьми ради успеха",
            "Умение {dtManipulate} другими — полезный навык"
    };
    static String[] DT_PSY = {
            "Меня не волнуют чужие {eq_emotion}", // переиспользуем словарь из EQ!
            "Я редко жалею о своих поступках" // статический, для разнообразия
    };

    public static List<Question> generateEQ(int countPerTrait, Level level) {
        List<Question> list = new ArrayList<>();
        Set<String> used = new HashSet<>();

        // Тут можно тоже прикрутить Level, если сделаешь массивы EASY/HARD для EQ
        list.addAll(genBlock(EQ_SELF, 0, countPerTrait, used));
        list.addAll(genBlock(EQ_SOCIAL, 1, countPerTrait, used));
        list.addAll(genBlock(EQ_EMPATHY, 2, countPerTrait, used));

        Collections.shuffle(list);
        return list;
    }

    public static List<Question> generateDarkTriad(int countPerTrait, Level level) {
        List<Question> list = new ArrayList<>();
        Set<String> used = new HashSet<>();

        list.addAll(genBlock(DT_NARC, 0, countPerTrait, used));
        list.addAll(genBlock(DT_MACH, 1, countPerTrait, used));
        list.addAll(genBlock(DT_PSY, 2, countPerTrait, used));

        Collections.shuffle(list);
        return list;
    }

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

    private static List<Question> genBlock(String[] base, int trait, int count, Set<String> used) {
        List<Question> out = new ArrayList<>();

        while (out.size() < count) {
            String rawText = base[r.nextInt(base.length)];
            String text = fillTemplates(rawText);

            if (used.contains(text)) continue;

            // false = прямой вопрос, true = обратный (инверсия)
            boolean isReverse = r.nextBoolean();

            if (isReverse) {
                text = applyNegative(text);
            }

            // Проверяем еще раз после инверсии, чтобы не было дублей
            if (used.contains(text)) continue;

            used.add(text);
            out.add(new Question(text, trait, isReverse));
        }

        return out;
    }

    private static String fillTemplates(String s) {
        return s.replace("{action}", pick(actions))
                .replace("{idea}", pick(ideas))
                .replace("{logic}", pick(logic))
                .replace("{plan}", pick(plans))
                // Добавляем словари для EQ и Темной Триады!
                .replace("{eq_emotion}", pick(eqEmotions))
                .replace("{eqPeople}", pick(eqPeople))
                .replace("{dt_power}", pick(dtPower))
                .replace("{dtManipulate}", pick(dtManipulate));
    }

    private static String applyNegative(String s) {
        return s.replace("Мне нравится", "Я скорее избегаю")
                .replace("Я люблю", "Мне не очень нравится")
                .replace("Я предпочитаю", "Я не особо люблю")
                .replace("Мне интереснее", "Мне скучно изучать")
                .replace("Я выбираю", "Я редко выбираю")
                .replace("Я получаю энергию от", "Меня утомляет")
                .replace("Я склонен искать", "Я стараюсь избегать");
    }

    private static String[] pickSet(Level level, String[] easy, String[] hard) {
        if (level == Level.EASY) return easy;
        if (level == Level.HARD) return hard;
        return r.nextBoolean() ? easy : hard;
    }

    private static String pick(String[] arr) {
        return arr[r.nextInt(arr.length)];
    }
}