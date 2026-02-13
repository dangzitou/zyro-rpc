package io.dangzitou.example.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户实体
 * @author dangzitou
 * @date 2026/02/11
 */
@Data
public class User implements Serializable {
    private String name;
}
