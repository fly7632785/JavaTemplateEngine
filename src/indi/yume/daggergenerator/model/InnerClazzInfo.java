package indi.yume.daggergenerator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yume on 2015/9/27.
 */
public class InnerClazzInfo extends ClazzInfo {
    public InnerClazzInfo(String packageName, String clazzName) {
        super(packageName, clazzName);
    }

    public InnerClazzInfo(Type type, String packageName, String clazzName) {
        super(type, packageName, clazzName);
    }

    public InnerClazzInfo(String packageName, Type type, String clazzName, ClazzInfo genericClazz){
        super(packageName, type, clazzName, genericClazz);
    }

    @Override
    public List<String> getImportClazz() {
        List<String> list = new ArrayList<>();
        if(packageName != null)
            list.add(packageName);

        if(genericClazz != null)
            list.addAll(genericClazz.getImportClazz());
        list = getAnnoImportClazz(list);
        return list;
    }
}