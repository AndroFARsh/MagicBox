package com.magicbox.app;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.widget.TextView;

import com.magicbox.AbstractBeanFactory;
import com.magicbox.Resource;
import com.magicbox.XmlBeanFactory;
import com.magicbox.demo.model.FileManager;
import com.magicbox.demo.model.Parser;
import com.magicbox.demo.model.ResourceManager;

public class MainActivity extends FragmentActivity implements LoaderCallbacks<AbstractBeanFactory>{

	private long startTime;
	private AbstractBeanFactory factory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		startTime = System.currentTimeMillis();
		getSupportLoaderManager().initLoader(0, savedInstanceState, this);
	}

	private void initialize() {
		StringBuilder text = new StringBuilder();
		text.append("<b>Initialize time: </b>").append(System.currentTimeMillis() - startTime).append("ms<br/><br/>");
		text.append("<b>FileManager bean: </b><br/>").append(factory.<FileManager>get("fileManager").getName()).append("<br/>");
		text.append("<b>ResourceManager bean: </b><br/>").append(factory.<ResourceManager>get("resourceManager").getName()).append("<br/>");
		text.append("<b>Json Parser bean: </b><br/>").append(factory.<Parser<?>>get("parser", "json").getName()).append("<br/>");
		text.append("<b>Xml Parser bean: </b><br/>").append(factory.<Parser<?>>get("parser", "xml").getName()).append("<br/>");
		
		((TextView)findViewById(R.id.text)).setText(Html.fromHtml(text.toString()));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		try {
			if (factory != null){
				factory.dispose(null);
				factory = null;
			}
		} catch (InterruptedException e) {
		}
	}
	
	static class LoaderImpl extends AsyncTaskLoader<AbstractBeanFactory> {
		
		LoaderImpl(Context context){
			super(context);
		}
		
		@Override
		public AbstractBeanFactory loadInBackground() {
			XmlBeanFactory factory = new XmlBeanFactory(new Resource() {
				@Override
				public InputStream open() {
					try {
						return getContext().getAssets().open("beans.xml");
					} catch (IOException e) {
					}
					return null;
				}
			});
			
			try {
				factory.initialize(null);
				return factory;
			} catch (InterruptedException e) {
			}
			return null;
		}
	}

	@Override
	public Loader<AbstractBeanFactory> onCreateLoader(int arg0, Bundle arg1) {
		Loader<AbstractBeanFactory> loader = new LoaderImpl(this); 
		loader.forceLoad();
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<AbstractBeanFactory> arg0, AbstractBeanFactory arg1) {
		factory = arg1;
		initialize();
	}

	@Override
	public void onLoaderReset(Loader<AbstractBeanFactory> arg0) {
		arg0.forceLoad();
	}
}
