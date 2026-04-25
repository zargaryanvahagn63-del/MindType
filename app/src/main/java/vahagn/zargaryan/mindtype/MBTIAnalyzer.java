package vahagn.zargaryan.mindtype;

import java.util.Random;

public class MBTIAnalyzer {

    static Random r = new Random();

    public static MBTIResult analyze(int E, int S, int T, int J) {

        String type = "";

        type += (E > 50) ? "E" : "I";
        type += (S > 50) ? "S" : "N";
        type += (T > 50) ? "T" : "F";
        type += (J > 50) ? "J" : "P";

        String desc =
                descEI(E) + " " +
                        descSN(S) + " " +
                        descTF(T) + " " +
                        descJP(J) + "\n\n" +
                        summary(type);

        return new MBTIResult(type, desc);
    }

    private static String descEI(int E) {
        if (E > 60) return pick(
                "Ты получаешь энергию от людей.",
                "Общение тебя заряжает."
        );
        else return pick(
                "Ты черпаешь энергию в одиночестве.",
                "Тебе комфортнее одному."
        );
    }

    private static String descSN(int S) {
        if (S > 60) return pick(
                "Ты опираешься на факты.",
                "Ты практичен."
        );
        else return pick(
                "Ты думаешь о возможностях.",
                "Ты ориентирован на идеи."
        );
    }

    private static String descTF(int T) {
        if (T > 60) return pick(
                "Ты принимаешь решения логикой.",
                "Рациональность для тебя важна."
        );
        else return pick(
                "Ты ориентируешься на чувства.",
                "Эмоции важны для тебя."
        );
    }

    private static String descJP(int J) {
        if (J > 60) return pick(
                "Ты любишь порядок.",
                "Ты предпочитаешь план."
        );
        else return pick(
                "Ты гибкий и спонтанный.",
                "Ты не любишь жесткие рамки."
        );
    }

    private static String summary(String type) {

        switch (type) {
            case "INTJ":
                return "Стратег. Сильное мышление и независимость.";
            case "ENFP":
                return "Идейный и энергичный человек.";
            case "ISTJ":
                return "Надёжный и системный.";
            case "ESFP":
                return "Активный и ориентирован на эмоции.";
        }

        return "У тебя сбалансированный тип личности.";
    }

    private static String pick(String... arr) {
        return arr[r.nextInt(arr.length)];
    }
}