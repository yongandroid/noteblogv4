package me.wuwenbin.noteblogv4.config.configuration;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import me.wuwenbin.noteblogv4.config.application.NBContext;
import me.wuwenbin.noteblogv4.config.application.NBSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Enumeration;

/**
 * created by Wuwenbin on 2018/2/7 at 22:40
 *
 * @author wuwenbin
 */
@Slf4j
@Component
@EnableScheduling
public class SessionConfig {

    private final NBContext nbContext;

    @Autowired
    public SessionConfig(NBContext nbContext) {
        this.nbContext = nbContext;
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void sessionValidate() {
        log.info("validate session of application task...");
        Enumeration<String> keys = nbContext.keys();
        while (keys.hasMoreElements()) {
            String uuid = keys.nextElement();
            NBSession session = nbContext.get(uuid);
            if (session != null) {
                if (session.isExpired()) {
                    String info = "delete session for id:[{}], at [{}]";
                    log.info(StrUtil.format(info, session.getId(), LocalDateTime.now()));
                    nbContext.remove(uuid);
                }
            }
        }
    }

}
