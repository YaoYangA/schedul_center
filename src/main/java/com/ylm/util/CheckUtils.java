package com.ylm.util;

import com.ylm.common.constant.Constant;

/**
 * <验证工具类>
 *
 * @author wanglei
 * @version [版本号, 2013年8月7日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class CheckUtils
{
    /**
     * <判断对象是否为null或者空>
     *
     * @param obj 需要判断的对象
     * @return 如果对象为null或者空则返回true
     */
    public static boolean isNullOrEmpty(Object obj)
    {
        if (obj == null || Constant.EMPTY.equals(String.valueOf(obj)))
        {
            return true;
        }
        return false;
    }
}
