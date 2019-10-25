package com.bitcoin.juwan.handleannotations;

import com.bitcoin.juwan.annotations.BindView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import afu.org.checkerframework.checker.igj.qual.AssignsFields;

/**
 * FileName：HandleViewAnnatation
 * Create By：liumengqiang
 * Description：TODO
 */
@AutoService(Processor.class)
public class HandleViewAnnatation extends AbstractProcessor {

    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private Types typeUtils;

    private HashMap<String, AnnotationClass> annotationClassHashMap;

    static class AnnotationClass {
        static class ClassTypeName {
            static final ClassName viewFinder = ClassName.get("com.bitcoin.juwan.api3", "ViewFinder");
            static final ClassName bindView = ClassName.get("com.bitcoin.juwan.api3", "ViewBinder");
        }
        List<BindViewField> fieldList = new ArrayList<>();

        TypeElement typeElement;

        Elements elements;

        AnnotationClass(TypeElement typeElement, Elements elements) {
            this.typeElement = typeElement;
            this.elements = elements;
        }

        public void addField(BindViewField bindViewField) {
            fieldList.add(bindViewField);
        }

        JavaFile generateJavaFile() {
            MethodSpec.Builder bindViewMethodsSpecBuilder = MethodSpec.methodBuilder("bindView")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(TypeName.get(typeElement.asType()), "host")
                    .addParameter(Object.class, "o")
                    .addParameter(ClassTypeName.viewFinder, "viewFinder")
                    .returns(void.class);
            for(BindViewField field : fieldList) {
//                host.textView = (TextView)viewFinder.binView(o, );
                bindViewMethodsSpecBuilder.addStatement("host.$N = ($T)viewFinder.binView(o, $L)",
                        field.getFieldName(), ClassName.get(field.getFieldType()), field.getResId());
            }
            MethodSpec bindViewMethodsSpec = bindViewMethodsSpecBuilder.build();
            MethodSpec.Builder unBindMethodSpecBuilder = MethodSpec.methodBuilder("unBindView")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(TypeName.get(typeElement.asType()), "host")
                    .returns(void.class);
            for(BindViewField field : fieldList) {
                unBindMethodSpecBuilder.addStatement("host.$N = null", field.getFieldName());
            }
            MethodSpec unBindMethodSpec = unBindMethodSpecBuilder.build();
            TypeSpec typeSpec = TypeSpec.classBuilder(typeElement.getSimpleName() + "$BindView")
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(bindViewMethodsSpec)
                    .addMethod(unBindMethodSpec)
                    .addSuperinterface(ParameterizedTypeName.get(ClassTypeName.bindView, TypeName.get(typeElement.asType())))
                    .build();
            String packName = elements.getPackageOf(typeElement).getQualifiedName().toString();
            System.out.println("asType: " + typeElement.asType());
            System.out.println("packageInfo: " + elements.getPackageOf(typeElement).getQualifiedName().toString());

            return JavaFile.builder(packName, typeSpec).build();
        }
    }

    static class BindViewField {
        VariableElement annotationField;

        int resId;

        BindViewField(Element element) {
            if(element.getKind() != ElementKind.FIELD) {
                throw new IllegalArgumentException(BindView.class.getSimpleName() + "must used field!");
            }
            this.annotationField = (VariableElement) element;
            BindView bindView = this.annotationField.getAnnotation(BindView.class);
            resId = bindView.value();
            if(resId < 0) {
                throw new IllegalArgumentException(annotationField.getSimpleName() + " resId is not valid!");
            }
        }

        public int getResId() {
            return resId;
        }

        public String getFieldName() {
            return annotationField.getSimpleName().toString();
        }

        public TypeMirror getFieldType() {
            System.out.println("Field: " + annotationField.asType());
            return annotationField.asType();
        }
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        typeUtils = processingEnvironment.getTypeUtils();
        annotationClassHashMap = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        annotationClassHashMap.clear();
        for(Element element : roundEnvironment.getElementsAnnotatedWith(BindView.class)) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            AnnotationClass annotationClass = getAnnotationClass(typeElement);
            BindViewField bindViewField = new BindViewField(element);
            annotationClass.addField(bindViewField);
        }
        for(AnnotationClass annotationClass : annotationClassHashMap.values()) {
            try {
                annotationClass.generateJavaFile().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private AnnotationClass getAnnotationClass(TypeElement typeElement) {
        String activityName = typeElement.getQualifiedName().toString();
        AnnotationClass annotationClass = annotationClassHashMap.get(activityName);
        if(annotationClass == null) {
            annotationClass = new AnnotationClass(typeElement, elementUtils);
            annotationClassHashMap.put(activityName, annotationClass);
        }
        return annotationClass;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add(BindView.class.getCanonicalName());
        return linkedHashSet;
    }
}
