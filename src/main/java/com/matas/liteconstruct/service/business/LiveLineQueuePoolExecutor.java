package com.matas.liteconstruct.service.business;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LiveLineQueuePoolExecutor {

  private volatile boolean isInited = false;

  private AtomicInteger countQueries = new AtomicInteger(0);

  private AtomicInteger startNextQuery = new AtomicInteger(0);

  private LiveLineBusinessService liveLineBusinessService;

  private boolean isServerStoped = false;

  @Autowired
  public void setLiveLineBusinessService(LiveLineBusinessService liveLineBusinessService) {
    this.liveLineBusinessService = liveLineBusinessService;
  }

  public void initService() {
    if (!isInited) {
      isInited = true;
      final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
      executorService.scheduleAtFixedRate(() -> {
        if (isServerStoped) {
          executorService.shutdown();
        }
        if (countQueries.get() > 0 && startNextQuery.get() == 0) {
          startNextQuery.incrementAndGet();
          new Thread(() -> {
            try {
              Thread.sleep(10000);
              startNextQuery.decrementAndGet();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }).start();
          log.info("update users with executor. respond: {}", nextUserExecutor());
          countQueries.decrementAndGet();
        }
      }, 0, 1000, TimeUnit.MILLISECONDS);
    } else {
      log.info("queue executor is inited yet.");
    }
  }

  public int addNewQuery() {
    initService();
    return countQueries.incrementAndGet();
  }

  public String nextUserExecutor() {
    String errorMessage = null;
    try {
      errorMessage = liveLineBusinessService.setNextUserMigration();
      if (errorMessage != null && !errorMessage.equals("nullnullnull")) {
        return errorMessage;
      } else {
        return null;
      }
    } catch (EmptyResultDataAccessException empty) {
      return errorMessage;
    }
  }

  public boolean stopDaemon() {
    isServerStoped = true;
    return isServerStoped;
  }
}
