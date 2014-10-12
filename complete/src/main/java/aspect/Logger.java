package aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

/**
 * Created by mdautrey on 12/10/14.
 */
@Aspect
public class Logger {
    @After(value = "execution(* model.data.InterfaceIndividu.getIndividu(..))")
    public void afterSelect() throws Exception {
        throw new Exception("test aspect pointcut");

    }
}
