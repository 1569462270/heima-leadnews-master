package com.heima.user;

import com.heima.feign.WemediaFeign;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.wemedia.entity.WmUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author : MR.wu
 * @Description : 测试
 * @Date : 2022/12/22 14:29
 * @Version : 1.0
 */
@SpringBootTest
public class FeignTests {

    @Autowired
    private WemediaFeign wemediaFeign;

    @Test
    public void findUserByName() {
        ResponseResult<WmUser> result = wemediaFeign.findByName("admin");
        System.out.println("result = " + result);
    }
}
