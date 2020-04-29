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
        final JSONObject first = new JSONObject();
        final JSONObject keyword1 = new JSONObject();
        final JSONObject keyword2 = new JSONObject();
        final JSONObject keyword3 = new JSONObject();
        final JSONObject remark = new JSONObject();

        data.put("first", first);
        data.put("keyword1", keyword1);
        data.put("keyword2", keyword2);
        data.put("keyword3", keyword3);
        data.put("remark", remark);

        first.put("color", "#173177");
        keyword1.put("color", "#173177");
        keyword2.put("color", "#173177");
        keyword3.put("color", "#173177");
        remark.put("color", "#173177");

        final String firstValue = "今日订单统计";
        first.put("value", firstValue);

        final String keyword1Value = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
        keyword1.put("value", keyword1Value);

        final String keyword2Value =
                "今日完成订单数：" + todayComplete +
                "\n今日创建订单数：" + todayCreateValue +
                "\n进行中的订单数：" + startValue +
                "\n还未开始的订单数：" + noStartValue;
        keyword2.put("value", keyword2Value);

        final String keyword3Value = localDateTime.format(DateTimeFormatter.ISO_LOCAL_TIME);
        keyword3.put("value", keyword3Value);

        remark.put("value", "");


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
