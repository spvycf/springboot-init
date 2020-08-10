package com.xxl.job.admin.controller;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author doger.wang
 * @date 2020/1/3 15:44
 */
@Controller
@RequestMapping("/open")
public class JobOpenController {

    @Resource
    private XxlJobService xxlJobService;


    @RequestMapping("/add")
    @ResponseBody
    public ReturnT<String> add(XxlJobInfo jobInfo) {
        return xxlJobService.add(jobInfo);
    }

/*    @RequestMapping("/update")
    @ResponseBody
    public ReturnT<String> update(XxlJobInfo jobInfo) {
        return xxlJobService.update(jobInfo);
    }*/

    @RequestMapping("/remove")
    @ResponseBody
    public ReturnT<String> remove(int id) {
        return xxlJobService.remove(id);
    }
}
