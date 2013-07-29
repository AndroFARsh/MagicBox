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
package com.beanannotation.processor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes({ "com.beanannotation.annotation.*" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class BeanAnnotationProcessor extends AbstractProcessor {
	private Messager messager;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		messager = processingEnv.getMessager();
		messager.printMessage(Diagnostic.Kind.NOTE,
				"Starting AndroidAnnotations annotation processing");
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {

		// Module bean = new Module();
		// try {
		// bean.initialize(null);
		//
		// Enumeration<PropertyInfo<?>> en = bean.get(
		// PropertyHolder.class.getCanonicalName(),
		// PropertyHolder.class).getInfo();
		// while (en.hasMoreElements()) {
		// PropertyInfo<?> info = en.nextElement();
		//
		// messager.printMessage(Kind.ERROR, info.value);
		// }
		//
		// bean.dispose(null);
		// } catch (InterruptedException e) {
		// }

		return true;
	}

}
