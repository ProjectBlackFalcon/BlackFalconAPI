package com.ankamagames.dofus.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.ankamagames.dofus.Main;


public class Reflection {
    private static final ProtectionDomain currentDomain = Main.class.getProtectionDomain();
    private static final String CONTAINER = Reflection.class.getProtectionDomain().getCodeSource().getLocation().getFile();

    public static Class<?>[] getClassesInPackage(String packageName) throws ClassNotFoundException, IOException {
        if (CONTAINER.endsWith(".jar"))
            return getClassesInPackageInJar(packageName);
        else
            return getClassesInPackageInDir(packageName);
    }

    private static Class<?>[] getClassesInPackageInDir(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        for (File directory : dirs)
            classes.addAll(findClassesInDir(directory, packageName));
        return classes.toArray(new Class[classes.size()]);
    }

    private static List<Class<?>> findClassesInDir(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (!directory.exists())
            return classes;
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory())
                classes.addAll(findClassesInDir(file, packageName + "." + file.getName()));
            else if (file.getName().endsWith(".class"))
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
        }
        return classes;
    }

    private static Class<?>[] getClassesInPackageInJar(String packageName) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new Vector<Class<?>>();
        packageName = packageName.replaceAll("\\.", "/");
        try (JarFile jarFile = new JarFile(CONTAINER)) {
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            JarEntry entry;
            String entryName;
            while (jarEntries.hasMoreElements()) {
                entry = jarEntries.nextElement();
                entryName = entry.getName();
                if (entryName.endsWith(".class") && entryName.startsWith(packageName))
                    classes.add(Class.forName(entryName.substring(0, entryName.length() - 6).replace('/', '.')));
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

}
