package com.magicbox.processor.processing;

import com.magicbox.BaseContext;
import com.magicbox.BaseContext.AbstractBeanDef;
import com.magicbox.processor.Constants;
import com.magicbox.processor.model.Node;
import com.magicbox.processor.model.P;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class BeanProcessor implements Processor {
	public static final int BEAN_DEF = 50;

	private static final String ARG = "arg";
	private static final String BEAN = "bean";
	private static final String CONTEXT = "context";

	private JCodeModel codeModel;
	private Processor processor;

	@Override
	public void process(Node node) {
		try {
			String clazzName = new StringBuilder()
					.append(node.parent().getNodeClass()
							.getString(P.packageName))
					.append(node.getNodeClass().getString(P.simpleName))
					.append(Constants.SUFFIX).toString();

			JDefinedClass beanDefClass = codeModel._class(JMod.FINAL,
					clazzName, ClassType.CLASS);
			beanDefClass._extends(AbstractBeanDef.class);

			node.getNodeClass().classComposer().set(BEAN_DEF, beanDefClass)
					.compose();

			JFieldVar contextField = beanDefClass.field(JMod.FINAL
					| JMod.PRIVATE, BaseContext.class, CONTEXT);

			JMethod beanDefConstructor = beanDefClass.constructor(JMod.PUBLIC);
			JVar argVar = beanDefConstructor.param(BaseContext.class, CONTEXT);
			beanDefConstructor.body().assign(JExpr._this().ref(contextField),
					JExpr.ref(argVar.name()));

			JType beanType = codeModel.parseType(node.getNodeClass().getString(
					P.fullQulifiedName));

			JMethod create = beanDefClass.method(JMod.PUBLIC | JMod.FINAL,
					Object.class, "create");
			create.annotate(Override.class);
			
			JTryBlock tyBlock = create.body()._try();
			tyBlock.body()._return(JExpr._new(beanType));
			JCatchBlock catchBlock = tyBlock._catch((JClass)codeModel.parseType(Throwable.class.getCanonicalName()));
			catchBlock.body()._throw(JExpr._new(codeModel.parseType(RuntimeException.class.getCanonicalName())));

			if (!node.children().isEmpty()) {
				processProperties(beanDefClass, beanType, contextField, node);
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (JClassAlreadyExistsException e) {
			e.printStackTrace();
		}
	}

	private void processProperties(JDefinedClass clazz, JType beanType,
			JFieldVar contextField, Node node) {
		JMethod assemble = clazz.method(JMod.PUBLIC | JMod.FINAL,
				codeModel.VOID, "assemble");
		assemble.annotate(Override.class);
		JVar arg = assemble.param(Object.class, ARG);
		JBlock block = assemble.body();
		JVar bean = block.decl(beanType, BEAN);
		block.assign(bean, JExpr.cast(beanType, arg));
		for (Node child : node.children()) {
			switch (child.type()) {
			case Alias:
				break;
			case Property:
				child.getNodeClass().classComposer()
						.set(PropertyProcessor.CONTEXT, contextField)
						.set(PropertyProcessor.BLOCK, block)
						.set(PropertyProcessor.BEAN, bean).compose();

				processor.process(child);
				break;
			default:
				break;
			}
		}
	}

	public void setCodeModel(JCodeModel codeModel) {
		this.codeModel = codeModel;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}
}
