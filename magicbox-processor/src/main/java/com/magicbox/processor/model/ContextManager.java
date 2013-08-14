package com.magicbox.processor.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.magicbox.Disposable;
import com.magicbox.Initializable;
import com.magicbox.ProgressCallback;
import com.magicbox.processor.Constants;
import com.magicbox.processor.xml.dtd.ContextDtd;
import com.magicbox.xml.XmlElement;
import com.magicbox.xml.XmlParser;
import com.magicbox.xml.XmlParser.XmlParserException;
import com.magicbox.xml.dtd.DtdMappings;

public class ContextManager implements Initializable, Disposable {
	private ProcessingEnvironment processingEnv;
	private RoundEnvironment roundEnv;
	private XmlParser parser;
	private Builder builder;
	private Serializer serializer;

	private RootNode rootNode;

	public Node root() {
		return rootNode;
	}

	@Override
	public void initialize(final ProgressCallback arg0)
			throws InterruptedException {
		rootNode = new RootNode();
		
		rootNode.merge(processXml());
		rootNode.merge(processElement());
	}

	private RootNode processElement() {
		Set<TypeElement> set = new HashSet<TypeElement>();
		for (Element e : roundEnv.getRootElements()){
			if (e instanceof TypeElement){
				set.add((TypeElement)e);
			}
		}
		return (RootNode) builder.build(set);
		
	}

	
	
	private RootNode processXml() {
		try {
			final FileObject props = processingEnv.getFiler().getResource(
					StandardLocation.SOURCE_OUTPUT, Constants.PROPS_PATH,
					Constants.PROPS_NAME);

			return (RootNode) builder.build(parse(props,
					ContextDtd.INSTANCE));
		} catch (final IOException e) {
		}
		return null;
	}

	@Override
	public void dispose(ProgressCallback arg0) throws InterruptedException {
		try {
			final FileObject props = processingEnv.getFiler().createResource(
					StandardLocation.SOURCE_OUTPUT, Constants.PROPS_PATH,
					Constants.PROPS_NAME);

			serializer.serialize(rootNode, props);

		} catch (IOException e) {
		}
	}

	private XmlElement parse(final FileObject property,
			final DtdMappings dtdMappings) {
		InputStream stream = null;
		try {
			stream = property.openInputStream();
			return parser.load(stream, dtdMappings);
		} catch (final IOException e) {
		} catch (final XmlParserException e) {
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (final IOException e) {
				}
			}
		}
		return XmlElement.EMPTY;
	}

	public void setProcessingEnv(final ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
	}

	public void setParser(final XmlParser parser) {
		this.parser = parser;
	}

	public void setRoundEnv(RoundEnvironment roundEnv) {
		this.roundEnv = roundEnv;
	}

	public void setBuilder(Builder builder) {
		this.builder = builder;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}
}
