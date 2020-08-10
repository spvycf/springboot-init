package com.project.genertor.domain;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 景点主表
 * </p>
 *
 * @author spv
 * @since 2019-08-06
 */

public class BaseScenicspot implements Serializable {

    public static final long serialVersionUID = 7580082343048106873L;

    public String id;

    /**
     * 省份
     */
    public String province;

    /**
     * 城市
     */
    public String city;

    /**
     * 区县
     */
    public String county;

    /**
     * 景点名称
     */
    @TableField("scenicSpotName")
    public String scenicSpotName;

    /**
     * 英文名称
     */
    @TableField("scenicSpotEnglishName")
    public String scenicSpotEnglishName;

    /**
     * 景点简码
     */
    @TableField("scenicSpotCode")
    public String scenicSpotCode;

    /**
     * 景点类型id
     */
    @TableField("scenicSpotTypeId")
    public String scenicSpotTypeId;

    @TableField("scenicSpotType")
    public String scenicSpotType;

    /**
     * 地址
     */
    public String address;

    /**
     * 经度
     */
    public String longitude;

    /**
     * 维度
     */
    public String dimension;

    @TableField("presentation")
    public String presentation;



    /**
     * 租金
     */
    @TableField("rentAmount")
    public BigDecimal rentAmount;

    /**
     * 押金
     */
    @TableField("depositAmount")
    public BigDecimal depositAmount;

    /**
     * 是否推荐0是1否
     */
    @TableField("isRecommend")
    public String isRecommend;

    /**
     * 营业时间
     */
    @TableField("shopHours")
    public String shopHours;

    /**
     * 客服电话
     */
    public String telephone;

    /**
     * 景点介绍
     */
    public String introduce;
    public String englishIntroduce;

    /**
     * 创建人
     */
    @TableField("createUser")
    public String createUser;

    /**
     * 创建时间
     */
    @TableField("createTime")
    public LocalDateTime createTime;

    /**
     * 最后修改人
     */
    @TableField("lastUpdateUser")
    public String lastUpdateUser;

    /**
     * 最后更新时间
     */
    @TableField("lastUpdateTime")
    public LocalDateTime lastUpdateTime;

    /**
     * 状态(0正常、1废弃)
     */
    public String status;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getScenicSpotName() {
        return scenicSpotName;
    }

    public void setScenicSpotName(String scenicSpotName) {
        this.scenicSpotName = scenicSpotName;
    }

    public String getScenicSpotEnglishName() {
        return scenicSpotEnglishName;
    }

    public void setScenicSpotEnglishName(String scenicSpotEnglishName) {
        this.scenicSpotEnglishName = scenicSpotEnglishName;
    }

    public String getScenicSpotCode() {
        return scenicSpotCode;
    }

    public void setScenicSpotCode(String scenicSpotCode) {
        this.scenicSpotCode = scenicSpotCode;
    }

    public String getScenicSpotTypeId() {
        return scenicSpotTypeId;
    }

    public void setScenicSpotTypeId(String scenicSpotTypeId) {
        this.scenicSpotTypeId = scenicSpotTypeId;
    }

    public String getScenicSpotType() {
        return scenicSpotType;
    }

    public void setScenicSpotType(String scenicSpotType) {
        this.scenicSpotType = scenicSpotType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public BigDecimal getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(BigDecimal rentAmount) {
        this.rentAmount = rentAmount;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getIsRecommend() {
        return isRecommend;
    }

    public void setIsRecommend(String isRecommend) {
        this.isRecommend = isRecommend;
    }

    public String getShopHours() {
        return shopHours;
    }

    public void setShopHours(String shopHours) {
        this.shopHours = shopHours;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getEnglishIntroduce() {
        return englishIntroduce;
    }

    public void setEnglishIntroduce(String englishIntroduce) {
        this.englishIntroduce = englishIntroduce;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
