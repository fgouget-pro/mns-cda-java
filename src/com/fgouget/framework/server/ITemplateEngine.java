package com.fgouget.framework.server;

import com.fgouget.framework.server.model.Request;

import javax.inject.Named;
import java.io.Writer;
import java.util.Map;

@Named
public interface ITemplateEngine {
    public String process(String template, Map<String, Object> context, Request request);
}
