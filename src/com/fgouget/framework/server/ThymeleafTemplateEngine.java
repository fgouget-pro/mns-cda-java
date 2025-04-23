package com.fgouget.framework.server;

import com.fgouget.framework.server.model.Request;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.util.Map;

public class ThymeleafTemplateEngine  implements ITemplateEngine{

    private final TemplateEngine thymeleafTemplateEngine;

    public ThymeleafTemplateEngine() {
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setCharacterEncoding("UTF-8");

        this.thymeleafTemplateEngine = new TemplateEngine();
        this.thymeleafTemplateEngine.setTemplateResolver(resolver);
    }


    public String process(String template, Map<String, Object> context, Request request){
        Context c = new Context();
        context.forEach(c::setVariable);
        return thymeleafTemplateEngine.process(template, c);
    }
}
