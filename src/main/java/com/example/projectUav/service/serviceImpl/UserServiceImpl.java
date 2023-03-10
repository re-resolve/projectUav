package com.example.projectUav.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.projectUav.entity.User;
import com.example.projectUav.mapper.UserMapper;
import com.example.projectUav.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
