package com.example;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;

/**
 * ユーザーエンティティ
 * Doma のサンプルエンティティクラス
 */
@Entity(immutable = true)
@Table(name = "users")
public class User {

    @Id
    private final Long id;
    
    private final String name;
    
    private final String email;
    
    /**
     * コンストラクタ
     */
    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}