
package toolsClases;

import bases.TreeGrower;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 *
 * @author Shkirmantsev
 */
public class MyLogPrinter
{
    private final Logger myPrintLog;
    
    public MyLogPrinter(Class cls){
        
        this.myPrintLog = Logger.getLogger(cls.getName());
        myPrintLog.setUseParentHandlers(false);
        
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new MyFormatter());
        consoleHandler.setLevel(Level.FINEST);

        myPrintLog.addHandler(consoleHandler);
        myPrintLog.setLevel(Level.OFF);}
     
    public MyLogPrinter(Class cls,Level hndlrLevel,Level onOff){
     this.myPrintLog = Logger.getLogger(cls.getName());
        myPrintLog.setUseParentHandlers(false);
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new MyFormatter());
        consoleHandler.setLevel(hndlrLevel);

        myPrintLog.addHandler(consoleHandler);
        myPrintLog.setLevel(onOff);
     }
    
    public void print(String str){
    

        this.myPrintLog.log(Level.INFO, str);
    }
    public void print(Level prntLevel,String str){
    

         this.myPrintLog.log(prntLevel, str);
    }
    
    public void print(Object obj){
    
        this.print(obj.toString());
    }
    public void print(Level prntLevel,Object obj){
    
        this.print(prntLevel, obj.toString());
    }
    
    private class MyFormatter extends Formatter{

        @Override
        public String format(LogRecord record)
        {
            return record.getMessage()+"\r\n";
        }
    }
    
}
