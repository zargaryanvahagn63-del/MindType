package vahagn.zargaryan.mindtype.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Генератор вопросов для тестов.
 * Использует наборы шаблонов и словарей для динамического создания разнообразных вопросов.
 */
public class QuestionGenerator {

    private static final Random r = new Random();

    /**
     * Уровни сложности вопросов.
     */
    public enum Level {
        EASY, NORMAL, HARD
    }

    // --- Словари терминов для шаблонов MBTI ---
    static String[] actions = { "быть в центре внимания", "общаться с незнакомцами", "активно взаимодействовать" };
    static String[] ideas = { "конкретные факты", "реальный опыт", "практические детали" };
    static String[] logic = { "логический анализ", "объективные доводы", "рациональные аргументы" };
    static String[] plans = { "строгий график", "заранее составленный план", "четкую структуру" };

    // --- Шаблоны для MBTI (E/I, S/N, T/F, J/P) ---
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

    // --- Словари для EQ (Эмоциональный интеллект) ---
    static String[] eqEmotions = { "эмоции", "чувства", "переживания", "настроение" };
    static String[] eqPeople = { "окружающих", "других людей", "близких", "собеседников" };

    // --- Шаблоны для EQ ---
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
    static String[] dtPower = { "власть", "контроль", "превосходство", "влияние" };
    static String[] dtManipulate = { "манипулировать", "управлять", "использовать в своих целях" };

    // --- Шаблоны для Темной Триады ---
    static String[] DT_NARC = {
            "Я заслуживаю {dt_power} больше, чем другие",
            "Я чувствую свое {dt_power} над остальными"
    };
    static String[] DT_MACH = {
            "Я готов {dtManipulate} людьми ради успеха",
            "Умение {dtManipulate} другими — полезный навык"
    };
    static String[] DT_PSY = {
            "Меня не волнуют чужие {eq_emotion}",
            "Я редко жалею о своих поступках"
    };

    // --- Словари для VARK ---
    static String[] varkVisual = { "инфографику и графики", "видеоуроки с анимацией", "цветовые пометки в тексте" };
    static String[] varkAural = { "аудиокниги и подкасты", "обсуждение темы в группе", "объяснение материала вслух" };
    static String[] varkRead = { "чтение длинных статей", "составлять подробные списки", "выписывание определений" };
    static String[] varkKin = { "практические опыты", "сборку моделей своими руками", "обучение через движение" };

    static String[] VARK_T = {
            "Мне легче усвоить материал через {item}",
            "Я предпочитаю использовать {item} при учебе",
            "Для меня эффективнее всего {item}"
    };

    /**
     * Генерирует вопросы для теста VARK.
     */
    public static List<Question> generateVARK(int countPerTrait) {
        List<Question> list = new ArrayList<>();
        Set<String> used = new HashSet<>();

        list.addAll(genVarkBlock(varkVisual, 0, countPerTrait, used));
        list.addAll(genVarkBlock(varkAural, 1, countPerTrait, used));
        list.addAll(genVarkBlock(varkRead, 2, countPerTrait, used));
        list.addAll(genVarkBlock(varkKin, 3, countPerTrait, used));

        Collections.shuffle(list);
        return list;
    }

    private static List<Question> genVarkBlock(String[] items, int trait, int count, Set<String> used) {
        List<Question> out = new ArrayList<>();
        while (out.size() < count) {
            String raw = VARK_T[r.nextInt(VARK_T.length)];
            String text = raw.replace("{item}", items[r.nextInt(items.length)]);
            if (used.contains(text)) continue;
            used.add(text);
            out.add(new Question(text, trait, false));
        }
        return out;
    }

    /**
     * Генерирует вопросы для теста EQ.
     */
    public static List<Question> generateEQ(int countPerTrait, Level level) {
        List<Question> list = new ArrayList<>();
        Set<String> used = new HashSet<>();

        list.addAll(genBlock(EQ_SELF, 0, countPerTrait, used));
        list.addAll(genBlock(EQ_SOCIAL, 1, countPerTrait, used));
        list.addAll(genBlock(EQ_EMPATHY, 2, countPerTrait, used));

        Collections.shuffle(list);
        return list;
    }

    /**
     * Генерирует вопросы для теста "Темная триада".
     */
    public static List<Question> generateDarkTriad(int countPerTrait, Level level) {
        List<Question> list = new ArrayList<>();
        Set<String> used = new HashSet<>();

        list.addAll(genBlock(DT_NARC, 0, countPerTrait, used));
        list.addAll(genBlock(DT_MACH, 1, countPerTrait, used));
        list.addAll(genBlock(DT_PSY, 2, countPerTrait, used));

        Collections.shuffle(list);
        return list;
    }

    /**
     * Генерирует вопросы для теста MBTI.
     */
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

    /**
     * Вспомогательный метод для генерации блока вопросов по конкретной черте.
     */
    private static List<Question> genBlock(String[] base, int trait, int count, Set<String> used) {
        List<Question> out = new ArrayList<>();

        while (out.size() < count) {
            String rawText = base[r.nextInt(base.length)];
            String text = fillTemplates(rawText);

            if (used.contains(text)) continue;

            // Случайный выбор: прямой вопрос или обратный
            boolean isReverse = r.nextBoolean();
            if (isReverse) {
                text = applyNegative(text);
            }

            if (used.contains(text)) continue;

            used.add(text);
            out.add(new Question(text, trait, isReverse));
        }

        return out;
    }

    /**
     * Заполняет шаблоны случайными словами из словарей.
     */
    private static String fillTemplates(String s) {
        return s.replace("{action}", pick(actions))
                .replace("{idea}", pick(ideas))
                .replace("{logic}", pick(logic))
                .replace("{plan}", pick(plans))
                .replace("{eq_emotion}", pick(eqEmotions))
                .replace("{eqPeople}", pick(eqPeople))
                .replace("{dt_power}", pick(dtPower))
                .replace("{dtManipulate}", pick(dtManipulate));
    }

    /**
     * Преобразует утверждение в отрицание для создания обратных вопросов.
     */
    private static String applyNegative(String s) {
        return s.replace("Мне нравится", "Я скорее избегаю")
                .replace("Я люблю", "Мне не очень нравится")
                .replace("Я предпочитаю", "Я не особо люблю")
                .replace("Мне интереснее", "Мне скучно изучать")
                .replace("Я выбираю", "Я редко выбираю")
                .replace("Я получаю энергию от", "Меня утомляет")
                .replace("Я склонен искать", "Я стараюсь избегать")
                .replace("Я всегда понимаю", "Я не всегда понимаю")
                .replace("Мне легко", "Мне довольно трудно")
                .replace("Я умею", "Я не всегда умею")
                .replace("Я хорошо чувствую", "Я плохо считываю");
    }

    /**
     * Выбирает набор шаблонов в зависимости от уровня сложности.
     */
    private static String[] pickSet(Level level, String[] easy, String[] hard) {
        if (level == Level.EASY) return easy;
        if (level == Level.HARD) return hard;
        return r.nextBoolean() ? easy : hard;
    }

    private static String pick(String[] arr) {
        return arr[r.nextInt(arr.length)];
    }
}
