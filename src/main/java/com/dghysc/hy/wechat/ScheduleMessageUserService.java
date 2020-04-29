package com.dghysc.hy.wechat;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.exception.WechatConfigWrongException;
import com.dghysc.hy.exception.WechatServiceDownException;
import com.dghysc.hy.product.rep.ProductProcessRepository;
import com.dghysc.hy.product.rep.ProductRepository;
import com.dghysc.hy.util.WechatMessageUtil;
import com.dghysc.hy.util.ZoneIdUtil;
import com.dghysc.hy.wechat.model.ScheduleMessageUser;
import com.dghysc.hy.wechat.repo.ScheduleMessageUserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static com.dghysc.hy.util.WechatMessageUtil.createData;

/**
 * Schedule Message User Service
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class ScheduleMessageUserService {

    private final String dailyReportTemplateId;

    private final ProductRepository productRepository;

    private final ProductProcessRepository productProcessRepository;

    private final ScheduleMessageUserRepository scheduleMessageUserRepository;

    private final WechatMessageUtil wechatMessageUtil;

    private final Log logger;

    public ScheduleMessageUserService(
            @Value("${manage.wechat.dailyReportTemplateId}") String dailyReportTemplateId,
            ProductRepository productRepository,
            ProductProcessRepository productProcessRepository,
            ScheduleMessageUserRepository scheduleMessageUserRepository,
            WechatMessageUtil wechatMessageUtil
    ) {
        this.dailyReportTemplateId = dailyReportTemplateId;
        this.productRepository = productRepository;
        this.productProcessRepository = productProcessRepository;
        this.scheduleMessageUserRepository = scheduleMessageUserRepository;
        this.wechatMessageUtil = wechatMessageUtil;
        this.logger = LogFactory.getLog(this.getClass());
    }

    /**
     * Add User In Schedule Message User Set.
     * @param id the user wechat id, must not be {@literal null}.
     * @throws NullPointerException in case the given {@literal id} is {@literal null}
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void add(@NotNull String id) {
        ScheduleMessageUser user = new ScheduleMessageUser(id);
        scheduleMessageUserRepository.save(user);
    }

    /**
     * Remove User From Schedule Message User Set.
     * @param id the user wechat id, must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void remove(@NotNull String id) {
        scheduleMessageUserRepository.deleteById(id);
    }

    /**
     * Load All Schedule Message User
     * @return the schedule message users.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Iterable<ScheduleMessageUser> loadAll() {
        return scheduleMessageUserRepository.findAll();
    }

    /**
     * Send Daily Message Service
     */
    @Scheduled(cron="0 0 10 * * ? ")
    public void sendDailyMessage() {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneIdUtil.CST);
        ZonedDateTime today = localDateTime
                .toLocalDate()
                .atStartOfDay(ZoneIdUtil.CST);

        Timestamp todayTimestamp = Timestamp.from(today.toInstant());
        Timestamp tomorrowTimestamp = Timestamp.from(today.plusDays(1).toInstant());

        final int todayComplete = productRepository.countAllByCompleteTimeAfterAndCompleteTimeBefore(
                todayTimestamp, tomorrowTimestamp
        );

        if (todayComplete == 0 && productProcessRepository
                .countAllByFinishTimeAfterAndFinishTimeBefore(
                        todayTimestamp, tomorrowTimestamp
                ) == 0) return;

        final int todayCreateValue = productRepository.countAllByCreateTimeAfterAndCreateTimeBefore(
                todayTimestamp, tomorrowTimestamp
        );
        final int startValue = productRepository.countALLStart();
        final int noStartValue = productRepository.countAllNotStart();

        final Map<String, JSONObject> data = new HashMap<>();
        data.put("first", createData("今日订单统计"));
        data.put("keyword1", createData(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE)));
        data.put("keyword2", createData(
                "今日完成订单数：" + todayComplete +
                        "\n今日创建订单数：" + todayCreateValue +
                        "\n进行中的订单数：" + startValue +
                        "\n还未开始的订单数：" + noStartValue)
        );
        data.put("keyword3", createData(localDateTime.format(DateTimeFormatter.ISO_LOCAL_TIME)));
        data.put("value", createData(null));

        for (ScheduleMessageUser scheduleMessageUser : loadAll()) {
            try {
                // TODO 提供URL
                wechatMessageUtil.sendTemplateMessage(
                        dailyReportTemplateId, scheduleMessageUser.getId(), null, null, data
                );
            } catch (WechatServiceDownException | WechatConfigWrongException e) {
                logger.error("Send user '" + scheduleMessageUser.getId() + "' daily message error.");
                logger.error(e.getMessage());
            }
        }
    }
}
