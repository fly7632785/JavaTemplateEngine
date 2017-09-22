package indi.yume.tools.codegenerator.model;

import java.util.List;

import indi.yume.tools.codegenerator.generator.NewLine;

/**
 * Created by yume on 15/9/26.
 */
public class PropertyInfo extends BaseInfo {
    private ModifierInfo modifier = new ModifierInfo(0);
    private ClazzInfo clazzInfo;
    private String name;
    private String body;
    private String note;

    public PropertyInfo(ClazzInfo clazzInfo, String name) {
        this.clazzInfo = clazzInfo;
        this.name = name;
    }

    public PropertyInfo(ModifierInfo modifier, ClazzInfo clazzInfo, String name) {
        this.modifier = modifier;
        this.clazzInfo = clazzInfo;
        this.name = name;
    }

    public String toString(NewLine newline) {
        String mod = modifier.toString();
        if (!"".equals(mod))
            mod += " ";
        StringBuilder stringBuilder = new StringBuilder();
        if (note != null && !note.isEmpty()) {
            note = note.replace("\\n", "\n");
            String[] ss = note.split("\n");
            stringBuilder.append(newline.getPrefix() + "/**\n");
            for (int i = 0; i < ss.length; i++) {
                stringBuilder.append(newline.getPrefix() + "* " + ss[i] + "\n");
            }
            stringBuilder.append(newline.getPrefix() + "*/\n");
        }
        stringBuilder.append(generatorAnnotation(newline)
                + newline.getPrefix()
                + mod
                + clazzInfo.toString()
                + " " + name);

        if (body != null && !body.isEmpty()) {
            stringBuilder.append(" = " + body);
        }

        return stringBuilder.toString();
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ModifierInfo getModifier() {
        return modifier;
    }

    public void setModifier(ModifierInfo modifier) {
        this.modifier = modifier;
    }

    public ClazzInfo getClazzInfo() {
        return clazzInfo;
    }

    public void setClazzInfo(ClazzInfo clazzInfo) {
        this.clazzInfo = clazzInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<String> getImportClazz() {
        return getAnnoImportClazz(clazzInfo.getImportClazz());
    }
}
