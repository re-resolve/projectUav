package com.example.projectUav.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.projectUav.common.Result;
import com.example.projectUav.entity.Uav;
import com.example.projectUav.service.UavService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("uav")
public class UavController {
    
    @Autowired
    private UavService uavService;
    
    /**
     * 录入一台无人机的信息
     *
     * @param uav
     * @return
     */
    @PostMapping("/createUav")
    public Result<String> createUav(@RequestBody Uav uav) {
        
        uavService.save(uav);
        
        return Result.success("成功创建一台新的无人机信息");
    }
    
    /**
     * 删除一台无人机的信息
     * @param id
     * @return
     */
    @DeleteMapping
    public Result<String> deleteUavById(Long id) {
        
        Uav uav = uavService.getById(id);
        
        if (uav != null) {
            String uav_name = uav.getUavName();
            
            uavService.removeById(id);
            
            return Result.success("成功删除名为：（" + uav_name + "） 的无人机");
            
        } else {
            return Result.error("要删除的无人机(id为 "+id.toString()+")不存在");
        }
    }
    
    /**
     * 修改一台无人机的信息
     * @param uav
     * @return
     */
    @PutMapping
    public Result<String> updateUav(@RequestBody Uav uav){
        
        uavService.updateById(uav);
        
        return Result.success("成功修改一台无人机的信息");
    }
    
    /**
     * 分页查询无人机信息
     * @param page
     * @param pageSize
     * @param port 无人机端口号（非必需）
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page ,int pageSize,String port){
        
        log.info("无人机信息分页查询：page = {},pageSize = {} ,port = {}", page, pageSize, port);
        
        //1.构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        
        //2.构造条件构造器
        LambdaQueryWrapper<Uav> queryWrapper = new LambdaQueryWrapper();
        
        //3.根据port做模糊查询
        // 添加过滤条件，使用StringUtils.isNotBlank()可判断空格也为空
        queryWrapper.like(StringUtils.isNotBlank(port),Uav::getUavPort,port);
        
        //4.添加排序条件
        queryWrapper.orderByDesc(Uav::getCreateTime);
        
        //执行查询
        uavService.page(pageInfo, queryWrapper);
        
        return Result.success(pageInfo);
    }
    
}
