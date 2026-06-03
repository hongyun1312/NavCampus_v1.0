package com.hongyun.accounting.service;

import com.hongyun.accounting.entity.SystemLog;
import com.hongyun.accounting.entity.User;
import com.hongyun.accounting.repository.SystemLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemLogService {

    @Autowired
    private SystemLogRepository systemLogRepository;

    public void log(User user, String action, String details) {
        SystemLog log = new SystemLog();
        log.setUser(user);
        log.setAction(action);
        log.setDetails(details);
        systemLogRepository.save(log);
    }
}
