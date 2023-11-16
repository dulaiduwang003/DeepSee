package com.cn.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public enum OssEnum {

    //头像
    ALI_YUN("ALI_YUN"),
    //绘图
    LOCAL("LOCAL");

    String dec;
}
