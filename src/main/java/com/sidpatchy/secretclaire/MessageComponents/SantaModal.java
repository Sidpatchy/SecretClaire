package com.sidpatchy.secretclaire.MessageComponents;

import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.TextInput;
import org.javacord.api.entity.message.component.TextInputStyle;

public class SantaModal {

    public static ActionRow getRulesRow() {
        return ActionRow.of(TextInput.create(TextInputStyle.PARAGRAPH, "rules-row", "Rules..."));
    }

    public static ActionRow getThemeRow() {
        return ActionRow.of(TextInput.create(TextInputStyle.PARAGRAPH, "theme-row", "Theme..."));
    }

}
