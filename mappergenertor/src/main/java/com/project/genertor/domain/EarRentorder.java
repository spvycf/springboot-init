package com.project.genertor.domain;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;


/**
 * <p>
 * 
 * </p>
 *
 * @author dogerWang
 * @since 2020-08-03
 */

@TableName("t_ear_rentOrder")
public class EarRentorder implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("transactionNo")
    private String transactionNo;

    @TableField("orderId")
    private String orderId;

    @TableField("cabinetId")
    private String cabinetId;

    @TableField("earChannelId")
    private String earChannelId;

    @TableField("buyType")
    private String buyType;

    @TableField("rentType")
    private String rentType;

    private BigDecimal price;

    private Integer number;

    @TableField("changeNum")
    private Integer changeNum;

    private String status;

    @TableField("createTime")
    private LocalDateTime createTime;


}
