/**
 * Copyright (C) 2010-2012 eBusiness Information, Anton Kuhlevskyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.magicbox.processor;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import com.magicbox.processor.generator.SourceCodewriter;
import com.magicbox.processor.model.ContextManager;
import com.magicbox.processor.processing.Processor;
import com.magicbox.processor.model.Node;
import com.sun.codemodel.JCodeModel;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes({ "com.magicbox.annotation.*" })
public class MagicBoxProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		if (annotations.isEmpty()) {
			return true;
		}

		final ProcessorContext module = new ProcessorContext(this, processingEnv, roundEnv);
		try {
			module.initialize(null);

			for (Node node : module.get(ContextManager.class).root().children()) {
				module.get(Processor.class, "context").process(node);
			}

			try {
				module.get(JCodeModel.class).build(
						new SourceCodewriter(processingEnv.getFiler(),
								processingEnv.getMessager()));
			} catch (IOException e) {
				processingEnv.getMessager().printMessage(Kind.ERROR,
						e.getMessage());
			}

			module.dispose(null);
		} catch (InterruptedException e) {
			processingEnv.getMessager()
					.printMessage(Kind.ERROR, e.getMessage());
		}

		return true;
	}
}
