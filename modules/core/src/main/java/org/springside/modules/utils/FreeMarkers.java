package org.springside.modules.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreeMarkers {

	private FreeMarkers() {
	}

	public static String rendereString(String templateString, Map<String, ?> model) {
		try {
			StringWriter result = new StringWriter();
			Template t = new Template("name", new StringReader(templateString), new Configuration());
			t.process(model, result);
			return result.toString();
		} catch (IOException e) {
			throw new RuntimeException("Freemarker template error", e);
		} catch (TemplateException e) {
			throw new RuntimeException("Freemarker template error", e);
		}
	}

	public static String renderTemplate(Template template, Object model) {
		try {
			StringWriter result = new StringWriter();
			template.process(model, result);
			return result.toString();
		} catch (IOException e) {
			throw new RuntimeException("Freemarker template not exist", e);
		} catch (TemplateException e) {
			throw new RuntimeException("Freemarker template error", e);
		}
	}

	public static Configuration buildConfiguration(String directory) throws IOException {

		Configuration cfg = new Configuration();
		Resource path = new DefaultResourceLoader().getResource(directory);
		cfg.setDirectoryForTemplateLoading(path.getFile());
		return cfg;

	}
}
