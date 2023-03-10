package com.example.projectUav.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Uav implements Serializable {
    //无人机ID
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    //无人机名字
    @TableField
    private String uavName;
    
    //无人机种类
    @TableField
    private String uavKind;
    
    //无人机ip地址
    @TableField
    private String uavIp;
    
    //无人机端口号
    @TableField
    private Integer uavPort;
    
    //无人机坐标信息：X坐标
    @TableField
    private Integer LocationX;
    
    //无人机坐标信息：Y坐标
    @TableField
    private Integer LocationY;
    
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
