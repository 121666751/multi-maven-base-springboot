package com.multi.maven.dao.redis.bean;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis ZSet 对 score、value的封装
 * @since v3.0
 * @author yangsongbo
 *
 */
@Setter
@AllArgsConstructor
public class ZSetTuple implements Tuple{

    private RedisSerializer<String> stringSerializer = new StringRedisSerializer();

    private String value ;

    private Double score;


    public ZSetTuple(byte[] value , Double score){
        this.value = stringSerializer.deserialize(value);
        this.score = score;
    }

    @Override
    public int compareTo(Double o) {
        return score.compareTo(o);
    }


    @Override
    public byte[] getValue() {
        return stringSerializer.serialize(value);
    }

    public String getStringValue(){
        return value;
    }

    @Override
    public Double getScore() {

        return score;
    }


}