package mainGUI_FX;

import java.io.IOException;
import java.io.OutputStream;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 *
 * @author Shkirmantsev
 */
public class MyConsole extends OutputStream
{

    private final TextArea console;

    public MyConsole(TextArea console)
    {
        this.console = console;
    }

    public void appendText(String valueOf)
    {
        Platform.runLater(() -> console.appendText(valueOf));
    }

    @Override
    public void write(int b) throws IOException
    {
        appendText(String.valueOf((char) b));
    }
}
