package ru.alexeyp.codegen_processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import ru.alexeyp.codegen_annotations.TestAnnotation;

@SupportedAnnotationTypes({"ru.alexeyp.codegen_annotations.TestAnnotation"})
public class CodegenProcessor extends AbstractProcessor {

	private static final char CHAR_DOT = '.';

	private Filer filer;
	private Messager messager;
	private Elements elements;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		filer = processingEnv.getFiler();
		messager = processingEnv.getMessager();
		elements = processingEnv.getElementUtils();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		for (Element element : roundEnv.getElementsAnnotatedWith(TestAnnotation.class)) {
			if (element.getKind() != ElementKind.FIELD) {
				messager.printMessage(Diagnostic.Kind.ERROR, "Only FIELD");
				return true;
			} else {
				VariableElement variableElement = (VariableElement) element;
				TypeSpec.Builder saverClass = TypeSpec
					.classBuilder("Saver")
					.addModifiers(Modifier.PUBLIC);

				MethodSpec constructor = MethodSpec.constructorBuilder()
					.addModifiers(Modifier.PUBLIC)
					.addParameter(TypeName.get(elements.getTypeElement("android.os.Bundle").asType()), "bundle")
					.addCode(
						"if (bundle == null) {\n"
						+ "    bundle = new Bundle();\n"
						+ "}\n"
					)
					.build();
				saverClass.addMethod(constructor);



				JavaFile javaFile = JavaFile.builder("ru.alexeyp.codegen.synthetic", saverClass.build()).build();
				try {
					javaFile.writeTo(filer);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			}
		}
		return false;
	}

	private void manualWrite() {
		try {
			Writer writer = filer.createSourceFile("ru.alexeyp.codegen.synthetic.MyFirstClass").openWriter();
			writer.write("package ru.alexeyp.codegen.synthetic;\n\n");
			writer.write("public class MyFirstClass {\n");
			writer.write("	final int a = 2;\n");
			writer.write("	final int b;\n\n");
			writer.write("	public MyFirstClass() {\n");
			writer.write("		this.b = 4;\n");
			writer.write("	}\n");
			writer.write("}\n");

			//writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

}
