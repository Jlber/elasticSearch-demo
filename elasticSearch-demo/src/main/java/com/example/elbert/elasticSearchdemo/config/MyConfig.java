package com.example.elbert.elasticSearchdemo.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by elbert on 2017/12/31.
 * @author  elbert
 */
@Configuration
public class MyConfig {
    private static final String IP = "127.0.0.1";
    @Bean
    public TransportClient client() throws UnknownHostException {
        Settings settings=  Settings.builder().put("cluster.name","Enqing").build();
        return new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(IP), 9300));
    }
}
