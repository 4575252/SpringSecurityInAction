package com.iyyxx.token.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @className: User
 * @description: TODO 类描述
 * @author: eric 4575252@gmail.com
 * @date: 2022/10/12/0012 14:44:24
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_user")
public class User implements Serializable {
    @TableId
    private Long id;
    private String userName;
    private String nickName;
    private String password;
    private String status; //0正常 1停用
    private String email;
    private String phoneNumber;
    private String sex;//0男，1女，2未知
    private String avatar;  //头像
    private String userType;    //0管理员，1普通用户
    private Long createBy;
    private Date createTime;
    private Long updateBy;
    private Date updateTime;
    private Integer delFlag; //0未删除，1已删除


}
