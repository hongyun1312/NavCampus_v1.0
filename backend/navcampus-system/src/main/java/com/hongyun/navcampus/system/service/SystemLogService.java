package com.hongyun.navcampus.system.service;

import com.hongyun.navcampus.system.entity.SystemLog;
import com.hongyun.navcampus.system.entity.User;
import com.hongyun.navcampus.system.mapper.SystemLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemLogService {

    @Autowired
    private SystemLogMapper systemLogMapper;

    public void log(User user, String action, String details) {
        SystemLog log = new SystemLog();
        log.setUser(user);
        log.setAction(action);
        log.setDetails(details);
        systemLogMapper.save(log);
    }
}
