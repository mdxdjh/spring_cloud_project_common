package com.ellis.commons.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 数字工具类
 * 
 * @author: smartlv
 */
public class DigitalUtil
{
    private final static Logger log = LoggerFactory.getLogger(DigitalUtil.class);

    public static long toFen(String yuanStr)
    {
        return new BigDecimal(yuanStr).multiply(new BigDecimal(100)).longValue();
    }

    public static BigDecimal toYuan(String fenStr)
    {
        return new BigDecimal(fenStr).divide(new BigDecimal(100), 2, BigDecimal.ROUND_FLOOR);
    }

    public static long toFen(BigDecimal yuan)
    {
        return yuan.multiply(new BigDecimal(100)).longValue();
    }

    public static BigDecimal toYuan1(long fen)
    {
        return new BigDecimal(fen).divide(new BigDecimal(100), 2, BigDecimal.ROUND_FLOOR);
    }

    public static String toYuan2(long fen)
    {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(toYuan1(fen));
    }

    /**
     * 把货币小数元转换成整数分
     * 
     * @param v
     *        精确到小数点后两位的元
     * @return 货币整数分
     * @author: hualong
     * @date: 2014年4月9日下午5:00:06
     */
    public static int toFen(double v)
    {
        try
        {
            return (int) (v * 100);
        }
        catch (Exception e)
        {
            log.error("activity sku price integer change to double error", e);
            return 0;
        }
    }

    /**
     * 把 7 整数转换成 [1, 2, 4]这样的2^n形式因子的数组 方便多个状态位，bit位
     */
    public static List<Integer> toBinaryArray(int i)
    {
        Preconditions.checkArgument(i >= 0);
        List<Integer> array = Lists.newArrayListWithCapacity(8);
        int e = 1;
        while (e <= i && e > 0)
        {
            if ((e & i) > 0)
            {
                array.add(e);
            }
            e = e << 1;
        }
        return array;
    }

    public static void main(String[] args)
    {
        long t = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++)
        {
            System.out.println(toBinaryArray(i));
        }

        System.out.println(System.currentTimeMillis() - t);
    }
}
