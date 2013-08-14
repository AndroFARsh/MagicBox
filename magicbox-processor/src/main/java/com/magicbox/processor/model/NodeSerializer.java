package com.magicbox.processor.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.magicbox.processor.model.Node.NodeType;
import com.magicbox.processor.xml.dtd.A;
import com.magicbox.processor.xml.dtd.ContextDtd;
import com.magicbox.processor.xml.dtd.T;

public class NodeSerializer implements Serializer {

	private Map<NodeType, AttrResolver> resolvers = new HashMap<NodeType, AttrResolver>() {
		private static final long serialVersionUID = 1L;
		{
			put(NodeType.Context, new ContextAttrResolver());
			put(NodeType.Bean, new BeanAttrResolver());
			put(NodeType.Alias, new AliasAttrResolver());
			put(NodeType.Property, new PropertyAttrResolver());

		}
	};

	private DocumentBuilderFactory documentFactory = DocumentBuilderFactory
			.newInstance();
	private TransformerFactory transformerFactory = TransformerFactory
			.newInstance();
	
	@Override
	public void serialize(Node node, FileObject output) {
		try {
			Document document = documentFactory.newDocumentBuilder()
					.newDocument();

			document.appendChild(serialize(node, document));

			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(output.openOutputStream());

			transformer.transform(source, result);
			result.getOutputStream().close();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Element serialize(Node node, Document document) {
		switch (node.type()) {
		case Root:
			return mergeNodeToElement(
					document.createElement(ContextDtd.INSTANCE.TagIdToName[T.Contexts]),
					node, document);
		case Context:
			return mergeNodeToElement(
					document.createElement(ContextDtd.INSTANCE.TagIdToName[T.Beans]),
					node, document);
		case Bean:
			return mergeNodeToElement(
					document.createElement(ContextDtd.INSTANCE.TagIdToName[T.Bean]),
					node, document);
		case Alias:
			return mergeNodeToElement(
					document.createElement(ContextDtd.INSTANCE.TagIdToName[T.Alias]),
					node, document);
		case Property:
			return mergeNodeToElement(
					document.createElement(ContextDtd.INSTANCE.TagIdToName[T.Property]),
					node, document);
		default:
			throw new UnsupportedOperationException();
		}
	}

	private Element mergeNodeToElement(Element element, Node node,
			Document document) {
		if (resolvers.containsKey(node.type())) {
			AttrResolver resolver = resolvers.get(node.type());
			NodeClass nodeClass = ((BaseNode) node).getNodeClass();
			for (int id : nodeClass.ids()) {
				Attr attr = resolver.resolve(id, nodeClass.get(id), document);
				if (attr != null) {
					element.setAttributeNode(attr);
				}
			}
		}

		for (Node child : node.children()) {
			element.appendChild(serialize(child, document));
		}
		return element;
	}
	
	public interface AttrResolver {
		Attr resolve(int p, Object value, Document document);
	}

	static class ContextAttrResolver implements AttrResolver {

		@Override
		public Attr resolve(int p, Object value, Document document) {
			switch (p) {
			case P.fullQulifiedName:
				Attr attr = document
						.createAttribute(ContextDtd.INSTANCE.AttributeIdToName[A.Output]);
				attr.setValue((String) value);
				return attr;
			default:
				return null;
			}
		}
	}

	static class BeanAttrResolver implements AttrResolver {

		@Override
		public Attr resolve(int p, Object value, Document document) {
			switch (p) {
			case P.id: {
				Attr attr = document
						.createAttribute(ContextDtd.INSTANCE.AttributeIdToName[A.Id]);
				attr.setValue((String) value);
				return attr;
			}
			case P.tag: {
				Attr attr = document
						.createAttribute(ContextDtd.INSTANCE.AttributeIdToName[A.Tag]);
				attr.setValue((String) value);
				return attr;
			}
			case P.element: {
				Attr attr = document
						.createAttribute(ContextDtd.INSTANCE.AttributeIdToName[A.Class]);
				attr.setValue(((TypeElement) value).getQualifiedName()
						.toString());
				return attr;
			}
			default:
				return null;
			}
		}
	}

	static class AliasAttrResolver implements AttrResolver {

		@Override
		public Attr resolve(int p, Object value, Document document) {
			switch (p) {
			case P.id: {
				Attr attr = document
						.createAttribute(ContextDtd.INSTANCE.AttributeIdToName[A.Id]);
				attr.setValue((String) value);
				return attr;
			}
			case P.tag: {
				Attr attr = document
						.createAttribute(ContextDtd.INSTANCE.AttributeIdToName[A.Tag]);
				attr.setValue((String) value);
				return attr;
			}
			default:
				return null;
			}
		}
	}

	static class PropertyAttrResolver implements AttrResolver {

		@Override
		public Attr resolve(int p, Object value, Document document) {
			switch (p) {
			case P.ref: {
				Attr attr = document
						.createAttribute(ContextDtd.INSTANCE.AttributeIdToName[A.Ref]);
				attr.setValue((String) value);
				return attr;
			}
			case P.refTag: {
				Attr attr = document
						.createAttribute(ContextDtd.INSTANCE.AttributeIdToName[A.RefTag]);
				attr.setValue((String) value);
				return attr;
			}
			case P.taggedBy: {
				Attr attr = document
						.createAttribute(ContextDtd.INSTANCE.AttributeIdToName[A.TaggedBy]);
				attr.setValue((String) value);
				return attr;
			}
			case P.name: {
				Attr attr = document
						.createAttribute(ContextDtd.INSTANCE.AttributeIdToName[A.Name]);
				attr.setValue((String) value);
				return attr;
			}
			default:
				return null;
			}
		}
	}
}
