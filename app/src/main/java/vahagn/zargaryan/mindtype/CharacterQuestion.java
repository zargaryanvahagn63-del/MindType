package vahagn.zargaryan.mindtype;

import java.util.List;

class CharacterQuestion {
    String text;
    List<Choice> choices;

    CharacterQuestion(String text, List<Choice> choices) {
        this.text = text;
        this.choices = choices;
    }
}