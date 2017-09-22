package indi.yume.tools.codegenerator.template;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import indi.yume.tools.codegenerator.generator.ClazzGenerator;
import indi.yume.tools.codegenerator.model.AnnotationInfo;
import indi.yume.tools.codegenerator.model.ClazzInfo;
import indi.yume.tools.codegenerator.model.MethodBodyGenerator;
import indi.yume.tools.codegenerator.model.MethodInfo;
import indi.yume.tools.codegenerator.model.ModifierInfo;
import indi.yume.tools.codegenerator.model.PropertyInfo;
import indi.yume.tools.codegenerator.model.Type;

/**
 * Created by yume on 15/11/23.
 */
public class TemplateEngine {
    private VarStringEngine varStringEngine;
    private ConfigEngine configEngine;

    public TemplateEngine(File configFile, VarStringEngine varStringEngine) throws Exception {
        this.configEngine = new ConfigEngine(configFile, varStringEngine);
        this.varStringEngine = varStringEngine;
    }

    public ClazzGenerator setTemplateFile(File templateFile) throws Exception {
        Document document = Jsoup.parse(new FileInputStream(templateFile), "UTF-8", "", Parser.xmlParser());

        Element classMakerElement = document.getElementsByTag(ClassMakerKey.KEY).first();

        ClazzGenerator generator = analysisClazzMaker(classMakerElement);

        return generator;
    }

    private ClazzGenerator analysisClazzMaker(Element classMakerElement) throws Exception {
        if (!ClassMakerKey.KEY.toLowerCase().trim().equals(classMakerElement.tag().getName()))
            throw new Exception("analysisClazzMaker has error, is not a ClassMaker: \n" + classMakerElement.toString());

        ClazzInfo baseClazzInfo = getBaseClass(classMakerElement);

        ClazzGenerator clazzGenerator = new ClazzGenerator(baseClazzInfo);

        getExtend(clazzGenerator, classMakerElement);
        getImplement(clazzGenerator, classMakerElement);

        for (AnnotationInfo anno : getAnno(classMakerElement))
            clazzGenerator.addAnnotation(anno);
        if (classMakerElement.hasAttr(ClassMakerKey.MODIFIER))
            baseClazzInfo.
                    setModifierInfo(new ModifierInfo(ModifierUtil
                            .analysisModifier(
                                    varStringEngine.analysisString(
                                            classMakerElement.attr(ClassMakerKey.MODIFIER)))));
        getNote(clazzGenerator, classMakerElement);
        getProperty(clazzGenerator, classMakerElement);
        getMethod(clazzGenerator, classMakerElement);

        Elements innerClazz = getNodesInChildren(classMakerElement, ClassMakerKey.KEY);
        for (Element inner : innerClazz)
            clazzGenerator.addInnerClazz(analysisClazzMaker(inner));
        Element note = getNodesInChildren(classMakerElement, ClassMakerKey.NOTE.KEY).first();
        if (note != null && !note.text().isEmpty()) {
            baseClazzInfo.setNote(note.text());
        }
        return clazzGenerator;
    }

    private ClazzInfo getBaseClass(Element classMaker) throws Exception {
        if (classMaker.hasAttr(ClassMakerKey.CLASS_VAR_NAME_ATTR))
            return configEngine.getClazzInfoInClazzInterface(
                    varStringEngine.analysisString(
                            classMaker.attr(ClassMakerKey.CLASS_VAR_NAME_ATTR)));

        ClazzInfo baseClazzInfo = new ClazzInfo();

        baseClazzInfo.setPackageName(
                varStringEngine.analysisString(
                        classMaker.attr(ClassMakerKey.PACKAGE_NAME)));
        baseClazzInfo.setClazzName(
                varStringEngine.analysisString(
                        classMaker.attr(ClassMakerKey.CLASS_NAME)));

        if (classMaker.hasAttr(ClassMakerKey.TYPE)) {
            String type = varStringEngine.analysisString(
                    classMaker.attr(ClassMakerKey.TYPE));
            switch (type) {
                case "interface":
                    baseClazzInfo.setType(Type.INTERFACE);
                    break;
                case "class":
                    baseClazzInfo.setType(Type.CLASS);
                    break;
                case "anno":
                    baseClazzInfo.setType(Type.ANNOTATION);
                    break;
            }
        }

        if (classMaker.hasAttr(ClassMakerKey.MODIFIER))
            baseClazzInfo.
                    setModifierInfo(new ModifierInfo(ModifierUtil
                            .analysisModifier(
                                    varStringEngine.analysisString(
                                            classMaker.attr(ClassMakerKey.MODIFIER)))));
        return baseClazzInfo;
    }

    private void getNote(ClazzGenerator classGenerator, Element classMaker) throws Exception {
        Element note = getNodesInChildren(classMaker, ClassMakerKey.NOTE.KEY).first();
        if (note != null) {
            String noteString = varStringEngine.analysisString(note.text());
            noteString = noteString.replace("\\n", "\n");
            StringBuilder stringBuilder = new StringBuilder();
            String[] ss = noteString.split("\n");
            stringBuilder.append("/**\n");
            for (int i = 0; i < ss.length; i++) {
                stringBuilder.append("* " + ss[i]+"\n");
            }
            stringBuilder.append( "*/\n");
            classGenerator.setNote(stringBuilder.toString());
        }
    }

    private void getExtend(ClazzGenerator clazzGenerator, Element classMaker) {
        Element extendEle = getNodesInChildren(classMaker, ClassMakerKey.EXTENDS.KEY).first();
        if (extendEle != null) {
            try {
                clazzGenerator.setExtendsClazzInfo(
                        configEngine.getClazzInfoInClazz(
                                varStringEngine.analysisString(
                                        extendEle.attr(ClassMakerKey.EXTENDS.NAME_ATTR))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getImplement(ClazzGenerator clazzGenerator, Element classMaker) {
        Element interfaceEle = getNodesInChildren(classMaker, ClassMakerKey.IMPLEMENTS.KEY).first();
        if (interfaceEle != null) {
            if (interfaceEle.hasAttr(ClassMakerKey.IMPLEMENTS.NAME_ATTR))
                try {
                    clazzGenerator.addInterface(
                            configEngine.getClazzInfoInInterface(
                                    varStringEngine.analysisString(
                                            interfaceEle.attr(ClassMakerKey.IMPLEMENTS.NAME_ATTR))));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            Elements itemList = getNodesInChildren(interfaceEle, ClassMakerKey.IMPLEMENTS.ITEM.KEY);
            for (Element itemEle : itemList)
                try {
                    clazzGenerator.addInterface(
                            configEngine.getClazzInfoInInterface(
                                    varStringEngine.analysisString(
                                            itemEle.attr(ClassMakerKey.IMPLEMENTS.ITEM.NAME_ATTR))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    private void getProperty(ClazzGenerator clazzGenerator, Element classMaker) throws Exception {
        Elements propertyList = getNodesInChildren(classMaker, ClassMakerKey.PROPERTY.KEY);
        for (Element propertyEle : propertyList) {
            ClazzInfo clazzInfo = configEngine.getClazzInfoInClazzInterface(
                    varStringEngine.analysisString(
                            propertyEle.attr(ClassMakerKey.PROPERTY.CLASS_VAR_NAME_ATTR)));
            String varName = varStringEngine.analysisString(
                    propertyEle.attr(ClassMakerKey.PROPERTY.VALUE_NAME_ATTR));
            if (!propertyEle.hasAttr(ClassMakerKey.PROPERTY.VALUE_NAME_ATTR))
                throw new Exception("Property must have " + ClassMakerKey.PROPERTY.VALUE_NAME_ATTR);

            PropertyInfo propertyInfo = new PropertyInfo(clazzInfo, varName);

            for (AnnotationInfo anno : getAnno(propertyEle))
                propertyInfo.addAnnotation(anno);
            if (propertyEle.hasAttr(ClassMakerKey.PROPERTY.MODIFIER_ATTR)) {
                String modifier = varStringEngine.analysisString(
                        propertyEle.attr(ClassMakerKey.PROPERTY.MODIFIER_ATTR));
                propertyInfo.getModifier().setModifier(ModifierUtil.analysisModifier(modifier));
            }
            final Element body = getNodesInChildren(propertyEle, ClassMakerKey.PROPERTY.BODY).first();
            if (body != null && !body.text().isEmpty()) {
                propertyInfo.setBody(body.text());
            }
            Element note = getNodesInChildren(propertyEle, ClassMakerKey.PROPERTY.NOTE.KEY).first();
            if (note != null && !note.text().isEmpty()) {
                propertyInfo.setNote(note.text());
            }
            clazzGenerator.addProperty(propertyInfo);
        }
    }

    private void getMethod(ClazzGenerator clazzGenerator, Element classMaker) throws Exception {
        Elements methodList = getNodesInChildren(classMaker, ClassMakerKey.METHOD.KEY);

        for (Element method : methodList) {
            ClazzInfo returnClazz = null;
            if (method.hasAttr(ClassMakerKey.METHOD.RETURN_CLASS_NAME_ATTR))
                returnClazz = configEngine.getClazzInfoInClazzInterface(
                        varStringEngine.analysisString(
                                method.attr(ClassMakerKey.METHOD.RETURN_CLASS_NAME_ATTR)));
            String methodName = varStringEngine.analysisString(
                    method.attr(ClassMakerKey.METHOD.METHOD_NAME_ATTR));

            List<MethodInfo.ParamInfo> paramList = new ArrayList<>();
            Elements paramEleList = getNodesInChildren(method, ClassMakerKey.METHOD.PARAM.KEY);
            for (Element paramEle : paramEleList) {
                MethodInfo.ParamInfo paramInfo = new MethodInfo.ParamInfo(
                        configEngine.getClazzInfoInClazzInterface(
                                varStringEngine.analysisString(
                                        paramEle.attr(ClassMakerKey.METHOD.PARAM.CLASS_VAR_NAME_ATTR))),
                        varStringEngine.analysisString(
                                paramEle.attr(
                                        ClassMakerKey.METHOD.PARAM.VALUE_NAME_ATTR)));
                paramList.add(paramInfo);
            }


            final Element body = getNodesInChildren(method, ClassMakerKey.METHOD.BODY.KEY).first();
            MethodInfo methodInfo = new MethodInfo(body == null ? Type.INTERFACE : Type.CLASS,
                    returnClazz,
                    methodName,
                    paramList.toArray(new MethodInfo.ParamInfo[paramList.size()]));
            Element note = getNodesInChildren(method, ClassMakerKey.METHOD.NOTE.KEY).first();
            if (note != null && !note.text().isEmpty()) {
                methodInfo.setNote(note.text());
            }
            Elements includeList = getNodesInChildren(method, ClassMakerKey.METHOD.INCLUDE.KEY);
            List<ClazzInfo> inClazzList = new ArrayList<>();
            for (Element includeEle : includeList)
                inClazzList.add(configEngine.getClazzInfoInClazzInterface(
                        varStringEngine.analysisString(
                                includeEle.attr(ClassMakerKey.METHOD.INCLUDE.CLASS_VAR_NAME_ATTR))));
            methodInfo.setMethodBodyGenerator(new MethodBodyGenerator() {
                                                  @Override
                                                  public String generatorMethodBody(String tab) {
                                                      String bodyString = null;
                                                      try {
                                                          bodyString = body != null ? body.text() : "";
                                                          bodyString = bodyString.replace("\\n", "\n");
                                                          bodyString = varStringEngine.analysisString(bodyString);
                                                          bodyString = StringContentEngine.generateString("", tab, bodyString);
                                                      } catch (Exception e) {
                                                          e.printStackTrace();
                                                      }
                                                      return bodyString;
                                                  }
                                              },
                    inClazzList.toArray(new ClazzInfo[inClazzList.size()]));

            for (AnnotationInfo anno : getAnno(method))
                methodInfo.addAnnotation(anno);

            if (method.hasAttr(ClassMakerKey.METHOD.MODIFIER_ATTR)) {
                String modifier = varStringEngine.analysisString(
                        method.attr(ClassMakerKey.METHOD.MODIFIER_ATTR));
                methodInfo.setModifierInfo(new ModifierInfo(ModifierUtil.analysisModifier(modifier)));
            }
            clazzGenerator.addMethod(methodInfo);
        }
    }

    private List<AnnotationInfo> getAnno(Element ele) throws Exception {
        List<AnnotationInfo> list = new ArrayList<>();

        Elements annoList = getNodesInChildren(ele, ClassMakerKey.ANNO.KEY);
        for (Element anno : annoList) {
            if (!anno.hasAttr(ClassMakerKey.ANNO.CLASS_VAR_NAME_ATTR))
                throw new Exception("Annotation must have " + ClassMakerKey.ANNO.CLASS_VAR_NAME_ATTR);
            AnnotationInfo annotationInfo = configEngine.getAnnotationInfo(
                    varStringEngine.analysisString(
                            anno.attr(ClassMakerKey.ANNO.CLASS_VAR_NAME_ATTR)));

            Elements paramList = getNodesInChildren(anno, ClassMakerKey.ANNO.PARAMS.KEY);
            for (Element param : paramList) {
                String key = varStringEngine.analysisString(
                        param.attr(ClassMakerKey.ANNO.PARAMS.KEY_ATTR));

                if (param.hasAttr(ClassMakerKey.ANNO.PARAMS.VALUE_ATTR)) {
                    annotationInfo.addValueParam(
                            key,
                            varStringEngine.analysisString(
                                    param.attr(ClassMakerKey.ANNO.PARAMS.VALUE_ATTR)));
                    continue;
                } else if (param.hasAttr(ClassMakerKey.ANNO.PARAMS.CLASS_VALUE_ATTR)) {
                    annotationInfo.addClazzParam(key,
                            configEngine.getClazzInfoInClazzInterface(
                                    varStringEngine.analysisString(
                                            param.attr(ClassMakerKey.ANNO.PARAMS.CLASS_VALUE_ATTR))));
                    continue;
                }

                Elements valueParamList = getNodesInChildren(param, ClassMakerKey.ANNO.PARAMS.VALUE.KEY);
                List<String> stringValueList = new ArrayList<>();
                for (Element valueParam : valueParamList)
                    stringValueList.add(
                            varStringEngine.analysisString(
                                    valueParam.text()));
                if (valueParamList.size() != 0) {
                    annotationInfo.addValueParam(key,
                            stringValueList.toArray(new String[stringValueList.size()]));
                    continue;
                }

                Elements clazzParamList = getNodesInChildren(param, ClassMakerKey.ANNO.PARAMS.CLASS_VALUE.KEY);
                List<ClazzInfo> clazzValueList = new ArrayList<>();
                for (Element valueParam : clazzParamList)
                    clazzValueList.add(
                            configEngine.getClazzInfoInClazzInterface(
                                    varStringEngine.analysisString(valueParam.text())));
                if (clazzParamList.size() != 0) {
                    annotationInfo.addClazzParam(key,
                            clazzValueList.toArray(new ClazzInfo[clazzValueList.size()]));
                    continue;
                }

                throw new Exception("Annotation's param " + key + " not have any param");
            }

            list.add(annotationInfo);
        }

        return list;
    }

    private Elements getNodesInChildren(Element parentEle, String tag) {
        Elements elements = new Elements();
        if (tag == null || "".equals(tag))
            return elements;

        tag = tag.toLowerCase().trim();
        for (Element child : parentEle.children())
            if (tag.equals(child.tagName()))
                elements.add(child);

        return elements;
    }
}
