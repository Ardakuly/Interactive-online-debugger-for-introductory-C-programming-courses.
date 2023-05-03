package com.example.onlinecodecompilerapi.configuraiton;

import com.example.onlinecodecompilerapi.controller.GCCCompiler;
import com.example.onlinecodecompilerapi.controller.GDBDebugger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final GCCCompiler gccCompiler;
    private final GDBDebugger gdbDebugger;

    @Autowired
    public WebSocketConfiguration(GCCCompiler gccCompiler, GDBDebugger gdbDebugger) {
        this.gccCompiler = gccCompiler;
        this.gdbDebugger = gdbDebugger;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gccCompiler, "/nu/editor/compile").setAllowedOrigins("*");
        registry.addHandler(gdbDebugger, "/nu/editor/debug").setAllowedOrigins("*");
    }

}
