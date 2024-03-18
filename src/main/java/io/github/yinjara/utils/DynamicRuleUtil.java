package io.github.yinjara.utils;

import io.github.yinjara.pojo.CompileResult;
import io.github.yinjara.pojo.SingleDefectRule;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.rules.api.Rules;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.CharBuffer;
import java.util.*;

@Slf4j
@Configuration
@Component
public class DynamicRuleUtil {
    private static FreemarkerUtil freemarkerUtil = new FreemarkerUtil();

    private static String jarPath;

    @Value("${file.ruleJarPath}")
    public void setJarPath(String jarPath) {
        DynamicRuleUtil.jarPath = jarPath;
    }


    public static Map<String, String> generateSingleRuleString(List<SingleDefectRule> list) {
        Map<String, String> returnMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("className", list.get(i).getRuleGroupName() + "SingleRule" + i);
            map.put("qualityRuleType", list.get(i).getRuleType());
            map.put("qualityDefectName", list.get(i).getDefectName());
            map.put("qualityResult", list.get(i).getGrade());
            if (list.get(i).getRuleType().equals("长度")) {
                map.put("qualityLowerLimit", list.get(i).getLengthLowerLimit() + "");
                map.put("qualityUpperLimit", list.get(i).getLengthUpperLimit() + "");
            } else if (list.get(i).getRuleType().equals("宽度")) {
                map.put("qualityLowerLimit", list.get(i).getWidthLowerLimit() + "");
                map.put("qualityUpperLimit", list.get(i).getWidthUpperLimit() + "");
            } else if (list.get(i).getRuleType().equals("面积")) {
                map.put("qualityLowerLimit", list.get(i).getAreaLowerLimit() + "");
                map.put("qualityUpperLimit", list.get(i).getAreaUpperLimit() + "");
            } else if (list.get(i).getRuleType().equals("密度")) {
                map.put("qualityLowerLimit", list.get(i).getDensityLowerLimit() + "");
                map.put("qualityUpperLimit", list.get(i).getDensityUpperLimit() + "");
            }
            String strCode = freemarkerUtil.generateString("SingleRule.ftl", map);
            returnMap.put(list.get(i).getRuleGroupName() + "SingleRule" + i + ".java", strCode);
        }
        if (list.size() > 0) {
            log.info("已生成{}规则组下的单缺陷判定规则源码，共{}条 ", list.get(0).getRuleGroupName(), returnMap.size());
        }
        return returnMap;
    }


    public static List<CompileResult> compile(Map<String, String> map) throws Exception {
        List<CompileResult> compileResultList = new ArrayList<>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager stdManager = compiler.getStandardFileManager(null, null, null);

        List<File> jars = new ArrayList<>();
        getJarFiles(new File(jarPath), jars);

        List<File> dependencies = new ArrayList<>();
        dependencies.addAll(jars);
        stdManager.setLocation(StandardLocation.CLASS_PATH, dependencies);
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        Iterable<String> options = Arrays.asList("-encoding", "UTF-8", "-source", "11");

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String mapKey = entry.getKey();
            String mapValue = entry.getValue();

            try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {
                JavaFileObject javaFileObject = MemoryJavaFileManager.makeStringSource(mapKey, mapValue);

                JavaCompiler.CompilationTask task =
                        compiler.getTask(
                                null, manager, collector, options, null, Arrays.asList(javaFileObject));
                if (task.call()) {
                    CompileResult compileResult =
                            CompileResult.builder()
                                    .mainClassFileName(mapKey)
                                    .byteCode(manager.getClassBytes())
                                    .build();
                    compileResultList.add(compileResult);
                }
            } catch (IOException e) {
                log.error("编译出错啦！", e);
            }
        }
        log.info("成功编译数："+compileResultList.size());
        return compileResultList;
    }

    public static Class<?> getRuleClass(CompileResult compileResult) throws Exception {
        String fullName =
                compileResult
                        .getMainClassFileName()
                        .substring(0, compileResult.getMainClassFileName().indexOf("."));
        try (MemoryClassLoader classLoader = new MemoryClassLoader(compileResult.getByteCode())) {
            return classLoader.findClass(fullName);
        } catch (Exception e) {
            log.error("加载类{}异常！", fullName);
            throw e;
        }
    }

    private static class MemoryClassLoader extends URLClassLoader {
        Map<String, byte[]> classBytes = new HashMap<>();

        public MemoryClassLoader(Map<String, byte[]> classBytes) {
            super(new URL[0], MemoryClassLoader.class.getClassLoader());
            this.classBytes.putAll(classBytes);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] buf = classBytes.get(name);
            if (buf == null) {
                return super.findClass(name);
            }
            classBytes.remove(name);
            return defineClass(name, buf, 0, buf.length);
        }
    }

    private static void getJarFiles(File jarFile, List<File> jars) throws Exception {
        if (jarFile.exists() && jarFile != null) {
            if (jarFile.isDirectory()) {
                File[] childrenFiles =
                        jarFile.listFiles(
                                (pathname) -> {
                                    if (pathname.isDirectory()) {
                                        return true;
                                    } else {
                                        String name = pathname.getName();
                                        if (name.endsWith(".jar") ? true : false) {
                                            return true;
                                        }
                                        return false;
                                    }
                                });
                for (File childFile : childrenFiles) {
                    getJarFiles(childFile, jars);
                }

            } else {
                String name = jarFile.getName();
                if (name.endsWith(".jar") ? true : false) {
                    jars.add(jarFile);
                }
            }
        }
    }


    public static Rules getQualityEngineRules(List<CompileResult> compileResults) throws Exception {
        Rules rules = new Rules();
        for (int i = 0; i < compileResults.size(); i++) {
            Class<?> clazz = getRuleClass(compileResults.get(i));
            Constructor constructor = clazz.getConstructor(null);
            rules.register(constructor.newInstance());
        }
        return rules;
    }
}

/**
 * 在内存中保持编译的类字节的JavaFileManager。
 */
@SuppressWarnings({"unchecked", "rawtypes"})
final class MemoryJavaFileManager extends ForwardingJavaFileManager {
    /**
     * Java源文件扩展名。
     */
    private static final String EXT = ".java";

    private Map<String, byte[]> classBytes;

    public MemoryJavaFileManager(JavaFileManager fileManager) {
        super(fileManager);
        classBytes = new HashMap<>();
    }

    public Map<String, byte[]> getClassBytes() {
        return classBytes;
    }

    @Override
    public void close() throws IOException {
        classBytes = new HashMap<>();
    }

    @Override
    public void flush() throws IOException {
    }

    /**
     * 将Java字节码存储到classBytes映射中的文件对象。
     */
    private static class StringInputBuffer extends SimpleJavaFileObject {
        final String code;

        StringInputBuffer(String name, String code) {
            super(toURI(name), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
            return CharBuffer.wrap(code);
        }

        @SuppressWarnings("unused")
        public Reader openReader() {
            return new StringReader(code);
        }
    }

    /**
     * 将Java字节码存储到classBytes映射中的文件对象。
     */
    private class ClassOutputBuffer extends SimpleJavaFileObject {
        private String name;

        ClassOutputBuffer(String name) {
            super(toURI(name), Kind.CLASS);
            this.name = name;
        }

        @Override
        public OutputStream openOutputStream() {
            return new FilterOutputStream(new ByteArrayOutputStream()) {
                @Override
                public void close() throws IOException {
                    out.close();
                    ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
                    classBytes.put(name, bos.toByteArray());
                }
            };
        }
    }

    @Override
    public JavaFileObject getJavaFileForOutput(
            Location location,
            String className,
            JavaFileObject.Kind kind,
            FileObject sibling)
            throws IOException {
        if (kind == JavaFileObject.Kind.CLASS) {
            return new io.github.yinjara.utils.MemoryJavaFileManager.ClassOutputBuffer(className);
        } else {
            return super.getJavaFileForOutput(location, className, kind, sibling);
        }
    }

    static JavaFileObject makeStringSource(String name, String code) {
        return new io.github.yinjara.utils.MemoryJavaFileManager.StringInputBuffer(name, code);
    }

    static URI toURI(String name) {
        File file = new File(name);
        if (file.exists()) {
            return file.toURI();
        } else {
            try {
                final StringBuilder newUri = new StringBuilder();
                newUri.append("mfm:///");
                newUri.append(name.replace('.', '/'));
                if (name.endsWith(EXT)) {
                    newUri.replace(newUri.length() - EXT.length(), newUri.length(), EXT);
                }
                return URI.create(newUri.toString());
            } catch (Exception exp) {
                return URI.create("mfm:///com/sun/script/java/java_source");
            }
        }
    }
}
