package com.example.projectUav.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.projectUav.entity.Uav;
import com.example.projectUav.mapper.UavMapper;
import com.example.projectUav.service.UavService;
import org.springframework.stereotype.Service;

@Service
public class UavServiceImpl extends ServiceImpl<UavMapper,Uav> implements UavService {
}
