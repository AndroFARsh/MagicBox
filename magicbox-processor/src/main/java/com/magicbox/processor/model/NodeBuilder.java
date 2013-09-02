package com.magicbox.processor.model;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.swing.text.html.Option;

import com.magicbox.Initializable;
import com.magicbox.ProgressCallback;
import com.magicbox.annotation.Alias;
import com.magicbox.annotation.Bean;
import com.magicbox.annotation.Context;
import com.magicbox.annotation.Property;
import com.magicbox.processor.Constants;
import com.magicbox.processor.android.AndroidManifest;
import com.magicbox.processor.android.AndroidManifestFinder;
import com.magicbox.processor.model.Node.NodeType;
import com.magicbox.processor.model.NodeClass.ClassComposer;
import com.magicbox.processor.model.NodeClass.NodeException;
import com.magicbox.processor.xml.dtd.A;
import com.magicbox.processor.xml.dtd.T;
import com.magicbox.xml.XmlElement;

public class NodeBuilder implements Builder, Initializable {
	private ProcessingEnvironment processingEnv;
	private AbstractProcessor processor;
	private AndroidManifestFinder manifestFinder;
	private AndroidManifest maifest;

	@Override
	public void initialize(ProgressCallback callback) throws InterruptedException {
		maifest = manifestFinder.extractAndroidManifest().getOr(AndroidManifest.createLibraryManifest(Constants.DEFAULT_PACKAGE));
	}
	
	@Override
	public BaseNode build(XmlElement element) {
		switch (element.getId()) {
		case T.Beans:
			return mergeElementToNode(new ContextNode(), element);
		case T.Bean:
			return mergeElementToNode(new BeanNode(), element);
		case T.Alias:
			return mergeElementToNode(new AliasNode(), element);
		case T.Property:
			return mergeElementToNode(new PropertyNode(), element);
		case T.Contexts:
			return mergeElementToNode(new RootNode(), element);
		default:
			return null;
		}
	}

	private boolean isAnnotationSupported(Element e) {
		for (AnnotationMirror m : e.getAnnotationMirrors()) {
			for (String annotation : processor.getSupportedAnnotationTypes()) {
				if (annotation.endsWith("*")) {
					annotation = annotation.substring(0,
							annotation.lastIndexOf("."));

					if (m.getAnnotationType().toString().startsWith(annotation)) {
						return true;
					}
				} else if (annotation.endsWith(m.toString())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Node build(Set<? extends TypeElement> elements) {
		BaseNode node = new RootNode();
		for (TypeElement e : elements) {
			if (isAnnotationSupported(e)) {
				node.attachChild(build(e.getAnnotation(Context.class), e,
						NodeType.Context));
			}
		}
		return node;
	}

	public BaseNode build(Annotation a, Element element, NodeType type) {
		switch (type) {
		case Context: {
			final Context annotation = (Context) a;
			final ContextNode node = new ContextNode();

			ClassComposer clazzComposer = node.getNodeClass().classComposer();
			applyAttributeToClass(clazzComposer, A.Output,
					(annotation != null) ? annotation.value()
							: maifest.getApplicationPackage()+"."+Constants.DEFAULT_CONTEXT_CLASS_NAME);
			clazzComposer.compose();
			node.attachChild(build(element.getAnnotation(Bean.class), element,
					NodeType.Bean));
			return node;
		}
		case Bean: {
			final Bean annotation = (Bean) a;
			if (annotation == null) {
				return null;
			}

			BeanNode node = new BeanNode();
			ClassComposer clazzComposer = node.getNodeClass().classComposer();
			String clazz = ((TypeElement) element).getQualifiedName()
					.toString();
			applyAttributeToClass(clazzComposer, A.Class, clazz);
			applyAttributeToClass(clazzComposer, A.Id, !annotation.id()
					.isEmpty() ? annotation.id() : clazz);

			if (!annotation.tag().isEmpty()) {
				applyAttributeToClass(clazzComposer, A.Tag, annotation.tag());
			}

			clazzComposer.compose();

			for (Element e : findAllAnsessorElement((TypeElement) element)) {
				node.attachChild(build(e.getAnnotation(Property.class), e,
						NodeType.Property));
			}
			for (Alias alias : annotation.alias()) {
				node.attachChild(build(alias, element, NodeType.Alias));
			}
			return node;
		}
		case Alias: {
			final Alias annotation = (Alias) a;
			if (annotation == null) {
				return null;
			}

			AliasNode node = new AliasNode();
			ClassComposer clazzComposer = node.getNodeClass().classComposer();
			applyAttributeToClass(clazzComposer, A.Id, annotation.id());
			if (!annotation.tag().isEmpty()) {
				applyAttributeToClass(clazzComposer, A.Tag, annotation.tag());
			}
			clazzComposer.compose();
			return node;
		}
		case Property: {
			final Property annotation = element.getAnnotation(Property.class);
			if (annotation == null) {
				return null;
			}

			PropertyNode node = new PropertyNode();
			ClassComposer clazzComposer = node.getNodeClass().classComposer();
			if (!annotation.taggedBy().isEmpty()) {
				applyAttributeToClass(clazzComposer, A.TaggedBy,
						annotation.taggedBy());
			} else {
				applyAttributeToClass(clazzComposer, A.Ref, (!annotation.id()
						.isEmpty()) ? annotation.id() : elemenToId(element));

				if (!annotation.tag().isEmpty()) {
					applyAttributeToClass(clazzComposer, A.RefTag,
							annotation.tag());
				}
			}
			applyAttributeToClass(clazzComposer, A.Name, elemenToName(element));
			clazzComposer.set(P.element, element);
			clazzComposer.compose();
			return node;
		}
		default:
			break;
		}
		return null;
	}

	private void findAllAnsessorElement(Set<Element> elements,
			TypeElement element) {
		for (Element enclosedElement : element.getEnclosedElements()) {
			ElementKind enclosedKind = enclosedElement.getKind();
			switch (enclosedKind) {
			case METHOD:
			case FIELD:
			case PARAMETER:
				if (isAnnotationSupported(enclosedElement)) {
					elements.add(enclosedElement);
				}
				break;
			default:
				break;
			}
		}

		TypeElement typeElement = element;
		TypeMirror ancestorTypeMirror = typeElement.getSuperclass();
		if (ancestorTypeMirror.getKind() != TypeKind.NONE
				&& ancestorTypeMirror instanceof DeclaredType) {
			DeclaredType ancestorDeclaredType = (DeclaredType) ancestorTypeMirror;
			Element ancestorElement = ancestorDeclaredType.asElement();
			if (ancestorElement instanceof TypeElement) {
				findAllAnsessorElement(elements, (TypeElement) ancestorElement);
			}
		}
	}

	private Set<Element> findAllAnsessorElement(TypeElement e) {
		Set<Element> set = new HashSet<Element>();
		findAllAnsessorElement(set, e);
		return set;
	}

	private String elemenToName(Element element) {
		String name = null;
		while (element.getKind() != ElementKind.PACKAGE) {
			if (name == null) {
				name = element.toString();
			} else {
				name = element.toString() + Constants.SEPARATOR + name;
			}
			element = element.getEnclosingElement();
			if (element == null) {
				break;
			}
		}
		return name;
	}

	private String elemenToId(Element element) {
		switch (element.getKind()) {
		case FIELD:
		case PARAMETER:
			return element.asType().toString();
		case METHOD: {
			ExecutableElement execElement = (ExecutableElement) element;
			List<? extends VariableElement> parameters = execElement
					.getParameters();
			if (parameters.size() == 1) {
				return parameters.get(0).asType().toString();
			}
		}
		default:
			break;
		}
		return null;
	}

	private BaseNode mergeElementToNode(BaseNode node, XmlElement element) {

		final ClassComposer clazzComposer = node.getNodeClass().classComposer();

		applyAttributesToClass(element, clazzComposer);

		clazzComposer.compose();

		for (final XmlElement child : element.children()) {
			final BaseNode childNode = build(child);
			if (childNode != null) {
				node.attachChild(childNode);
			}
		}

		return node;
	}

	private void applyAttributesToClass(XmlElement element,
			final ClassComposer clazzComposer) {
		for (final int attrId : element.getAttrIds()) {
			final String value = element.getAttrById(attrId);
			if (value != null) {
				applyAttributeToClass(clazzComposer, attrId, value);
			}
		}
	}

	private void applyAttributeToClass(ClassComposer clazzComposer, int id,
			String value) {
		try {
			switch (id) {
			case A.Output:
				clazzComposer.setString(P.fullQulifiedName, value);
				clazzComposer.setString(P.packageName, extractPackage(value));
				break;
			case A.Class:
				final TypeElement typeElement = processingEnv.getElementUtils()
						.getTypeElement(value);

				clazzComposer.setString(P.packageName, extractPackage(value));
				clazzComposer.setString(P.simpleName, extractSimpleName(value));
				clazzComposer.setString(P.fullQulifiedName, value);
				clazzComposer.set(P.element, typeElement);
				break;
			case A.Id:
				clazzComposer.setString(P.id, value);
				break;
			case A.Ref:
				clazzComposer.setString(P.ref, value);
				break;
			case A.Tag:
				clazzComposer.setString(P.tag, value);
				break;
			case A.RefTag:
				clazzComposer.setString(P.refTag, value);
				break;
			case A.TaggedBy:
				clazzComposer.setString(P.taggedBy, value);
				break;
			case A.Name:
				final String[] tokens = value.split(Constants.SEPARATOR);
				if (tokens.length != 2) {
					return;
				}
				Element element = findElement(tokens[1], processingEnv
						.getElementUtils().getTypeElement(tokens[0]));
				if (element == null) {
					return;
				}
				clazzComposer.set(P.name, value);
				clazzComposer.set(P.element, element);
				break;
			}
		} catch (final NodeException exception) {
		}
	}

	private Element findElement(String name, TypeElement element) {
		if (element == null || name == null || name.isEmpty()) {
			return null;
		}

		for (Element e : element.getEnclosedElements()) {
			switch (e.getKind()) {
			case FIELD:
			case METHOD:
				if (e.toString().equals(name)
						&& e.getAnnotation(Property.class) != null) {
					return e;
				}
				break;
			default:
				break;
			}
		}

		final TypeMirror superTypeMirror = element.getSuperclass();
		if (superTypeMirror.getKind() == TypeKind.NONE
				&& superTypeMirror instanceof DeclaredType) {
			DeclaredType superDeclaredType = (DeclaredType) superTypeMirror;
			return findElement(name,
					(TypeElement) superDeclaredType.asElement());
		}
		return null;
	}

	private String extractSimpleName(String value) {
		if (value == null || value.isEmpty()) {
			return "";
		}
		int index = value.lastIndexOf(".");
		return index != -1 ? value.substring(index) : "";
	}

	private String extractPackage(String value) {
		if (value == null || value.isEmpty()) {
			return "";
		}
		int index = value.lastIndexOf(".");
		return index != -1 ? value.substring(0, index) : "";
	}

	public void setProcessingEnvironment(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
	}

	public void setProcessor(AbstractProcessor processor) {
		this.processor = processor;
	}

	public void setManifestFinder(AndroidManifestFinder manifestFinder) {
		this.manifestFinder = manifestFinder;
	}
}
