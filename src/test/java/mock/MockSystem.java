package mock;

import mockit.Mock;
import mockit.MockUp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MockSystem extends MockUp<System> {
    /**
     * mock系统时间
     *
     * @return 自定义的系统时间
     */
    @Mock
    public long currentTimeMillis() {
        return this.nowTime;
    }

    /**
     * 系统时间
     */
    Long nowTime;

    /**
     * 让调用方自定义系统时间
     *
     * @param nowTimePattern 自定义系统时间带格式
     */
    public void setNowTime(String nowTimePattern) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        long nowTime = df.parse(nowTimePattern).getTime();

        this.nowTime = nowTime;
    }
}
