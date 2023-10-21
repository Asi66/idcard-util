import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 身份证信息工具类
 *
 * @author asi
 * @date 2022/6/1 19:12
 */
public enum IdCardUtil {

    /**
     * 实例
     */
    X;

    /**
     * 身份证生日格式
     */
    private static final String BIRTHDAY_FORMAT = "yyyyMMdd";

    /**
     * 性别取余数
     */
    private static final int SEX_REMAINDER = 2;

    /**
     * 存储省市的map
     */
    private static ConcurrentMap<String, String> provinceCity = new ConcurrentHashMap<>();

    static {
        try (InputStream is = IdCardUtil.class.getResourceAsStream("/province-city.txt")) {
            assert is != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder buffer = new StringBuilder();
                String str;
                while ((str = reader.readLine()) != null) {
                    buffer.append(str).append("\n");
                }
                String p = buffer.toString();
                provinceCity = init(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化code和省市的map
     * @param data 行数据
     * @return 省市map
     */
    private static ConcurrentMap<String, String> init(String data) {
        ConcurrentMap<String, String> hm = new ConcurrentHashMap<>();
        if (StringUtils.isNotBlank(data)) {
            String[] dataSplit = data.split("\n");
            for (String d : dataSplit) {
                String[] dd = d.split("\t");
                if (dd.length > 1) {
                    hm.put(dd[0].trim(), dd[1].trim());
                }
            }
        }
        return hm;
    }

    /**
     * 获得省市名称
     * @param idCard 身份证号
     * @return 省市
     */
    public String getProvinceCity(String idCard) {
        String cityCode = idCard.substring(0, 4).trim();
        return Optional.ofNullable(provinceCity.get(cityCode)).orElse(StringUtils.EMPTY);
    }

    /**
     * 获得生日
     * @param idCard 身份证号
     * @return 生日
     */
    public LocalDate getBirthday(String idCard) {
        String birthdayCode = idCard.substring(6, 14);
        return LocalDate.parse(birthdayCode, DateTimeFormatter.ofPattern(BIRTHDAY_FORMAT));
    }

    /**
     * 获得性别
     * @param idCard 身份证号
     * @return 性别
     */
    public String getGender(String idCard) {
        String id17 = idCard.substring(16, 17);
        String gender = "男";
        if (Integer.parseInt(id17) % SEX_REMAINDER == 0) {
            gender = "女";
        }
        return gender;
    }

    public static void main(String[] args) {
        LocalDate birthday = IdCardUtil.X.getBirthday("110101199003076499");
        String provinceCity = IdCardUtil.X.getProvinceCity("110101199003076499");
        System.out.println("生日为:"+birthday+", 省市为:"+provinceCity);
    }

}
