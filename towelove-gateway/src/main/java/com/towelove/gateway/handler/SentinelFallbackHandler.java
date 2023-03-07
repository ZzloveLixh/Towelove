package com.towelove.gateway.handler;

import cn.hutool.http.HttpUtil;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.towelove.common.core.utils.ServletUtils;
import org.springframework.boot.actuate.health.HttpCodeStatusMapper;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * 自定义限流异常处理
 *
 * @author: 张锦标
 */
public class SentinelFallbackHandler implements WebExceptionHandler
{
    private Mono<Void> writeResponse(ServerResponse response, ServerWebExchange exchange)
    {
        return ServletUtils.webFluxResponseWriter(exchange.getResponse(),
                "当前访问人数过多，请稍候再试");
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex)
    {
        if (exchange.getResponse().isCommitted())
        {
            return Mono.error(ex);
        }
        if (!BlockException.isBlockException(ex))
        {
            return Mono.error(ex);
        }
        return handleBlockedRequest(exchange, ex)
                .flatMap(response -> writeResponse(response, exchange));
    }

    private Mono<ServerResponse> handleBlockedRequest(ServerWebExchange exchange, Throwable throwable)
    {
        return GatewayCallbackManager.getBlockHandler().handleRequest(exchange, throwable);
    }
}
