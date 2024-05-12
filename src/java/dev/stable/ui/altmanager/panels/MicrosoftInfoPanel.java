package dev.stable.ui.altmanager.panels;

import dev.stable.ui.altmanager.Panel;
import dev.stable.utils.Utils;
import dev.stable.utils.render.ColorUtil;
import dev.stable.utils.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class MicrosoftInfoPanel extends Panel {

    private final List<Pair<String, String>> steps = new ArrayList<>();

    public MicrosoftInfoPanel() {
        setHeight(135);
        steps.add(Pair.of("1", "Type the email and password either as a combo or in each respective field"));
        steps.add(Pair.of("2", "Click the microsoft login button"));
        steps.add(Pair.of("3", "Your browser will open with a microsoft login panel"));
        steps.add(Pair.of("INFO", "Make sure that you are logged out of all microsoft accounts so that you are prompted with a login panel"));
        steps.add(Pair.of("4", "The email and password will be copied to the clipboard"));
        steps.add(Pair.of("5", "Follow all the steps Microsoft gives you to log into your account"));
        steps.add(Pair.of("6", "Enjoy! You are now logged in to your microsoft account"));
    }


    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }


    @Override
    public void drawScreen(int mouseX, int mouseY) {
        setHeight(119);
        super.drawScreen(mouseX, mouseY);
        Utils.tenacityBoldFont26.drawCenteredString("How to use Microsoft Login", getX() + getWidth() / 2f, getY() + 4, ColorUtil.applyOpacity(-1, .75f));

        float controlY = getY() + Utils.tenacityBoldFont32.getHeight() + 8;
        for (Pair<String, String> control : steps) {
            if (control.getFirst().equals("INFO")) {
                Utils.rubikFont16.drawCenteredString("Make sure that you are logged out of all microsoft accounts so that you are",
                        getX() + getWidth() /2f, controlY, ColorUtil.applyOpacity(-1, .75f));

                controlY += Utils.tenacityBoldFont16.getHeight() + 4;

                Utils.rubikFont16.drawCenteredString("prompted with a new login panel",
                        getX() + getWidth() /2f, controlY, ColorUtil.applyOpacity(-1, .75f));

                controlY += Utils.tenacityBoldFont16.getHeight() + 6;
                continue;
            }

            Utils.tenacityBoldFont16.drawString(control.getFirst() + ". ", getX() + 12, controlY, ColorUtil.applyOpacity(-1, .5f));
            Utils.rubikFont16.drawString(control.getSecond(), getX() +
                    Utils.tenacityBoldFont16.getStringWidth(control.getFirst() + ". ") + 14, controlY, ColorUtil.applyOpacity(-1, .35f));

            controlY += Utils.tenacityBoldFont16.getHeight() + 6;
        }

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
