package com.imooc.bilibili.dao;

import com.imooc.bilibili.domain.DanMu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 描述: TODO
 */
@Mapper
public interface DanMuDao {
    Integer addDanMu(DanMu danMu);

    List<DanMu> getDanMus(Map<String, Object> params);
}
