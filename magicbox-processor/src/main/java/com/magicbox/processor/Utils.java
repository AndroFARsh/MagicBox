package com.magicbox.processor;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.magicbox.annotation.Property;

public class Utils {

	public static Set<? extends Element> getProperty(final TypeElement parent,
			RoundEnvironment roundEnv) {
		final Set<? extends Element> rootSet = roundEnv
				.getElementsAnnotatedWith(Property.class);

		final Set<Element> set = new HashSet<Element>();
		extractAnnotatedElement(set, rootSet, parent);
		return set;
	}

	private static void extractAnnotatedElement(Set<Element> resElements,
			Set<? extends Element> elements, Element element) {
		for (Element e : elements) {
			if (e.equals(element)) {
				resElements.add(element);
			}
		}

		if (element.getKind() == ElementKind.CLASS) {
			for (Element e : element.getEnclosedElements()) {
				extractAnnotatedElement(resElements, elements, e);
			}

			TypeMirror superTypeMirror = ((TypeElement) element)
					.getSuperclass();
			if (superTypeMirror.getKind() != TypeKind.NONE
					&& superTypeMirror instanceof DeclaredType) {
				DeclaredType superDeclaredType = (DeclaredType) superTypeMirror;
				extractAnnotatedElement(resElements, elements,
						superDeclaredType.asElement());
			}
		}
	}

}
