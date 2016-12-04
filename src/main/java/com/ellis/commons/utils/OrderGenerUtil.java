package com.ellis.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * @author ellis.luo
 * @date 16/11/28 下午3:17
 * @description
 */
public class OrderGenerUtil
{
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderGenerUtil.class);

    // 订单号
    public static final String ORDER = "01";

    private static Random random = new Random();

    /**
     * 生成订单编号
     *
     * @param uid
     * @return
     */
    public static String genOrderId(String type, long uid)
    {
        String userPart = String.format("%02d", uid);

        return System.currentTimeMillis() + type + getRandomData() + userPart.substring(userPart.length() - 2);
    }

    private static int getRandomData()
    {
        return random.nextInt(899) + 100;
    }
}
