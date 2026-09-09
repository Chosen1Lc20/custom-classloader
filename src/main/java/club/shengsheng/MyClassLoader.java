package club.shengsheng;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class MyClassLoader extends ClassLoader {
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        //我要打破双亲委派机制
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            // c就是我们加载的类
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                //java包下的类,我们让父类加载器去加载. 其它的我们自定义类加载器去加载
                if (name.startsWith("java")) {
                    if (getParent() != null) {
                        ClassLoader parent = getParent();
                        c = parent.loadClass(name);
                    }
                }
                if (c == null) {
                    // If still not found, then invoke findClass in order
                    // to find the class.
                    c = findClass(name);
                }
            }
            return c;
        }
    }

    @Override
    //根据这个 fqcn 找类
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bt;
        try {
            bt = Files.readAllBytes(Paths.get("加密.class"));
            if (bt.length == 0) {
                throw new FileNotFoundException("这个文件的内容为空");
            }
            for (int i = 0; i < bt.length; i++) {
                bt[i] = (byte) (bt[i] - 1);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //这个函数重要
        return defineClass(name, bt, 0, bt.length);
    }
}
