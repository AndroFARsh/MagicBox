package com.magicbox.processor.processing;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import com.magicbox.processor.model.Node;
import com.magicbox.processor.model.NodeClass;
import com.magicbox.processor.model.P;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class PropertyProcessor implements Processor {
	private static final String GET = "get";
	private static final String GET_TAGGED = "getTagged";

	public static final int CONTEXT = 100;
	public static final int BLOCK = 101;
	public static final int BEAN = 102;

	private JCodeModel codeModel;

	@Override
	public void process(final Node node) {
		try {
			final NodeClass nodeClass = node.getNodeClass();
			final JFieldVar contextField = nodeClass.get(CONTEXT);
			final JBlock block = nodeClass.get(BLOCK);
			final JVar bean = nodeClass.get(BEAN);

			final JInvocation value;
			if (nodeClass.isExist(P.taggedBy)) {
				value = JExpr.invoke(contextField, GET_TAGGED)
						.arg(nodeClass.getString(P.ref))
						.arg(nodeClass.getString(P.taggedBy));
			} else {
				value = JExpr.invoke(contextField, GET).arg(
						nodeClass.getString(P.ref));
				if (nodeClass.isExist(P.refTag)) {
					value.arg(nodeClass.getString(P.refTag));
				}
			}

			final Element element = (Element) nodeClass.get(P.element);
			switch (element.getKind()) {
			case METHOD:
				final JType beanType = codeModel
						.parseType(((ExecutableElement) element)
								.getParameters().get(0).asType().toString());
				block.invoke(bean, element.getSimpleName().toString()).arg(
						JExpr.cast(beanType, value));
				break;
			case FIELD:
				block.assign(JExpr
						.ref(bean, element.getSimpleName().toString()), JExpr
						.cast(codeModel.parseType(element.asType().toString()), value));
				break;
			default:
				break;
			}
		} catch (final ClassNotFoundException e) {
		}
	}

	public void setCodeModel(JCodeModel codeModel) {
		this.codeModel = codeModel;
	}

}
