package com.talkman.saas.controller;

import com.talkman.saas.common.constant.CommonConstant;
import com.talkman.saas.common.exception.Result;
import com.talkman.saas.common.exception.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author doger.wang
 * @date 2020/8/7 10:02
 */
@Api(description = "test")
@RestController
@RequestMapping("/test")
public class testController {



    @ApiOperation("tt")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "guideMachineNo")
    })
    @GetMapping("/tt")
    public Result getUserCityManageInfo(@RequestHeader(CommonConstant.UId)String id, String userId) {
        System.out.println(id);
        String a=null;
        a.equals(1);
        int i= 1/0;
        //throw new ResultException(ResultCode.PARAMER_EXCEPTION,"11111");
        return ResultUtil.Success("获取成功", null);
    }

}
