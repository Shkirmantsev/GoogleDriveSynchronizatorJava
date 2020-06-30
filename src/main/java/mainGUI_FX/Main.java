
package mainGUI_FX;

/**
 *
 * @author Shkirmantsev
 */
public class Main {
    public static void main(final String[] args) {
        final String libsfx="libsfx";
        
        System.setProperty("java.library.path",System.getProperty("java.library.path")
                +":"+ClassLoader.getSystemClassLoader().getResource(libsfx).getFile());
        
        MainJFX.main(args);
    }
    
}
