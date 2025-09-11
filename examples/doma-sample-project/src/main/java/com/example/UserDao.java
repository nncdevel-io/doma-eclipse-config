package com.example;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

/**
 * ユーザーDAO
 * Doma のサンプル DAO インターフェース
 */
@Dao
@ConfigAutowireable
public interface UserDao {

    /**
     * 全ユーザーを取得
     */
    @Select
    List<User> selectAll();
    
    /**
     * IDでユーザーを取得
     */
    @Select
    User selectById(Long id);
    
    /**
     * ユーザーを挿入
     */
    @Insert
    int insert(User user);
    
    /**
     * 名前の部分一致でユーザーを検索
     */
    @Select
    List<User> selectByNameLike(String name);
}