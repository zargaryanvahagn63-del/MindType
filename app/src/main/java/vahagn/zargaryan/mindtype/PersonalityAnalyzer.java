package vahagn.zargaryan.mindtype;

import java.util.Random;

public class PersonalityAnalyzer {

    private static final Random r = new Random();

    public static String analyze(int E, int A, int C, int N, int O) {

        String text = intro() +
                "\n\n" +
                extraversion(E) + " " +
                agreeableness(A) + " " +
                conscientiousness(C) + " " +
                neuroticism(N) + " " +
                openness(O) + " " +
                "\n\n" +
                summary(E, A, C, N, O);

        return text;
    }

    // --- Вступление ---
    private static String intro() {
        String[] arr = {
                "Анализ завершён.",
                "Результаты готовы.",
                "Твой профиль личности сформирован."
        };
        return arr[r.nextInt(arr.length)];
    }

    // --- E ---
    private static String extraversion(int E) {
        if (E > 60) {
            return pick(
                    "Ты открыт к людям и легко входишь в новые компании.",
                    "Общение даёт тебе энергию.",
                    "Ты склонен быть активным и социальным."
            );
        } else if (E < 40) {
            return pick(
                    "Ты предпочитаешь спокойствие и уединение.",
                    "Тебе комфортнее в узком кругу.",
                    "Ты быстро устаёшь от большого количества людей."
            );
        } else {
            return pick(
                    "Ты балансируешь между общением и уединением.",
                    "Ты адаптивен в социальной среде.",
                    "Ты можешь быть и активным, и спокойным."
            );
        }
    }

    // --- A ---
    private static String agreeableness(int A) {
        if (A > 60) {
            return pick(
                    "Ты склонен доверять людям.",
                    "Ты проявляешь эмпатию и доброжелательность.",
                    "Ты стараешься избегать конфликтов."
            );
        } else {
            return pick(
                    "Ты более прямолинеен.",
                    "Ты не всегда соглашаешься с другими.",
                    "Ты склонен отстаивать свою позицию."
            );
        }
    }

    // --- C ---
    private static String conscientiousness(int C) {
        if (C > 60) {
            return pick(
                    "Ты организован и дисциплинирован.",
                    "Ты доводишь дела до конца.",
                    "Ты умеешь планировать."
            );
        } else {
            return pick(
                    "Ты действуешь более спонтанно.",
                    "Ты не любишь строгие рамки.",
                    "Ты гибок в своих решениях."
            );
        }
    }

    // --- N ---
    private static String neuroticism(int N) {
        if (N > 60) {
            return pick(
                    "Ты склонен к переживаниям.",
                    "Эмоции могут сильно влиять на тебя.",
                    "Ты чувствителен к стрессу."
            );
        } else {
            return pick(
                    "Ты эмоционально стабилен.",
                    "Ты спокойно реагируешь на стресс.",
                    "Ты сохраняешь хладнокровие."
            );
        }
    }

    // --- O ---
    private static String openness(int O) {
        if (O > 60) {
            return pick(
                    "Тебе интересны новые идеи.",
                    "Ты любишь пробовать новое.",
                    "Ты склонен к творчеству."
            );
        } else {
            return pick(
                    "Ты предпочитаешь проверенные вещи.",
                    "Ты ценишь стабильность.",
                    "Ты не любишь резкие изменения."
            );
        }
    }

    // --- Итог ---
    private static String summary(int E, int A, int C, int N, int O) {

        if (E > 60 && O > 60) {
            return "Ты активный и открытый к новому человек.";
        }

        if (E < 40 && N > 60) {
            return "Ты более замкнутый и чувствительный тип.";
        }

        if (A > 60 && C > 60) {
            return "Ты надёжный и доброжелательный человек.";
        }

        if (A < 40 && C < 40) {
            return "Ты независимый и не любишь ограничения.";
        }

        return "У тебя сбалансированный профиль личности.";
    }

    // --- random helper ---
    private static String pick(String... arr) {
        return arr[r.nextInt(arr.length)];
    }
}