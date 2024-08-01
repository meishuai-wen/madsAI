package com.mads.ai.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

/**
 * @author wangjun on 6/28/18
 */
public class DateTimeUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateTimeUtil.class);
    public static final long ONE_DAY = 24 * 3600;

    public static String isoFormat(Instant instant) {
        if (instant == null) {
            return "";
        }
        return isoFormat(new Date(instant.toEpochMilli()));
    }

    public static String isoFormat(Date date) {
        if (date == null) {
            return "";
        }
        return getISODateFormat().format(date);
    }

    public static String isoFormat(LocalDate localDate) {
        Date from = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return isoFormat(from);
    }

    public static Optional<Date> isoParseToDate(String str) {
        try {
            Date date = getISODateFormat().parse(str);
            return Optional.of(date);
        } catch (ParseException e) {
            logger.error("failed to parse with iso format ", str, e);
            return Optional.empty();
        }
    }

    public static Optional<LocalDate> isoParseToLocalDate(String str) {
        Optional<Date> date = isoParseToDate(str);
        if (date.isPresent()) {
            LocalDate from = LocalDate
                    .from(Instant.ofEpochMilli(date.get().getTime()).atZone(ZoneId.systemDefault()));

            return Optional.of(from);
        }

        return Optional.empty();
    }

    public static Optional<Instant> isoParseToInstant(String str) {
        Optional<Date> date = isoParseToDate(str);
        return date.map(Date::toInstant);
    }


    public static int getAgeFrom(LocalDate birthday) {
        return Period.between(birthday, LocalDate.now()).getYears();
    }

    public static LocalDate getBirthdayFromAge(int age) {
        LocalDate localDate = LocalDate.now();
        return localDate.plus(age * -1L, ChronoUnit.YEARS);
    }

    public static Optional<LocalDate> getBirthdayFromString(String birthday) {
        if (StringUtils.isEmpty(birthday)) {
            return Optional.empty();
        }
        String substring = birthday.substring(0, 10);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = simpleDateFormat.parse(substring);
            LocalDate from = LocalDate
                    .from(Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()));

            return Optional.of(from);
        } catch (ParseException e) {
            logger.error("failed to get birthda from {}", birthday);
        }

        return Optional.empty();
    }

    public static SimpleDateFormat getISODateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    }

    public static int getOnlyHourDifference(Instant event, Instant now) {
        int hourEvent = LocalDateTime.ofInstant(event, ZoneId.of("UTC")).getHour();
        int hourNow = LocalDateTime.ofInstant(now, ZoneId.of("UTC")).getHour();

        int tz = hourEvent - hourNow;

        if (tz < -12) {
            return tz + 24;
        } else if (tz > 11) {
            return tz - 24;
        }

        return tz;
    }

    public static boolean isWeekend(LocalDateTime time) {
        return time.getDayOfWeek() == DayOfWeek.SATURDAY || time.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    public static LocalDate getLocalDateToday(int timezone) {
        return Instant.now().plus(timezone, ChronoUnit.HOURS)
                .atZone(ZoneId.of("UTC"))
                .toLocalDate();
    }

    public static LocalDateTime getLocalDateTimeToday(int timezone) {
        return Instant.now().plus(timezone, ChronoUnit.HOURS)
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime();
    }


    /***
     * Calculate how many seconds are left until 24 o 'clock at current day of localtime
     * @param timezone
     * @return
     */
    public static long getLeftSecondsBeforeDayEnd(int timezone) {
        LocalDateTime dateTime = LocalDateTime.now();
        int hour = dateTime.getHour() + timezone;
        int localHour = 0;
        if (hour < 0) {
            localHour = hour + 24;
        } else if (hour < 24) {
            localHour = hour;
        } else {
            localHour = localHour - 24;
        }
        int localMinute = dateTime.getMinute();
        int localSecond = dateTime.getSecond();
        long left = (24 - localHour - 1) * 60 * 60 + (60 - localMinute - 1) * 60 + (60 - localSecond);
        return left;
    }

    public static int getHourOfYear(LocalDateTime localDateTime) {
        int hour = localDateTime.get(ChronoField.HOUR_OF_DAY);
        int dayOfYear = localDateTime.get(ChronoField.DAY_OF_YEAR);
        return dayOfYear * 24 + hour;
    }

    public static long calculateTimeDiff(LocalDateTime now, int timezone, int hour, int minute, int second) {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime twelveClock = LocalDateTime.of(
                now.getYear(),
                now.getMonth(),
                now.getDayOfMonth(),
                hour,
                minute,
                second);
        long diff = Duration.between(now, twelveClock).getSeconds();
        diff -= timezone * 3600;
        if (diff < 0) {
            diff += ONE_DAY;
        }
        return diff;
    }

    /***
     *  utc to est
     **/
    public static ZonedDateTime utcToEst(Instant utc) {
        String zoneId = ZoneId.SHORT_IDS.get("EST");
        return utc.atZone(ZoneId.of(zoneId));
        //return utc.atOffset(ZoneOffset.of("-5")).toInstant();
    }

    /**
     * 将时间字符串 转换为Instant 国际时间
     *
     * @param string 时间
     * @return Instant
     */
    public static Instant StringToInstant(String string) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //将 string 装换成带有 T 的国际时间，但是没有进行，时区的转换，即没有将时间转为国际时间，只是格式转为国际时间
        LocalDateTime parse = LocalDateTime.parse(string, dateTimeFormatter);
        //+8 小时，offset 可以理解为时间偏移量
        ZoneOffset offset = OffsetDateTime.now().getOffset();
        //转换为 通过时间偏移量将 string -8小时 变为 国际时间，因为亚洲上海是8时区
        Instant instant = parse.toInstant(offset);
        return instant;
    }

    /**
     * String ---> date---> Instant
     *
     * @param s 2020-03-21 17:23:54
     * @return 2020-03-21T09:23:54Z
     * @throws ParseException 抛出异常
     */
    public static Instant StringToDateToInstant(String s) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormat.parse(s);
        Instant instant = date.toInstant();
        return instant;
    }

    public static Integer hourByInstant(Instant instant) {
        //将 instant 转成 date
        Date date = Date.from(instant);
        //将 date 转成 string，调用上面的方法
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH");
        String string = dateFormat.format(date);
        return Integer.parseInt(string);
    }

    /**
     * instant 转成 String
     *
     * @param instant 国际时间
     * @return
     */
    public static String InstantToDateToString(Instant instant) {
        //将 instant 转成 date
        Date from = Date.from(instant);
        //将 date 转成 string，调用上面的方法
        String string = DateToString(from);
        /*
        //或者使用这两句
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String string = dateFormat.format(date);*/
        return string;
    }

    /**
     * date ---> instant
     *
     * @param date
     * @return
     */
    public static Instant DateToInstant(Date date) {
        Instant instant = date.toInstant();
        return instant;
    }

    /**
     * instant ---> date
     *
     * @param instant
     * @return
     */
    public static Date InstantToDate(Instant instant) {
        Date from = Date.from(instant);
        return from;
    }

    /**
     * String ---> date
     *
     * @param s 2020-03-21 17:23:54
     * @return Sat Mar 21 17:23:54 CST 2020
     * @throws ParseException
     */
    public static Date StringToDate(String s) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormat.parse(s);
        return date;
    }

    /**
     * date格式时间 转换为字符串
     */
    public static String DateToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String string = dateFormat.format(date);
        return string;
    }

    public static String formatEST(Instant instant) {
        String zoneId = ZoneId.SHORT_IDS.get("EST");
        return format(instant, ZoneId.of(zoneId));
    }

    public static String format(Instant instant, ZoneId zoneId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d'st' MMM,yy hh:mm a'(EST)'", Locale.ENGLISH).withZone(zoneId);
        return formatter.format(instant);
    }

    /**
     * 注意 里面讲instant -> date, 如果参数目前是经过时区计算的，那么这个方法返回是数字是不对的
     * 如果都是按照UTC来计算的话就没有问题
     */
    public static String format(Instant instant, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(new Date(instant.toEpochMilli()));
    }

    public static int getDid() {
        Instant now = Instant.now();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String string = dateFormat.format(new Date(now.toEpochMilli()));
        return Integer.parseInt(string);
    }

    /**
     * 注意 里面讲instant -> date, 如果参数目前是经过时区计算的，那么这个方法返回是数字是不对的
     * 如果都是按照UTC来计算的话就没有问题
     */
    public static int getDid(Instant now) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String string = dateFormat.format(new Date(now.toEpochMilli()));
        return Integer.parseInt(string);
    }

    /**
     * 注意 里面讲instant -> date, 如果参数目前是经过时区计算的，那么这个方法返回是数字是不对的
     * 如果都是按照UTC来计算的话就没有问题
     */
    public static int getHid(Instant now) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH");
        String string = dateFormat.format(new Date(now.toEpochMilli()));
        return Integer.parseInt(string);
    }


    /**
     * 比较时间，相差天数
     **/
    public static int timeBetweenDays(Instant instant1, Instant instant2) {

        Period period = timeBetween(instant1, instant2);
        int days = period.getDays();
        if (0 == days) {
            return 0;
        }
        return Math.abs(days);
    }

    /**
     * 比较时间，相差秒数
     **/
    public static long timeBetweenSeconds(Instant instant1, Instant instant2) {
        long second = Duration.between(instant1, instant2).getSeconds();
        if (0 == second) {
            return 0;
        }
        return Math.abs(second);
    }

    /**
     * 比较时间，相差秒数
     **/
    public static long timeBetweenSeconds(LocalDateTime start, LocalDateTime end) {
        long second = Duration.between(start, end).getSeconds();
        if (0 == second) {
            return 0;
        }
        return Math.abs(second);
    }

    public static Period timeBetween(Instant instant1, Instant instant2) {

        LocalDate localDate = instant1.atZone(ZoneOffset.ofHours(0)).toLocalDate();
        LocalDate localDate1 = instant2.atZone(ZoneOffset.ofHours(0)).toLocalDate();

        return Period.between(localDate1, localDate);
    }

    //获得当天24点时间
    public static long getTimeEnd() {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().getTime();
    }

    //获得当天0点时间
    public static long getTimeBegin() {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().getTime();
    }

    //获得当天0点时间
    public static long getTodayTimeBegin() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Instant.now().toEpochMilli());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().getTime();
    }

    public static long getYesterdayTimeBegin() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Instant.now().toEpochMilli());
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().getTime();
    }

    public static String getThisMondayDid() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        Calendar cld = Calendar.getInstance();
        cld.setFirstDayOfWeek(Calendar.MONDAY);//以周一为首日
        cld.setTimeInMillis(System.currentTimeMillis());

        cld.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);//周一
        return df.format(cld.getTime());
    }

    public static String getLastMondayDid() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        Calendar cld = Calendar.getInstance();
        cld.add(Calendar.DAY_OF_WEEK, -7);
        cld.setFirstDayOfWeek(Calendar.MONDAY);//以周一为首日
//        cld.setTimeInMillis(System.currentTimeMillis());

        cld.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);//周一
        return df.format(cld.getTime());
    }

    public static LocalDateTime dayMaxTime(LocalDateTime now) {
        // 获取当天的结束时间（即23:59:59.999999999）
        LocalTime endOfDay = LocalTime.MAX;
        return LocalDateTime.of(now.toLocalDate(), endOfDay);
    }

    public static LocalDateTime dayMaxTime() {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        return dayMaxTime(now);
    }

    public static LocalDate getMonthLastDay() {
        LocalDate current = LocalDate.now(); // 当前日期

        int lastDayInCurrentMonth = current.lengthOfMonth(); // 当前月份的最大天数
        return current.withDayOfMonth(lastDayInCurrentMonth); // 设置为当前月份的最后一天
    }

    public static int getLastMonthDayDid() {
        LocalDate lastDayOfMonth = getMonthLastDay();
        String did = lastDayOfMonth.getYear()
                + appZeo(lastDayOfMonth.getMonthValue())
                + appZeo(lastDayOfMonth.getDayOfMonth());
        return Integer.parseInt(did);
    }

    private static String appZeo(int value) {
        return value < 10 ? "0" + value : value + "";
    }

    /**
     * 测试主方法
     *
     * @param args
     */
    public static void main(String args[]) throws ParseException {
//        // 计算时间差并输出结果（单位为秒）
//        Duration duration = Duration.between(now, end);
//        long secondsToEndOfDay = duration.getSeconds();
//        System.out.println("距离今天结束还有 " + secondsToEndOfDay + " 秒");

        LocalDate lastDayOfMonth = getMonthLastDay();
        LocalDate lastDayOfMonth1 = getMonthLastDay();
        System.out.println(lastDayOfMonth.equals(lastDayOfMonth1));
        System.out.println(getLastMonthDayDid());
    }
}
