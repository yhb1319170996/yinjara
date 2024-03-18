package io.github.yinjara.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@org.springframework.context.annotation.Configuration
public class FreemarkerUtil {

    @Value("${file.ruleJarPath}")
    public String ruleFilePath;

    @Value("${file.freemarkerUrl}")
    private String freemarkerPath;


    public Template getTemplate(String name) {
        try {
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_0);
            Resource resource = new ClassPathResource(freemarkerPath);
            File file = resource.getFile();
            configuration.setDirectoryForTemplateLoading(file);
            Template template = configuration.getTemplate(name);

            return template;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void generateFile(String name, Map<String, Object> root, String outFile) {
        FileWriter out = null;
        try {
            out = new FileWriter(new File(ruleFilePath + outFile));
            Template temp = this.getTemplate(name);
            temp.process(root, out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String generateString(String name, Map<String, Object> root) {
        StringWriter out = null;
        try {
            out = new StringWriter();
            Template temp = this.getTemplate(name);
            temp.process(root, out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return out.toString();
    }
}