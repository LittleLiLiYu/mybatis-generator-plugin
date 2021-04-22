package com.kuangyu.core.mybatis.generator;

import com.alibaba.fastjson.annotation.JSONField;
import com.kuangyu.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.config.PropertyRegistry;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * mapper插件
 *
 * @author
 */
public class MultiDbPaginationPlugin extends PluginAdapter {

    protected final static String MAPPER_XML_FILE_POSTFIX = "Ext";

    protected final static String MAPPER_XML_FILE_ENDING_NAME = "Mapper.xml";

    protected final static String PARAM_NAME_RECORD = "record";
    protected final static String ANNOTATION_RECORD = "@Param(\"record\")";

    protected final static String PARAM_NAME_EXAMPLE = "example";
    protected final static String ANNOTATION_EXAMPLE = "@Param(\"example\")";

    protected static FullyQualifiedJavaType mapperJavaType = new FullyQualifiedJavaType(Mapper.class.getName());

    /**
     * 添删改Document的sql语句及属性
     * @param document
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        XmlElement parentElement = document.getRootElement();

        moveDocumentInsertSql(parentElement);

        sqlMapInsertSelectiveForMutilDatabaseGenerated(parentElement, introspectedTable);

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    /**
     * 移除 insert
     * @param parentElement
     */
    protected void moveDocumentInsertSql(XmlElement parentElement) {
        XmlElement insertElement = null;
        for (Element element : parentElement.getElements()) {
            XmlElement xmlElement = (XmlElement) element;
            if (xmlElement.getName().equals("insert")) {
                for (Attribute attribute : xmlElement.getAttributes()) {
                    if (attribute.getValue().equals("insert")) {
                        insertElement = xmlElement;
                        break;
                    }
                }
            }
        }
        parentElement.getElements().remove(insertElement);
    }

    /**
     * InsertSelective添加自增ID获取
     * @param parentElement
     * @param introspectedTable
     */
    protected void sqlMapInsertSelectiveForMutilDatabaseGenerated(XmlElement parentElement,
                                                                  IntrospectedTable introspectedTable) {
        XmlElement oldElement = null;
        XmlElement mysqlElement = null;
//        XmlElement oracleElement = null;
        for (Element element : parentElement.getElements()) {
            XmlElement xmlElement = (XmlElement) element;
            if (xmlElement.getName().equals("insert")) {
                for (Attribute attribute : xmlElement.getAttributes()) {
                    if (attribute.getValue().equals("insertSelective")) {
                        oldElement = xmlElement;
                        mysqlElement = (XmlElement) this.copyElement(xmlElement);
//                        oracleElement = (XmlElement) this.copyElement(xmlElement);
                        break;
                    }
                }
            }
        }
        parentElement.getElements().remove(oldElement);
        sqlInsertSelectiveGenerated(parentElement, "mysql", mysqlElement, introspectedTable);
    }

    protected Element copyElement(Element element) {
        if (element instanceof XmlElement) {
            XmlElement xmlElement = (XmlElement) element;
            String name = xmlElement.getName();
            List<Attribute> aList = xmlElement.getAttributes();
            List<Element> eList = xmlElement.getElements();
            XmlElement newElement = new XmlElement(name);
            if (aList != null) {
                for (int i = 0; i < aList.size(); i++) {
                    newElement.addAttribute(new Attribute(aList.get(i).getName(), aList.get(i).getValue()));
                }
            }
            if (eList != null) {
                for (int i = 0; i < eList.size(); i++) {
                    Element e = this.copyElement(eList.get(i));
                    newElement.addElement(e);
                }
            }
            return newElement;
        } else if (element instanceof TextElement) {
            TextElement textElement = (TextElement) element;
            TextElement newElement = new TextElement(textElement.getContent());
            return newElement;
        }

        return null;
    }

    public void sqlInsertSelectiveGenerated(XmlElement parentElement, String dbType, XmlElement element,
                                            IntrospectedTable introspectedTable) {
        List<Element> elements = element.getElements();
        parentElement.addElement(element);
        Element fixE = elements.get(5);

        List<IntrospectedColumn> list = introspectedTable.getPrimaryKeyColumns();
        boolean identity = false;
        String javaProperty = null;
        for (IntrospectedColumn column : list
             ) {
            if (column.isIdentity()) {
                identity = true;
                javaProperty = column.getJavaProperty();
            }
        }
        // mysql添加属性
        if (dbType != null && dbType.equals("mysql")) {
            if (identity) {
                element.addAttribute(new Attribute("useGeneratedKeys", "true"));
                element.addAttribute(new Attribute("keyProperty", javaProperty));
            }
            // 有selectKey,那就是oracle生成的，mysql不需要
            if ((fixE instanceof XmlElement) && ((XmlElement) fixE).getName().equals("selectKey")) {
                // 添加oracle的test判断
                elements.remove(5);
            }
        }
    }

    @Override
    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        element.getAttributes().remove(2);
        element.addAttribute(new Attribute("parameterType", java.io.Serializable.class.getName()));
        Element element1 = element.getElements().get(8);
        if (element1 instanceof TextElement) {
            TextElement whereElement = (TextElement) element.getElements().remove(8);
            IntrospectedColumn column = introspectedTable.getPrimaryKeyColumns().get(0);
            TextElement where = new TextElement(whereElement.getContent().replace(column.getJavaProperty(), "id"));
            element.addElement(where);
        }
        return super.sqlMapSelectByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        element.getAttributes().remove(1);
        element.addAttribute(new Attribute("parameterType", java.io.Serializable.class.getName()));
        Element element1 = element.getElements().get(6);
        if (element1 instanceof TextElement) {
            TextElement whereElement = (TextElement) element.getElements().remove(6);
            IntrospectedColumn column = introspectedTable.getPrimaryKeyColumns().get(0);
            TextElement where = new TextElement(whereElement.getContent().replace(column.getJavaProperty(), "id"));
            element.addElement(where);
        }
        return super.sqlMapDeleteByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
//        添加注释
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" *");
        topLevelClass.addJavaDocLine(" * @author auto");
        topLevelClass.addJavaDocLine(" * @date " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        topLevelClass.addJavaDocLine(" */");
//        添加JSONField注解 import
        FullyQualifiedJavaType dateType = new FullyQualifiedJavaType(Date.class.getName());
        topLevelClass.getImportedTypes().forEach(fullyQualifiedJavaType -> {
            if (dateType.getFullyQualifiedName().equals(fullyQualifiedJavaType.getFullyQualifiedName())) {
                topLevelClass.addImportedType(JSONField.class.getName());
            }
        });
//        对 java.util.Date类型字段添加 JSONField注解
        topLevelClass.getFields().stream().forEach(field -> {
            if (dateType.getFullyQualifiedName().equals(field.getType().getFullyQualifiedName())) {
                field.addAnnotation("@JSONField(format = \"yyyy-MM-dd HH:mm:ss\")");
            }
//            清除JavaDoc注释
            field.getJavaDocLines().clear();
        });
//        清除JavaDoc注释
        topLevelClass.getMethods().stream().forEach(method -> method.getJavaDocLines().clear());
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
//        添加注释
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" *");
        topLevelClass.addJavaDocLine(" * @author auto");
        topLevelClass.addJavaDocLine(" * @date " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        topLevelClass.addJavaDocLine(" */");
//        清除JavaDoc注释
        topLevelClass.getFields().stream().forEach(field -> field.getJavaDocLines().clear());
        topLevelClass.getMethods().stream().forEach(method -> method.getJavaDocLines().clear());
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String mapperType = introspectedTable.getMyBatis3JavaMapperType();
//        已生成 则不覆盖
        if (isExistExtFile(context.getJavaClientGeneratorConfiguration().getTargetProject(),
                           mapperType.substring(0, mapperType.lastIndexOf(".")),
                           mapperType.substring(mapperType.lastIndexOf(".") + 1) + ".java")) {
            return false;
        }
//        添加父接口BaseMapper，添加引用类，添加对应的import
        FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        FullyQualifiedJavaType exampleType = new FullyQualifiedJavaType(introspectedTable.getExampleType());
        FullyQualifiedJavaType baseDoType = new FullyQualifiedJavaType(BaseMapper.class.getName()  + "<" + entityType.getShortName() + ", " + exampleType.getShortName() + ">");
        interfaze.addImportedType(baseDoType);
        interfaze.addSuperInterface(baseDoType);
        interfaze.addImportedType(entityType);
        interfaze.addImportedType(exampleType);
//        添加Mapper注解及import
        interfaze.addImportedType(mapperJavaType);
        interfaze.addAnnotation("@Mapper");

//        添加注释
        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine(" *");
        interfaze.addJavaDocLine(" * @author auto");
        interfaze.addJavaDocLine(" * @date " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        interfaze.addJavaDocLine(" */");

//        BaseMapper已集成默认接口
        interfaze.getMethods().clear();
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    @Override
    public boolean sqlMapCountByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {

        element.getAttributes().remove(2);
        element.addAttribute(new Attribute("resultType", Long.class.getName()));
        return super.sqlMapCountByExampleElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element,
                                                                        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
        String xmlName = introspectedTable.getMyBatis3XmlMapperFileName();

        String fileNameExt = xmlName.replace(MAPPER_XML_FILE_ENDING_NAME, MAPPER_XML_FILE_POSTFIX + MAPPER_XML_FILE_ENDING_NAME);

//        String targetPackage =
//                context.getSqlMapGeneratorConfiguration().getTargetPackage() + "." + xmlName.substring(0,
//                                                                                                       xmlName.lastIndexOf(MAPPER_XML_FILE_ENDING_NAME)).toLowerCase(Locale.ROOT);
        String targetPackage =
                context.getSqlMapGeneratorConfiguration().getTargetPackage();
//        判断扩展xml文件是否存在，存在则不生成扩展xml，指覆盖默认xml
        if (isExistExtFile(context.getSqlMapGeneratorConfiguration().getTargetProject(),
                           targetPackage, fileNameExt)) {
            return super.contextGenerateAdditionalXmlFiles(introspectedTable);
        }

        Document document = new Document(XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID,
                                         XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);

        XmlElement root = new XmlElement("mapper");
        document.setRootElement(root);
        String namespace = introspectedTable.getMyBatis3SqlMapNamespace();
        root.addAttribute(new Attribute("namespace", namespace));

        GeneratedXmlFile gxf = new GeneratedXmlFile(document, fileNameExt,
                                                    targetPackage,
                                                    context.getSqlMapGeneratorConfiguration().getTargetProject(), false,
                                                    context.getXmlFormatter());

        List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>(1);
        answer.add(gxf);

        return answer;
    }

//    @Override
//    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
//
//        List<GeneratedJavaFile> generatedJavaFiles = new ArrayList<GeneratedJavaFile>(1);
//
//        GeneratedJavaFile baseMapper = generateBaseMapperJava(introspectedTable);
//        if (baseMapper != null) {
//            generatedJavaFiles.add(baseMapper);
//        }
//        return generatedJavaFiles;
//    }

//    发现插件生成不支持泛型
    protected GeneratedJavaFile generateBaseMapperJava(IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType typeT = new FullyQualifiedJavaType("T");
        FullyQualifiedJavaType typeE = new FullyQualifiedJavaType("E");
        FullyQualifiedJavaType type =
                new FullyQualifiedJavaType(context.getJavaClientGeneratorConfiguration().getTargetPackage()+
                                                   "." + BaseMapper.class.getSimpleName());
        type.addTypeArgument(typeT);
        type.addTypeArgument(typeE);
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        context.getCommentGenerator().addJavaFileComment(interfaze);

        FullyQualifiedJavaType paramJavaType = new FullyQualifiedJavaType(Param.class.getName());
        interfaze.addImportedType(paramJavaType);
        FullyQualifiedJavaType listType = new FullyQualifiedJavaType(List.class.getName());
        interfaze.addImportedType(listType);


        Parameter paramT = new Parameter(typeT, PARAM_NAME_RECORD);
        Parameter paramE = new Parameter(typeE, PARAM_NAME_EXAMPLE);
        Parameter parameterT = new Parameter(typeT, PARAM_NAME_RECORD, ANNOTATION_RECORD);
        Parameter parameterE = new Parameter(typeE, PARAM_NAME_EXAMPLE, ANNOTATION_EXAMPLE);

        createInterfaceMethod(interfaze, new FullyQualifiedJavaType(Integer.class.getName()), "insertSelective",
                              paramT);
        createInterfaceMethod(interfaze, new FullyQualifiedJavaType(Integer.class.getName()),
                              "updateByExampleSelective", parameterT, parameterE);
        createInterfaceMethod(interfaze, new FullyQualifiedJavaType(Integer.class.getName()),
                              "updateByPrimaryKeySelective", paramT);
        createInterfaceMethod(interfaze, new FullyQualifiedJavaType(Integer.class.getName()), "deleteByPrimaryKey",
                              paramT);
        createInterfaceMethod(interfaze, new FullyQualifiedJavaType(Integer.class.getName()), "deleteByExample",
                              paramE);
        createInterfaceMethod(interfaze, new FullyQualifiedJavaType(Integer.class.getName()), "countByExample",
                              paramE);
        createInterfaceMethod(interfaze, typeT, "selectByPrimaryKey",
                              paramT);
        createInterfaceMethod(interfaze, new FullyQualifiedJavaType(List.class.getName() + "<T>"), "deleteByExample",
                              paramE);


        if (isExistExtFile(context.getJavaClientGeneratorConfiguration().getTargetProject(),
                           context.getJavaClientGeneratorConfiguration().getTargetPackage(),
                           BaseMapper.class.getName() + ".java")) {
            return null;
        }
        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(interfaze,
                                                                    context.getJavaClientGeneratorConfiguration().getTargetProject(),
                                                                    context.getProperty(
                                                                            PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                                                                    context.getJavaFormatter());
        return generatedJavaFile;
    }

    private void createInterfaceMethod(Interface interfaze, FullyQualifiedJavaType returnType, String methodName,
                                       Parameter... params) {
        Method method = new Method();
        method.setReturnType(returnType);
        method.setName(methodName);
        method.addJavaDocLine("test");
        if (params != null || params.length > 0) {
            for (Parameter param : params
                 ) {
                method.addParameter(param);
            }
        }
        interfaze.addMethod(method);
    }

    protected boolean isExistExtFile(String targetProject, String targetPackage, String fileName) {

        File project = new File(targetProject);
        if (!project.isDirectory()) {
            return true;
        }

        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(targetPackage, ".");
        while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
            sb.append(File.separatorChar);
        }

        File directory = new File(project, sb.toString());
        if (!directory.isDirectory()) {
            boolean rc = directory.mkdirs();
            if (!rc) {
                return true;
            }
        }

        File testFile = new File(directory, fileName);
        if (testFile.exists()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}
