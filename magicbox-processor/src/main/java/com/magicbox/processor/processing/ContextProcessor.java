package com.magicbox.processor.processing;

import com.magicbox.BaseContext;
import com.magicbox.processor.model.Node;
import com.magicbox.processor.model.P;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class ContextProcessor implements Processor {
	private static final String REGISTER_BEAN_DEFINITIONS = "registerBeanDefinitions";

	private static final String CONTEXTS = "contexts";
	
	private JCodeModel codeModel;
	
	private Processor processor; 

	@Override
	public void process(Node node) {
		try {
			String clazzName = node.getNodeClass().getString(P.fullQulifiedName);
			JDefinedClass beanDefClass = codeModel._class(JMod.FINAL | JMod.PUBLIC, clazzName, ClassType.CLASS);
			beanDefClass._extends(BaseContext.class);
	
			JMethod contextConstructor = beanDefClass.constructor(JMod.PUBLIC);
			JVar argVar = contextConstructor.varParam(BaseContext.class, CONTEXTS);
		
			JBlock constructorBody = contextConstructor.body();
			constructorBody.invoke("super").arg(argVar);
		
			if (!node.children().isEmpty()){
				processBeanDef(beanDefClass, node);
			}
		} catch (JClassAlreadyExistsException e) {
			e.printStackTrace();
		}
	}

	private void processBeanDef(JDefinedClass clazz, Node node) {
		JMethod registerMethod = clazz.method(JMod.PUBLIC | JMod.FINAL,
				codeModel.VOID, REGISTER_BEAN_DEFINITIONS);
		JBlock block = registerMethod.body();
		
		block.invoke(JExpr._super(), REGISTER_BEAN_DEFINITIONS);
		for (Node context : node.children()) {
			processor.process(context);

			JDefinedClass beanDef = context.getNodeClass().get(BeanProcessor.BEAN_DEF);
			
			JInvocation register = block.invoke("register").arg(context.getNodeClass().getString(P.id));
			if (context.getNodeClass().isExist(P.tag)){
				register.arg(context.getNodeClass().getString(P.tag));
			}
			
			JInvocation beanDefInstancy = JExpr._new(beanDef).arg(JExpr._this());
			register.arg(beanDefInstancy);
		}
	}

	public void setCodeModel(JCodeModel codeModel) {
		this.codeModel = codeModel;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}
}
