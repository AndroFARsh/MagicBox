package com.magicbox.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

import com.magicbox.BaseContext;
import com.magicbox.processor.android.AndroidManifestFinder;
import com.magicbox.processor.model.Builder;
import com.magicbox.processor.model.ContextManager;
import com.magicbox.processor.model.NodeBuilder;
import com.magicbox.processor.model.NodeSerializer;
import com.magicbox.processor.model.Serializer;
import com.magicbox.processor.processing.BeanProcessor;
import com.magicbox.processor.processing.ContextProcessor;
import com.magicbox.processor.processing.Processor;
import com.magicbox.processor.processing.PropertyProcessor;
import com.magicbox.xml.DefaultXmlParser;
import com.magicbox.xml.XmlParser;
import com.sun.codemodel.JCodeModel;

public class ProcessorContext extends BaseContext {
	private ProcessingEnvironment processingEnv;
	private RoundEnvironment roundEnv;
	private AbstractProcessor processor;

	public ProcessorContext(AbstractProcessor processor,
			ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
		this.processor = processor;
		this.roundEnv = roundEnv;
		this.processingEnv = processingEnv;
	}

	@Override
	protected void registerBeanDefinitions() {
		super.registerBeanDefinitions();

		register(AndroidManifestFinder.class, new AbstractBeanDef() {

			@Override
			public Object create() {
				return new AndroidManifestFinder();
			}
			
			@Override
			public void assemble(Object bean) {
				AndroidManifestFinder finder = (AndroidManifestFinder) bean;
				finder.setProcessingEnvironment(processingEnv);
			}
		});
		
		register(JCodeModel.class, new AbstractBeanDef() {

			@Override
			public Object create() {
				return new JCodeModel();
			}
		});

		register(XmlParser.class, new AbstractBeanDef() {

			@Override
			public Object create() {
				return new DefaultXmlParser();
			}
		});

		register(ContextManager.class, new AbstractBeanDef() {

			@Override
			public Object create() {
				return new ContextManager();
			}

			@Override
			public void assemble(Object bean) {
				ContextManager manager = (ContextManager) bean;
				manager.setProcessingEnv(processingEnv);
				manager.setRoundEnv(roundEnv);
				manager.setBuilder(get(Builder.class));
				manager.setParser(get(XmlParser.class));
				manager.setSerializer(get(Serializer.class));
			}
		});

		register(Serializer.class, new AbstractBeanDef() {
			@Override
			public Object create() {
				return new NodeSerializer();
			}
		});

		register(Builder.class, new AbstractBeanDef() {

			@Override
			public Object create() {
				return new NodeBuilder();
			}

			@Override
			public void assemble(Object bean) {
				NodeBuilder builder = (NodeBuilder) bean;
				builder.setProcessingEnvironment(processingEnv);
				builder.setProcessor(processor);
				builder.setManifestFinder(get(AndroidManifestFinder.class));
			}
		});
		
		register(Processor.class, "context",  new AbstractBeanDef() {

			@Override
			public Object create() {
				return new ContextProcessor();
			}

			@Override
			public void assemble(Object bean) {
				ContextProcessor processor = (ContextProcessor) bean;
				processor.setCodeModel(get(JCodeModel.class));
				processor.setProcessor(get(Processor.class, "bean"));
			}
		});
		
		register(Processor.class, "bean",  new AbstractBeanDef() {

			@Override
			public Object create() {
				return new BeanProcessor();
			}

			@Override
			public void assemble(Object bean) {
				BeanProcessor processor = (BeanProcessor) bean;
				processor.setCodeModel(get(JCodeModel.class));
				processor.setProcessor(get(Processor.class, "property"));
			}
		});
		
		register(Processor.class, "property",  new AbstractBeanDef() {

			@Override
			public Object create() {
				return new PropertyProcessor();
			}

			@Override
			public void assemble(Object bean) {
				PropertyProcessor processor = (PropertyProcessor) bean;
				processor.setCodeModel(get(JCodeModel.class));
			}
		});
	}
}
