package io.xpipe.app.fxcomps.impl;

import atlantafx.base.layout.InputGroup;
import io.xpipe.app.comp.base.ButtonComp;
import io.xpipe.app.core.AppFont;
import io.xpipe.app.fxcomps.Comp;
import io.xpipe.app.fxcomps.CompStructure;
import io.xpipe.app.fxcomps.util.PlatformThread;
import io.xpipe.app.util.ClipboardHelper;
import io.xpipe.core.util.InPlaceSecretValue;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Objects;

public class SecretFieldComp extends Comp<SecretFieldComp.Structure> {

    @AllArgsConstructor
    public static class Structure implements CompStructure<InputGroup> {

        private final InputGroup inputGroup;
        @Getter
        private final TextField field;

        @Override
        public InputGroup get() {
            return inputGroup;
        }
    }

    private final Property<InPlaceSecretValue> value;
    private final boolean allowCopy;

    public SecretFieldComp(Property<InPlaceSecretValue> value, boolean allowCopy) {
        this.value = value;
        this.allowCopy = allowCopy;
    }

    public static SecretFieldComp ofString(Property<String> s) {
        var prop = new SimpleObjectProperty<>(s.getValue() != null ? InPlaceSecretValue.of(s.getValue()) : null);
        prop.addListener((observable, oldValue, newValue) -> {
            s.setValue(newValue != null ? new String(newValue.getSecret()) : null);
        });
        s.addListener((observableValue, s1, t1) -> {
            prop.set(t1 != null ? InPlaceSecretValue.of(t1) : null);
        });
        return new SecretFieldComp(prop, false);
    }

    protected InPlaceSecretValue encrypt(char[] c) {
        return InPlaceSecretValue.of(c);
    }

    @Override
    public Structure createBase() {
        var text = new PasswordField();
        text.getStyleClass().add("secret-field-comp");
        text.setText(value.getValue() != null ? value.getValue().getSecretValue() : null);
        text.textProperty().addListener((c, o, n) -> {
            value.setValue(n != null && n.length() > 0 ? encrypt(n.toCharArray()) : null);
        });
        value.addListener((c, o, n) -> {
            PlatformThread.runLaterIfNeeded(() -> {
                // Check if control value is the same. Then don't set it as that might cause bugs
                if ((n == null && text.getText().isEmpty())
                        || Objects.equals(text.getText(), n != null ? n.getSecretValue() : null)) {
                    return;
                }

                text.setText(n != null ? n.getSecretValue() : null);
            });
        });
        HBox.setHgrow(text, Priority.ALWAYS);

        var copyButton = new ButtonComp(null, new FontIcon("mdi2c-clipboard-multiple-outline"), () -> {
            ClipboardHelper.copyPassword(value.getValue());
        }).grow(false, true).tooltipKey("copyPassword").createRegion();

        var ig = new InputGroup(text);
        AppFont.small(ig);
        if (allowCopy) {
            ig.getChildren().add(copyButton);
        }
        return new Structure(ig, text);
    }
}
