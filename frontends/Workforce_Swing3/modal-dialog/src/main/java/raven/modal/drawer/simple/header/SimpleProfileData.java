package raven.modal.drawer.simple.header;

import javax.swing.*;

/**
 * @author Raven
 */
public class SimpleProfileData {

    public Icon getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public SimpleProfileStyle getSimpleHeaderStyle() {
        return simpleHeaderStyle;
    }

    protected Icon icon;
    protected String title;
    protected String description;

    protected SimpleProfileStyle simpleHeaderStyle;

    public SimpleProfileData setIcon(Icon icon) {
        this.icon = icon;
        return this;
    }

    public SimpleProfileData setTitle(String title) {
        this.title = title;
        return this;
    }

    public SimpleProfileData setDescription(String description) {
        this.description = description;
        return this;
    }

    public SimpleProfileData setHeaderStyle(SimpleProfileStyle simpleHeaderStyle) {
        this.simpleHeaderStyle = simpleHeaderStyle;
        return this;
    }
}
