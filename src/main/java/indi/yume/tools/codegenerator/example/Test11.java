package indi.yume.tools.codegenerator.example;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import indi.yume.tools.codegenerator.generator.ClazzGenerator;
import indi.yume.tools.codegenerator.template.TemplateEngine;
import indi.yume.tools.codegenerator.template.VarStringEngine;

/**
 * Created by yume on 15/11/24.
 */
public class Test11 {
    public static void main(String[] args){
        VarStringEngine varStringEngine = new VarStringEngine();
        varStringEngine.binding("basePackage", "com.happy_bears.mybears.android");
        varStringEngine.binding("diPackage", ".di");
        varStringEngine.binding("uiPackage", ".ui");
        varStringEngine.binding("fragmentPackage", ".ui.fragment");
        varStringEngine.binding("activityPackage", ".ui.activity");
        varStringEngine.binding("presenterPackage", ".ui.presenter");
        varStringEngine.binding("componentPackage", ".ui.component");
        varStringEngine.binding("modulePackage", ".ui.module");

        varStringEngine.binding("name", "A09_1_TestTest");

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        varStringEngine.binding("note", "Created by DaggerGenerator on " + df.format(Calendar.getInstance().getTime()) + ".\ntest");

        varStringEngine.binding("type", "Activity");
        try {
            File baseFile = new File("");
            TemplateEngine templateEngine = new TemplateEngine(new File(baseFile.getAbsoluteFile() + "/src/main/java/indi/yume/tools/codegenerator/example/config.xml"), varStringEngine);
            ClazzGenerator generator = templateEngine.setTemplateFile(new File(baseFile.getAbsoluteFile() + "/src/main/java/indi/yume/tools/codegenerator/example/activity.xml"));
            String content = generator.render();
            System.out.println(content);
//            ClazzGenerator generator1 = templateEngine.setTemplateFile(new File(baseFile.getAbsoluteFile() + "/src/main/java/indi/yume/tools/codegenerator/example/abstract.xml"));
//            String content1 = generator1.render();
//            System.out.println(content1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}