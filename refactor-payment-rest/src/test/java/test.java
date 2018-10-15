/**
 * Project: JVM_Test
 * <p>
 * File: PACKAGE_NAME
 * <p>
 * Description:
 *
 * @author: MikeLC
 * @date: 2018-04-03 下午 03:34
 * <p>
 * Copyright ( c ) 2017
 */

import com.alibaba.druid.sql.visitor.functions.Char;
import com.google.common.base.Charsets;
import com.refactor.mall.common.enums.ServiceTypeEnum;
import com.refactor.mall.common.util.Date.DateUtil;
import com.refactor.mall.common.util.MD5Util;
import com.refactor.mall.common.util.UUIDUtils;
import com.refactor.mall.common.util.bean.BeanUtil;
import com.refactor.mall.common.util.compute.MathUtil;
import com.refactor.mall.common.util.program.PwdUtil;
import com.refactor.mall.common.vo.busi.rent.response.ResRentConfigsShowVo;
import com.refactor.mall.common.vo.busi.system.request.system.sel.SellerMemberAddVo;
import com.refactor.mall.common.vo.user.MemberOperateInfo;
import org.bouncycastle.asn1.dvcs.ServiceType;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Project: RefactorMall
 *
 * File:PACKAGE_NAME
 *
 * Description:
 *
 * @author: MikeLC
 *
 * @date: 2018-04-03 下午 03:34 
 **/
public class test {


    @Test
    public void demo10(){
        //
        BigDecimal a = MathUtil.div(new BigDecimal(1), new BigDecimal(100), 4);
        BigDecimal b = new BigDecimal(100);
        BigDecimal result = MathUtil.mul(a, b);
        result.setScale(0, BigDecimal.ROUND_DOWN);
        result.setScale(1, BigDecimal.ROUND_DOWN);
        DecimalFormat df   =new  DecimalFormat("#");
        df.format(result);
        System.out.println(result);
    }

    @Test
    public void demo9(){
        //
        BigDecimal a = new BigDecimal(10);
        BigDecimal b = new BigDecimal(3);
        System.out.println(MathUtil.div(a, b, 2, BigDecimal.ROUND_UP));
        System.out.println(MathUtil.div(a, b, 2, BigDecimal.ROUND_DOWN));
        System.out.println(MathUtil.div(a, b, 2, BigDecimal.ROUND_CEILING));


    }

    @Test
    public void demo8(){
        SellerMemberAddVo vo = new SellerMemberAddVo();
        vo.setCode("111");
        vo.setName("123");
        vo.setPassword("123456");

        MemberOperateInfo memberOperateInfo = new MemberOperateInfo();
        BeanUtil.copyProperties(vo, memberOperateInfo);
        vo.setPassword(PwdUtil.getMemberPwd(memberOperateInfo));

    }

    @Test
    public void demo7(){
        //
        String targetStr = "ê%\u0097ú";
        //
        try {
            byte[] bytes = targetStr.getBytes("UTF-16");
            targetStr = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void testReflect(Object model) throws SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException, InstantiationException
    {
        // 获取实体类的所有属性，返回Field数组
        Field[] field = model.getClass().getDeclaredFields();
        // 获取属性的名字
        String[] modelName = new String[field.length];
        String[] modelType = new String[field.length];
        for (int i = 0; i < field.length; i++)
        {
            // 获取属性的名字
            String name = field[i].getName();
            modelName[i] = name;
            // 获取属性类型
            String type = field[i].getGenericType().toString();
            modelType[i] = type;

            //关键。。。可访问私有变量
            field[i].setAccessible(true);
            //给属性设置
            field[i].set(model,  field[i].getType().getConstructor(field[i].getType()).newInstance("kou"));

            // 将属性的首字母大写
            name = name.replaceFirst(name.substring(0, 1), name.substring(0, 1)
                    .toUpperCase());

            if (type.equals("class java.lang.String"))
            {
                // 如果type是类类型，则前面包含"class "，后面跟类名
                Method m = model.getClass().getMethod("get" + name);
                // 调用getter方法获取属性值
                String value = (String) m.invoke(model);
                if (value != null)
                {

                    System.out.println("attribute value:" + value);
                }

            }
            if (type.equals("class java.lang.Integer"))
            {
                Method m = model.getClass().getMethod("get" + name);
                Integer value = (Integer) m.invoke(model);
                if (value != null)
                {
                    System.out.println("attribute value:" + value);
                }
            }
            if (type.equals("class java.lang.Short"))
            {
                Method m = model.getClass().getMethod("get" + name);
                Short value = (Short) m.invoke(model);
                if (value != null)
                {
                    System.out.println("attribute value:" + value);
                }
            }
            if (type.equals("class java.lang.Double"))
            {
                Method m = model.getClass().getMethod("get" + name);
                Double value = (Double) m.invoke(model);
                if (value != null)
                {
                    System.out.println("attribute value:" + value);
                }
            }
            if (type.equals("class java.lang.Boolean"))
            {
                Method m = model.getClass().getMethod("get" + name);
                Boolean value = (Boolean) m.invoke(model);
                if (value != null)
                {
                    System.out.println("attribute value:" + value);
                }
            }
            if (type.equals("class java.util.Date"))
            {
                Method m = model.getClass().getMethod("get" + name);
                Date value = (Date) m.invoke(model);
                if (value != null)
                {
                    System.out.println("attribute value:"
                            + value.toLocaleString());
                }
            }
        }
    }

    @Test
    public void demo6(){
        //
        ResRentConfigsShowVo vo = new ResRentConfigsShowVo();
        Class cls = vo.getClass();
        Field[] fields = cls.getDeclaredFields();
        for(int i=0; i<fields.length; i++){
            Field f = fields[i];
            f.setAccessible(true);
            //System.out.println("属性名:" + f.getName() + " 属性值:" + f.get() );
        }
    }

    @Test
    public void demo5(){
        //
        ServiceTypeEnum result = ServiceTypeEnum.valueOf(ServiceTypeEnum.TONING.getCode());
        System.out.println(result.getName());
        //
        ServiceTypeEnum result2 = ServiceTypeEnum.valueOf(ServiceTypeEnum.TONING.getName());
        System.out.println(result2.getName());
        //

    }

    @Test
    public void demo4(){
        Date beginDate = DateUtil.str2Date("2019-05-01 00:00:00", DateUtil.secondPattern);
        Date endDate = DateUtil.str2Date("2018-06-14 00:00:00", DateUtil.secondPattern);
        //
        Calendar from  =  Calendar.getInstance();
        from.setTime(beginDate);
        Calendar  to  =  Calendar.getInstance();
        to.setTime(endDate);
        int fromYear = from.get(Calendar.YEAR);
        int fromMonth = from.get(Calendar.MONTH);
        int fromDay = from.get(Calendar.DAY_OF_MONTH);
        int toYear = to.get(Calendar.YEAR);
        int toMonth = to.get(Calendar.MONTH);
        int toDay = to.get(Calendar.DAY_OF_MONTH);
        int year = toYear  -  fromYear;
        int month = toMonth  - fromMonth;
        int day = toDay  - fromDay;
        System.out.println(year + "年" + month + "月" + day + "天");
    }

    @Test
    public void demo3(){
        String str = UUIDUtils.generateShortUuid(8);
        System.out.println(str.toUpperCase());
    }

    @Test
    public void demo2(){
        try {
            String str = "123456"+"refactorMallSDMembEr";

            String strResult = MD5Util.GetMD5Code(str);
            System.out.println(strResult);


            String string = URLEncoder.encode(strResult, Charsets.UTF_8.name());
            System.out.println(string);

            String string2 = URLDecoder.decode(string, Charsets.UTF_8.name());
            System.out.println(string2);




        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void demo1(){
        try {
            String str = "123456"+"refactorMallSDseLLer";

            String strResult = MD5Util.GetMD5Code(str);
            System.out.println(strResult);


            String string = URLEncoder.encode(strResult, Charsets.UTF_8.name());
            System.out.println(string);

            String string2 = URLDecoder.decode(string, Charsets.UTF_8.name());
            System.out.println(string2);




        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }





}
